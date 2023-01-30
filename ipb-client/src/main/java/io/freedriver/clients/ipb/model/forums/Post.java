package io.freedriver.clients.ipb.model.forums;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.freedriver.clients.ipb.model.core.Member;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString

@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {
    private int id;
    @JsonProperty("item_id")
    private int itemId;
    private Member author;
    //private LocalDateTime date; // TODO
    private String content;
    private boolean hidden;
    private String url;
    private List<Integer> reactions;
}
