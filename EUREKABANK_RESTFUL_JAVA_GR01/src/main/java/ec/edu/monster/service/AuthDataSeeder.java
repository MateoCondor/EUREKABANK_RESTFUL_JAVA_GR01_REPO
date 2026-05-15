package ec.edu.monster.service;

import ec.edu.monster.model.User;
import ec.edu.monster.model.UserRole;
import ec.edu.monster.model.UserStatus;
import ec.edu.monster.security.PasswordUtil;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Singleton
@Startup
public class AuthDataSeeder {

    private static final String DEFAULT_USERNAME = "MONSTER";
    private static final String DEFAULT_PASSWORD = "MONSTER9";

    @PersistenceContext(unitName = "ec.edu.monster_EUREKABANK_RESTFUL_JAVA_GR01_war_1.0PU")
    private EntityManager entityManager;

    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void seedDefaultUser() {
        Long existingUsers = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                Long.class
        )
                .setParameter("username", DEFAULT_USERNAME)
                .getSingleResult();

        if (existingUsers != null && existingUsers > 0) {
            return;
        }

        User user = new User();
        user.setUsername(DEFAULT_USERNAME);
        user.setPassword(PasswordUtil.hashPassword(DEFAULT_PASSWORD));
        user.setRole(UserRole.ADMIN);
        user.setStatus(UserStatus.ACTIVE);

        entityManager.persist(user);
    }
}