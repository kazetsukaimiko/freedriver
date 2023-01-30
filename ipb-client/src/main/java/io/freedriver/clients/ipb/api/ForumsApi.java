package io.freedriver.clients.ipb.api;

import io.freedriver.clients.ipb.model.forums.Forum;
import io.freedriver.clients.ipb.model.request.ForumRequest;
import io.freedriver.clients.ipb.model.request.PaginatedRequest;
import io.freedriver.clients.ipb.model.response.PaginatedResponse;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/forums/forums")
public interface ForumsApi {

    @GET
    PaginatedResponse<Forum> getForums(@BeanParam PaginatedRequest<ForumRequest> forumRequest);

}
