package vsu.csf.rentyserver.model.dto.rent.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import vsu.csf.rentyserver.model.dto.catalog.response.ProductPreviewResponse;
import vsu.csf.rentyserver.model.dto.catalog.response.SizeResponse;
import vsu.csf.rentyserver.model.dto.user.response.UserResponse;
import vsu.csf.rentyserver.model.entity.enumeration.RentStatus;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RentResponse(

        @JsonProperty("rent_id")
        Long rentId,

        UserResponse user,

        @JsonProperty("start_time")
        OffsetDateTime startTime,

        @JsonProperty("end_time")
        OffsetDateTime endTime,

        @JsonProperty("finished_at")
        OffsetDateTime finishedAt,

        @JsonProperty("excepted_duration")
        Duration exceptedDuration,

        @JsonProperty("pretty_duration")
        String prettyDuration,

        ProductPreviewResponse product,

        SizeResponse size,

        Integer count,

        Integer price,

        @JsonProperty("receipt_id")
        UUID receiptId,

        RentStatus status

) {
}
