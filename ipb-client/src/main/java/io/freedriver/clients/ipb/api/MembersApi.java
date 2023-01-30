package io.freedriver.clients.ipb.api;

import io.freedriver.clients.ipb.model.core.Member;
import io.freedriver.clients.ipb.model.request.MembersRequest;
import io.freedriver.clients.ipb.model.response.PaginatedResponse;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/core/members")
public interface MembersApi {

    @GET
    PaginatedResponse<Member> getMembers(@BeanParam MembersRequest membersRequest);

}
