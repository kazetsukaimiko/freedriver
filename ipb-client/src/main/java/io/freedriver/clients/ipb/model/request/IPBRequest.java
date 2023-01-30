package io.freedriver.clients.ipb.model.request;

import io.freedriver.clients.ipb.IPBKey;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

@Data
public class IPBRequest<R extends IPBRequest<R>> {
    @QueryParam("key")
    private String key = IPBKey.get().orElse(null);
}
