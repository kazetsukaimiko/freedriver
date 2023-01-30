package io.freedriver.ipb2wp;

import io.freedriver.clients.ipb.model.core.Member;
import io.freedriver.ipb2wp.manager.user.IPBMembersManager;
import io.freedriver.ipb2wp.manager.user.WordpressUserManager;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        // Connects to WP database for user management.
        WordpressUserManager wpUsers = new WordpressUserManager();

        // Connects to IPB (or the database depending on implementation) for user management.
        IPBMembersManager ipbMembers = new IPBMembersManager();

        // Get all IPB members. Keep these in memory for reference.
        List<Member> allMembers = ipbMembers.getAllMembers();

        // Create them in Wordpress if they don't exist.
        // Also, fix any fields that are bad.
        List<WpUserBundle> bundles = allMembers.stream()
                .map(wpUsers::createIfNotExistsInWp)
                .map(wpUsers::correctInvalidFields)
                .collect(Collectors.toList());



    }
}
