package io.freedriver.ipb2wp.manager.forum;

import io.freedriver.client.RESTClient;
import io.freedriver.clients.ipb.GenetryForums;
import io.freedriver.clients.ipb.api.ForumsApi;
import io.freedriver.clients.ipb.model.forums.Forum;
import io.freedriver.clients.ipb.model.request.AutoPager;
import io.freedriver.clients.ipb.model.request.ForumRequest;

import java.util.List;
import java.util.stream.Collectors;

public class IPBForumsManager {

    private final ForumsApi forumsApi;

    public IPBForumsManager(ForumsApi forumsApi) {
        this.forumsApi = forumsApi;
    }

    public IPBForumsManager() {
        this(RESTClient.create(ForumsApi.class, GenetryForums.URI));
    }

    public List<Forum> getAllForums() {
        ForumRequest request = ForumRequest.builder().build();
        return AutoPager.stream(request::withPage, forumsApi::getForums)
                .collect(Collectors.toList());
    }
}
