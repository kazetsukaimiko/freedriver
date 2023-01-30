package io.freedriver.jpa.model.wordpress;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity(name = "wp_usermeta")
public class WpUserMeta {
    @Id
    @Column(name = "umeta_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "meta_key")
    private String key;

    @Column(name = "meta_value")
    private String value;

}

/*
        mysql> describe wp_usermeta;
        +------------+---------------------+------+-----+---------+----------------+
        | Field      | Type                | Null | Key | Default | Extra          |
        +------------+---------------------+------+-----+---------+----------------+
        | umeta_id   | bigint(20) unsigned | NO   | PRI | NULL    | auto_increment |
        | user_id    | bigint(20) unsigned | NO   | MUL | 0       |                |
        | meta_key   | varchar(255)        | YES  | MUL | NULL    |                |
        | meta_value | longtext            | YES  |     | NULL    |                |
        +------------+---------------------+------+-----+---------+----------------+
        4 rows in set (0.00 sec)
*/
