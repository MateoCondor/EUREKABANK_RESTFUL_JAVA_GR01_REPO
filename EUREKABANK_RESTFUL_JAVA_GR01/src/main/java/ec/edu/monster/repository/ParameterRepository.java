package ec.edu.monster.repository;

import ec.edu.monster.model.Parameter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ParameterRepository {

    @PersistenceContext(unitName = "ec.edu.monster_EUREKABANK_RESTFUL_JAVA_GR01_war_1.0PU")
    private EntityManager entityManager;

    public void create(Parameter parameter) {
        entityManager.persist(parameter);
    }

    public Parameter update(Parameter parameter) {
        return entityManager.merge(parameter);
    }

    public Optional<Parameter> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Parameter.class, id));
    }

    public Optional<Parameter> findByKey(String key) {
        TypedQuery<Parameter> query = entityManager.createQuery(
                "SELECT p FROM Parameter p WHERE p.key = :key",
                Parameter.class
        );
        query.setParameter("key", key);
        query.setMaxResults(1);
        List<Parameter> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Parameter> findAll() {
        return entityManager.createQuery(
                "SELECT p FROM Parameter p ORDER BY p.key",
                Parameter.class
        ).getResultList();
    }
}
