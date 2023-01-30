package io.freedriver.jpa.model.wordpress;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@Entity(name = "wp_wpforo_forums")
public class WpWpforoForum {
    @Id
    @Column(name = "forumid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    String title;
    @Column
    String slug;
    @Column
    String description;
    @Column
    Long parentid;
    @Column
    String icon;
    @Column
    Long cover;
    @Column(name = "last_topicid")
    Long lastTopicId;
    @Column(name = "last_postid")
    Long lastPostId;
    @Column(name = "last_userid")
    Long lastUserId;
    @Column(name = "last_post_date")
    LocalDateTime lastPostDate;
    @Column
    int topics;
    @Column
    int posts;
    @Column
    String permissions;
    @Column(name = "meta_key")
    String metaKey;
    @Column(name = "meta_desc")
    String metaDesc;
    @Column
    Integer status;
    @Column(name = "is_cat")
    Integer isCat;
    @Column
    Integer layout;
    @Column
    Integer order;
    @Column
    String color;


}

/*
mysql> describe wp_wpforo_forums;
        +----------------+---------------------+------+-----+---------------------+----------------+
        | Field          | Type                | Null | Key | Default             | Extra          |
        +----------------+---------------------+------+-----+---------------------+----------------+
        | forumid        | int(10) unsigned    | NO   | PRI | NULL                | auto_increment |
        | title          | varchar(255)        | NO   |     | NULL                |                |
        | slug           | varchar(255)        | NO   | UNI | NULL                |                |
        | description    | longtext            | YES  |     | NULL                |                |
        | parentid       | int(10) unsigned    | NO   | MUL | 0                   |                |
        | icon           | varchar(255)        | YES  |     | NULL                |                |
        | cover          | bigint(20) unsigned | NO   |     | 0                   |                |
        | cover_height   | int(4) unsigned     | NO   |     | 150                 |                |
        | last_topicid   | int(10) unsigned    | NO   |     | 0                   |                |
        | last_postid    | int(10) unsigned    | NO   | MUL | 0                   |                |
        | last_userid    | int(10) unsigned    | NO   |     | 0                   |                |
        | last_post_date | datetime            | NO   |     | 0000-00-00 00:00:00 |                |
        | topics         | int(11)             | NO   |     | 0                   |                |
        | posts          | int(11)             | NO   |     | 0                   |                |
        | permissions    | text                | YES  |     | NULL                |                |
        | meta_key       | text                | YES  |     | NULL                |                |
        | meta_desc      | text                | YES  |     | NULL                |                |
        | status         | tinyint(1) unsigned | NO   | MUL | 0                   |                |
        | is_cat         | tinyint(1) unsigned | NO   | MUL | 0                   |                |
        | layout         | tinyint(1) unsigned | NO   |     | 0                   |                |
        | order          | int(10) unsigned    | NO   | MUL | 0                   |                |
        | color          | varchar(7)          | NO   |     |                     |                |
        +----------------+---------------------+------+-----+---------------------+----------------+
        22 rows in set (0.00 sec)
*/
