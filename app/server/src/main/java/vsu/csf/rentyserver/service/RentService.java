package vsu.csf.rentyserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vsu.csf.rentyserver.component.RentProcessor;
import vsu.csf.rentyserver.component.RentUpdaterScheduler;
import vsu.csf.rentyserver.exception.NoSuchElementException;
import vsu.csf.rentyserver.exception.NotAvailableSizeException;
import vsu.csf.rentyserver.exception.WrongRentStatusException;
import vsu.csf.rentyserver.model.dto.rent.request.CreateRentRequest;
import vsu.csf.rentyserver.model.dto.rent.request.CreateRentsBatchRequest;
import vsu.csf.rentyserver.model.dto.rent.request.ProlongRentRequest;
import vsu.csf.rentyserver.model.dto.rent.request.RentStatusFilter;
import vsu.csf.rentyserver.model.dto.rent.response.RentResponse;
import vsu.csf.rentyserver.model.entity.*;
import vsu.csf.rentyserver.model.entity.enumeration.RentStatus;
import vsu.csf.rentyserver.model.entity.id.SizeId;
import vsu.csf.rentyserver.model.mapping.RentMapper;
import vsu.csf.rentyserver.repository.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RentService {

    private final RentEventsRepository eventRepository;
    private final AppUsersRepository usersRepository;
    private final ProductsRepository productsRepository;
    private final SizesRepository sizesRepository;
    private final ReceiptsRepository receiptsRepository;

    private final RentProcessor rentProcessor;
    private final RentUpdaterScheduler rentUpdaterScheduler;

    private final RentMapper rentMapper;

    @Transactional(readOnly = true)
    public List<RentResponse> getAll(Long userId, RentStatusFilter statusFilter) {
        log.info("List {} rents for user {} called", statusFilter, userId);

        return rentMapper.map(eventRepository.findRentEventsByUser_userIdEqualsAndStatusIsIn(userId, statusFilter.getEvents()));
    }

    @Transactional(readOnly = true)
    public RentResponse getOne(Long rentId) {
        log.info("Get rent {} called", rentId);

        var rent = eventRepository.findById(rentId)
                .orElseThrow(() -> new NoSuchElementException("rent", RentEvent.class, rentId));

        return rentMapper.map(rent);
    }

    @Transactional(readOnly = true)
    public boolean owns(Long userId, Long rentId) {
        return eventRepository.findById(rentId)
                .orElseThrow(() -> new NoSuchElementException("rent", RentEvent.class, rentId))
                .getUser().getUserId().equals(userId);
    }

    public RentResponse create(Long userId, CreateRentRequest request) {

        var user = usersRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user", AppUser.class, userId));

        return create(user, request);
    }

    public List<RentResponse> create(Long userId, CreateRentsBatchRequest requests) {
        var user = usersRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user", AppUser.class, userId));

        List<RentResponse> events = new ArrayList<>();

        requests.rents().forEach((request -> events.add(create(user, request))));

        return events;
    }

    private RentResponse create(AppUser user, CreateRentRequest request) {

        log.info("Create rent {} by user {} called", request, user.getUserId());

        var product = productsRepository.findById(request.productId())
                .orElseThrow(() -> new NoSuchElementException("product", Product.class, request.productId()));

        var sizeId = new SizeId()
                .setName(request.sizeName())
                .setProduct(product);

        var size = sizesRepository.findById(sizeId)
                .orElseThrow(() -> new NoSuchElementException("size", Size.class, sizeId));

        Integer availableCount = rentProcessor.countOfAvailableAt(
                size, request.startTime(), request.endTime());

        if (availableCount < request.count()) {
            throw new NotAvailableSizeException(request.sizeName(), request.count(), availableCount);
        }

        var rent = new RentEvent()
                .setUser(user)
                .setStartTime(request.startTime())
                .setEndTime(request.endTime())
                .setProduct(product)
                .setSize(size)
                .setCount(request.count())
                .setPrice(product.getPrice());

        var saved = eventRepository.save(rent);

        rentUpdaterScheduler.addRentTrack(rent.getRentId(), rent.getStartTime(), rent.getEndTime());

        return rentMapper.map(saved);

    }

    public RentResponse finish(Long userId, Long employeeId, Long rentId) {
        return finish(userId, employeeId, List.of(rentId)).get(0);
    }

    public List<RentResponse> finish(Long userId, Long employeeId, List<Long> rentIds) {

        var employee = usersRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("employee", AppUser.class, employeeId));

        var user = usersRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user", AppUser.class, userId));

        Receipt receipt = new Receipt()
                .setUser(user)
                .setEmployee(employee);

        List<RentEvent> rents = new ArrayList<>();

        rentIds.forEach((rentId) -> rents.add(finish(receipt, rentId)));

        receipt.setRents(rents);

        receiptsRepository.save(receipt);

        log.info("Added {} item(s) to receipt {}", rentIds.size(), receipt.getReceiptId());

        return rentMapper.map(rents);
    }

    private RentEvent finish(Receipt receipt, Long rentId) {
        log.info("Finish rent {} to receipt {} called", rentId, receipt);

        var rent = eventRepository.findById(rentId)
                .orElseThrow(() -> new NoSuchElementException("rent", RentEvent.class, rentId));

        if (!(rent.getStatus() == RentStatus.ONGOING || rent.getStatus() == RentStatus.EXPIRED)) {
            throw new WrongRentStatusException(rentId, rent.getStatus());
        }

        return rent
                .setFinishedAt(OffsetDateTime.now())
                .setReceipt(receipt)
                .setStatus(RentStatus.AWAITING_PAYMENT);
    }

    public RentResponse prolong(Long rentId, ProlongRentRequest request) {
        log.info("Prolong rent {} to time", request);

        var rent = eventRepository.findById(rentId)
                .orElseThrow(() -> new NoSuchElementException("rent", RentEvent.class, rentId));

        if (rent.getStatus() != RentStatus.ONGOING) {
            throw new WrongRentStatusException(rentId, rent.getStatus());
        }

        Integer availableCount = rentProcessor.countOfAvailableAt(
                rent.getSize(), rent.getStartTime(), request.endTime());

        if (availableCount < 0) { // because of curr rent counts too
            throw new NotAvailableSizeException(rent.getSize().getSizeId().getName(),
                    rent.getSize().getTotal(), availableCount);
        }

        rent.setEndTime(request.endTime());

        var changed = eventRepository.save(rent);

        rentUpdaterScheduler.onProlong(rentId, request.endTime());

        return rentMapper.map(changed);

    }

}
