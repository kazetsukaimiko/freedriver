package io.freedriver.clients.ipb.model.response;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedResponse<T> {
    private int page;
    private int perPage;
    private int totalResults;
    private int totalPages;
    private List<T> results;
}
