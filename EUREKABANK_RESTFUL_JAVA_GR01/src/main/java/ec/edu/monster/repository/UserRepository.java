package ec.edu.monster.repository;

import ec.edu.monster.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository {

    @PersistenceContext(unitName = "ec.edu.monster_EUREKABANK_RESTFUL_JAVA_GR01_war_1.0PU")
    private EntityManager entityManager;

    public void create(User user) {
        entityManager.persist(user);
    }

    public Optional<User> findByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.username = :username",
                User.class
        );
        query.setParameter("username", username);
        query.setMaxResults(1);
        List<User> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public boolean existsByUsername(String username) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                Long.class
        ).setParameter("username", username).getSingleResult();
        return count != null && count > 0;
    }
}

