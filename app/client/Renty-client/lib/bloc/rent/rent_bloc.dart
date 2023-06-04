import 'package:client/api/dto/request/rent/extend_rent_event.dart';
import 'package:client/api/dto/request/rent/finish_rent.dart';
import 'package:client/api/dto/request/rent/start_rent_event.dart';
import 'package:client/api/dto/response/rent.dart';
import 'package:client/api/dto/response/user/user.dart';
import 'package:client/api/repository/product_repository.dart';
import 'package:client/api/repository/rent_repository.dart';
import 'package:client/bloc/rent/rent_event.dart';
import 'package:client/bloc/rent/rent_state.dart';
import 'package:client/common/widgets/auxiliary_wigets.dart';
import 'package:client/global.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class RentBloc extends Bloc<RentEvent, RentState> {
  final RentRepository _rentRepository = RentRepository();
  final ProductRepository _productRepository = ProductRepository();

  late List<RentResponse> myRents = [];

  RentBloc() : super(RentsLoadingState()) {
    on<MyRentsLoadEvent>(
      (event, emit) async {
        if (Global.storageService.isUserAuthenticated()) {
          myRents = await _rentRepository
              .getMyOngRents({"status_filter": "NOT_FINISHED"});
          emit(RentsLoadedState(rents: myRents));
        } else {
          emit(RentsUnAuthenticatedUserState());
        }
      },
    );

    // on<UserRentsLoadEvent>(
    //   (event, emit) async {
    //     myRents = await _rentRepository.getUserOngRents(event.userId);
    //     emit(RentsLoadedState(rents: myRents));
    //   },
    // );

    on<FinishRentsEvent>(
      (event, emit) async {
        UserRentsFinishRequest finishRequest =
            UserRentsFinishRequest(rentsId: event.rentsIds);
        await _rentRepository.finishUserRents(
            event.userId, finishRequest.toJson());
        // myRents = await _rentRepository
        //     .getUserOngRents(event.userId, {"status_filter": "ONGOING"});
        emit(UserRentsLoadedState(userId: event.userId, userRents: myRents));

        // emit(RentsLoadedState(rents: myRents));

        // myRents = await _rentRepository.getUserOngRents(event.userId);
        // emit(RentsLoadedState(rents: myRents));
      },
    );

    on<AddCartItemRentEvent>((event, emit) async {
      StartRentEventRequest startRentRequest = StartRentEventRequest(
        productId: event.productId,
        sizeName: event.sizeName,
        count: event.count,
        startTime: event.startTime,
        endTime: event.endTime,
      );
      // await _rentRepository.startRent(startRentRequest.toJson());
      // myRents = await _rentRepository.getMyOngRents();
      final product =
          await _productRepository.getProductPreviewById(event.productId);
      Global.cart.addRentItem(startRentRequest, product);
      // emit(RentsLoadedState(rents: myRents));
    });

    on<ProlongRentEvent>(
      (event, emit) async {
        ExtendRentEventRequest eventRequest =
            ExtendRentEventRequest(newEndTime: event.newEndTime);
        await _rentRepository.prolongRent(event.rentId, eventRequest.toJson());
        myRents = await _rentRepository
            .getMyOngRents({"status_filter": "NOT_FINISHED"});
        emit(RentsLoadedState(rents: myRents));
      },
    );

    on<SearchUserRentsEvent>(
      (event, emit) async {
        UserResponse? user;
        user = await _rentRepository.getUserData({"email": event.userEmail});
        if (user != null) {
          myRents = await _rentRepository
              .getUserOngRents(user.userId, {"status_filter": "ONGOING"});
          emit(UserRentsLoadedState(userId: user.userId, userRents: myRents));
        } else {
          emit(UnsuccessfulUserSearchState());
        }
      },
    );
  }
}
