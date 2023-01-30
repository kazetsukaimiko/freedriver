package io.freedriver.ipb2wp;

import io.freedriver.jpa.model.wordpress.WpUserMeta;
import io.freedriver.jpa.model.wordpress.WpUser;
import lombok.Data;

import java.util.List;

@Data
public class WpUserBundle {
    private WpUser user;
    private List<WpUserMeta> metadata;

}
