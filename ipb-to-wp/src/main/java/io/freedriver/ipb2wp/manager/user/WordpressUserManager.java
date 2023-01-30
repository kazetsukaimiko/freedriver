package io.freedriver.ipb2wp.manager.user;

import io.freedriver.clients.ipb.model.core.Member;
import io.freedriver.ipb2wp.WpUserBundle;
import io.freedriver.ipb2wp.WpUserMetaKey;
import io.freedriver.ipb2wp.WpUsersMapper;
import io.freedriver.jpa.model.wordpress.WordpressEM;
import io.freedriver.jpa.model.wordpress.WpUserMeta;
import io.freedriver.jpa.model.wordpress.WpUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class WordpressUserManager {
    private final EntityManager em;

    public WordpressUserManager(EntityManager em) {
        this.em = em;
    }

    public WordpressUserManager() {
        this(WordpressEM.getEntityManager());
    }

    public WpUserBundle createIfNotExistsInWp(Member member) {
        WpUserBundle bundle = new WpUserBundle();

        // Lets find an existing user in wordpress with this email.
        Optional<WpUser> existing = getMemberWpUser(member);

        // If not present...
        if (existing.isEmpty()) {
            // Start a database transaction.
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            try {
                // Make a new wordpress wp_users record.
                WpUser newUser = WpUsersMapper.create(member);
                log.info("Persisting WpUser: " + newUser);
                em.persist(newUser);

                bundle.setUser(newUser);

                // Now make the mandatory metadata so the user can see their profile.
                List<WpUserMeta> metadata = WpUserMetaKey.stream()
                        .map(key -> persist(key.create(member, newUser)))
                        .collect(Collectors.toList());

                bundle.setMetadata(metadata);
                // If all went well, commit this user to db.
                tx.commit();
            } catch (Exception ex) {
                // If anything went wrong, log and roll back the transaction.
                ex.printStackTrace();
                tx.rollback();
            }
        } else {
            bundle.setUser(existing.get());
            bundle.setMetadata(getWpUserMeta(existing.get()));
        }
        return bundle;
    }

    private WpUserMeta persist(WpUserMeta wpUserMeta) {
        log.info("Persisting WpUserMeta: " + wpUserMeta);
        em.persist(wpUserMeta);
        return wpUserMeta;
    }

    private Optional<WpUser> getMemberWpUser(Member member) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WpUser> usersCriteriaQuery = cb.createQuery(WpUser.class);
        Root<WpUser> usersRoot = usersCriteriaQuery.from(WpUser.class);
        try {
            return Optional.of(em.createQuery(usersCriteriaQuery.select(usersRoot).where(cb.equal(usersRoot.get("email"), member.getEmail())))
                    .setMaxResults(1)
                    .getSingleResult());
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    public List<WpUser> getAllWpUsers() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WpUser> usersCriteriaQuery = cb.createQuery(WpUser.class);
        Root<WpUser> usersRoot = usersCriteriaQuery.from(WpUser.class);
        return em.createQuery(usersCriteriaQuery.select(usersRoot))
                .getResultList();
    }

    public List<WpUserMeta> getWpUserMeta(WpUser wpUser) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WpUserMeta> usersCriteriaQuery = cb.createQuery(WpUserMeta.class);
        Root<WpUserMeta> metaRoot = usersCriteriaQuery.from(WpUserMeta.class);
        return em.createQuery(usersCriteriaQuery.select(metaRoot).where(cb.equal(metaRoot.get("userId"), wpUser.getId())))
                .getResultList();
    }

    public WpUserBundle correctInvalidFields(WpUserBundle bundle) {
        // TODO
        return bundle;
    }
}
