package io.freedriver.clients.ipb.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.freedriver.client.jackson.InstantEpochDeserializer;
import io.freedriver.client.jackson.InstantEpochSerializer;
import jakarta.ws.rs.QueryParam;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class MembersRequest extends SortedPaginatedRequest<MembersRequest> {
    @QueryParam("email")
    private String email;
    @QueryParam("group")
    private List<Integer> group;

    @QueryParam("activity_before")
    @JsonSerialize(using = InstantEpochSerializer.class)
    @JsonDeserialize(using = InstantEpochDeserializer.class)
    @JsonProperty("activity_before")
    private Instant activityBefore;

    @QueryParam("activity_after")
    @JsonSerialize(using = InstantEpochSerializer.class)
    @JsonDeserialize(using = InstantEpochDeserializer.class)
    @JsonProperty("activity_after")
    private Instant activityAfter;


}
