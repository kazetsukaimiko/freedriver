package io.freedriver.ipb2wp.manager.user;

import io.freedriver.client.RESTClient;
import io.freedriver.clients.ipb.GenetryForums;
import io.freedriver.clients.ipb.api.MembersApi;
import io.freedriver.clients.ipb.model.core.Member;
import io.freedriver.clients.ipb.model.request.MembersRequest;
import io.freedriver.clients.ipb.model.response.PaginatedResponse;

import java.util.List;

public class IPBMembersManager {
    private final MembersApi membersApi;

    public IPBMembersManager(MembersApi membersApi) {
        this.membersApi = membersApi;
    }

    public IPBMembersManager() {
        this(RESTClient.create(MembersApi.class, GenetryForums.URI));
    }

    public List<Member> getAllMembers() {
        MembersRequest membersRequest = new MembersRequest();
        membersRequest.setPerPage(200);

        // Low lets get the members from the API.
        PaginatedResponse<Member> memberPage = membersApi.getMembers(membersRequest);
        return memberPage.getResults();
    }
}
