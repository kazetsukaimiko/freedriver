package io.freedriver.clients.ipb.model.request;

import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedRequest<PR extends PaginatedRequest<PR>> extends IPBRequest<PR> {
    @QueryParam("page")
    private Integer page;
    @QueryParam("perPage")
    private Integer perPage;
}
