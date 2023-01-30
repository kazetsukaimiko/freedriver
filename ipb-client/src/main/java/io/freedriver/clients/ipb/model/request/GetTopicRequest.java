package io.freedriver.clients.ipb.model.request;

import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;

import static io.freedriver.clients.ipb.api.TopicsApi.ID_PARAM;

@RequiredArgsConstructor
public class GetTopicRequest extends IPBRequest<GetTopicRequest> {
    @PathParam(ID_PARAM)
    private final int topicId;
}
