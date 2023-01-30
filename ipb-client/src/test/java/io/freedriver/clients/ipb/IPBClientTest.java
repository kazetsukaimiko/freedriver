package io.freedriver.clients.ipb;

import io.freedriver.client.RESTClient;
import io.freedriver.clients.ipb.api.ForumsApi;
import io.freedriver.clients.ipb.api.MembersApi;
import io.freedriver.clients.ipb.api.TopicsApi;
import io.freedriver.clients.ipb.model.core.Member;
import io.freedriver.clients.ipb.model.forums.Forum;
import io.freedriver.clients.ipb.model.forums.Topic;
import io.freedriver.clients.ipb.model.request.AutoPager;
import io.freedriver.clients.ipb.model.request.ForumRequest;
import io.freedriver.clients.ipb.model.request.GetTopicRequest;
import io.freedriver.clients.ipb.model.request.MembersRequest;
import io.freedriver.clients.ipb.model.response.PaginatedResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IPBClientTest {

    static final String DIVIDER = "###############################################################";

    @Test
    public void testGettingMembersFromGSForums() {
        // Get an object representing IPB's Members REST API.
        MembersApi membersApi = RESTClient.create(MembersApi.class, GenetryForums.URI);

        // Low lets get the members from the API.
        PaginatedResponse<Member> memberPage = membersApi.getMembers(new MembersRequest());

        // And print them.
        memberPage.getResults()
                .forEach(System.out::println);

    }

    @Test
    public void testGettingMyTopic() {
        int topicId = 520;
        TopicsApi topicsApi = RESTClient.create(TopicsApi.class, GenetryForums.URI);

        Topic myTopic = topicsApi.getTopicById(new GetTopicRequest(topicId));

        System.out.println("Topic author: \n " + myTopic.getFirstPost().getAuthor());
        System.out.println(DIVIDER);
        System.out.println(myTopic.getFirstPost().getContent());
        System.out.println(DIVIDER);

    }

    @Test
    public void testGettingAllForums() {
        ForumsApi forumsApi = RESTClient.create(ForumsApi.class, GenetryForums.URI);

        ForumRequest request = ForumRequest.builder().build();

        PaginatedResponse<Forum> singlePage = forumsApi.getForums(request);


        List<Forum> allForums = AutoPager.stream(request::withPage, forumsApi::getForums)
                .collect(Collectors.toList());

        allForums.forEach(System.out::println);
        assertEquals(singlePage.getTotalResults(), allForums.size());


    }


}
