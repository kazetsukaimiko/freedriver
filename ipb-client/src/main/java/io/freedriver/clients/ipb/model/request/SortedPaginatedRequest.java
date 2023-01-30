package io.freedriver.clients.ipb.model.request;

import jakarta.ws.rs.QueryParam;
import lombok.Data;

@Data
public class SortedPaginatedRequest<PR extends SortedPaginatedRequest<PR>> extends PaginatedRequest<PR> {
    @QueryParam("sortBy")
    private String sortBy;
    @QueryParam("sortDir")
    private SortDirection sortDir;
}
