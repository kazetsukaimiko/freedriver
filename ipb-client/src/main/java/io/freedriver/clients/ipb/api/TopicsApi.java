package io.freedriver.clients.ipb.api;

import io.freedriver.clients.ipb.model.forums.Topic;
import io.freedriver.clients.ipb.model.request.GetTopicRequest;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/forums/topics")
public interface TopicsApi {

    String ID_PARAM = "id";

    @GET
    @Path("{"+ID_PARAM+"}")
    Topic getTopicById(@BeanParam GetTopicRequest getTopicRequest);

}
