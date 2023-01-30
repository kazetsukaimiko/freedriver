package io.freedriver.clients.ipb.model.core;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Member {

    private int id;
    private String name;

    String title;

    String timeZone;
    String formattedName;
    Group primaryGroup;
    List<Group> secondaryGroups;
    String email;
    LocalDateTime joined;
    String registrationIpAddress;
    Integer warningPoints;
    Integer reputationPoints;
    String photoUrl;
    boolean photoUrlIsDefault;
    String coverPhotoUrl;
    String profileUrl;
    boolean validating;
    int posts;
    LocalDateTime lastActivity;
    LocalDateTime lastVisit;
    LocalDateTime lastPost;
    int profileViews;
    String birthday;
    List<FieldGroup> customFields;
    List<Rank> rank;
    @JsonProperty("achievements_points")
    int achievementsPoints;
    boolean allowAdminEmails;
    boolean completed;



}
