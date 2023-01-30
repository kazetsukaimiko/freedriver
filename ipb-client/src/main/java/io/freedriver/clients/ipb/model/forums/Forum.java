package io.freedriver.clients.ipb.model.forums;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Forum {
    private int id;
    private String name;
    private String path;
    private ForumType type;
    private int topics;
    private String url;
    private Integer parentId;

    Map<String, String> permissions;

}
