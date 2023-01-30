package io.freedriver.clients.ipb.model.forums;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString

@JsonIgnoreProperties(ignoreUnknown = true)
public class Topic {
    private int id;
    private String title;
    private Forum forum;
    private int posts;
    private int views;
    private String prefix;
    private Post firstPost;
    private Post lastPost;
    private Post bestAnswer;
    private boolean locked;
    private boolean hidden;
    private boolean pinned;
    private boolean featured;
    private boolean archived;
    private Poll poll;
    private String url;
    private BigDecimal rating;
}
