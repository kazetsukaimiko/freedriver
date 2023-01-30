package io.freedriver.clients.ipb.model.request;

import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.WithBy;

@Getter
@Setter
@With
@WithBy
@Builder
@AllArgsConstructor
public class ForumRequest extends PaginatedRequest<ForumRequest> {
    @QueryParam("clubs")
    private Integer clubs;

}
