package io.freedriver.jpa.model.wordpress;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class EMTest {
    @Test
    public void testGetUsers() {
        EntityManager em = WordpressEM.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<WpUser> usersCriteriaQuery = cb.createQuery(WpUser.class);
        Root<WpUser> usersRoot = usersCriteriaQuery.from(WpUser.class);
        em.createQuery(usersCriteriaQuery.select(usersRoot))
                .getResultStream()
                .forEach(System.out::println);
    }


    @Test
    public void testGetForums() {
        EntityManager em = WordpressEM.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<WpWpforoForum> usersCriteriaQuery = cb.createQuery(WpWpforoForum.class);
        Root<WpWpforoForum> usersRoot = usersCriteriaQuery.from(WpWpforoForum.class);
        em.createQuery(usersCriteriaQuery.select(usersRoot))
                .getResultStream()
                .forEach(System.out::println);
    }


    /*
+----+------------+------------------------------------+---------------+-----------------------------+----------------------------------+---------------------+---------------------+-------------+--------------+
| ID | user_login | user_pass                          | user_nicename | user_email                  | user_url                         | user_registered     | user_activation_key | user_status | display_name |
+----+------------+------------------------------------+---------------+-----------------------------+----------------------------------+---------------------+---------------------+-------------+--------------+
|  1 | admin      | $P$BLDCqvWjApPEuitzq9a4EVE9IKPbFR. | admin-2-2-2-2 | kazetsukai@protonmail.com   | https://staging.genetrysolar.com | 2022-08-09 20:20:07 |                     |           0 | admin        |
|  3 | subscriber | $P$BHqFGBT3pXlnblxDMst.x.o1.idN2h/ | subscriber    | subscriber@genetrysolar.com |                                  | 2022-08-13 17:13:44 |                     |           0 | subscriber   |
|  4 | siddy      | $P$BxYeKKMNSxr49M0tyqef0.SgCbNJAd/ | siddy         | sid@genetrysolar.com        |                                  | 2022-08-16 00:58:10 |                     |           0 | siddy        |
+----+------------+------------------------------------+---------------+-----------------------------+----------------------------------+---------------------+---------------------+-------------+--------------+

     */
    @Test
    public void testCreateUser() {

        WpUser penguin = WpUser.builder()
                .login("penguin")
                .pass("$P$BxYeKKMNSxr49M0tyqef0.SgCbNJAd/")
                .niceName("penguin")
                .email("penguin@genetrysolar.com")
                .registered(LocalDateTime.now())
                .activationKey("")
                .url("")
                .status(0)
                .displayName("Penguin")
                .build();

        EntityManager em = WordpressEM.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(penguin);
        transaction.commit();

        System.out.println(penguin);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WpUser> usersCriteriaQuery = cb.createQuery(WpUser.class);
        Root<WpUser> usersRoot = usersCriteriaQuery.from(WpUser.class);
        em.createQuery(usersCriteriaQuery.select(usersRoot))
                .getResultStream()
                .forEach(System.out::println);


    }
}
