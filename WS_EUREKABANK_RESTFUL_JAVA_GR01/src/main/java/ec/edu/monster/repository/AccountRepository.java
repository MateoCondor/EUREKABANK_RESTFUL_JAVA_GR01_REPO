package ec.edu.monster.repository;

import ec.edu.monster.model.Account;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AccountRepository {

    @PersistenceContext(unitName = "ec.edu.monster_WS_EUREKABANK_RESTFUL_JAVA_GR01_war_1.0PU")
    private EntityManager entityManager;

    public void create(Account account) {
        entityManager.persist(account);
    }

    public Account update(Account account) {
        return entityManager.merge(account);
    }

    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Account.class, id));
    }

    public List<Account> findAll() {
        return entityManager.createQuery(
                "SELECT a FROM Account a ORDER BY a.id",
                Account.class
        ).getResultList();
    }

    public List<Account> findByClientId(Long clientId) {
        TypedQuery<Account> query = entityManager.createQuery(
                "SELECT a FROM Account a WHERE a.client.id = :clientId ORDER BY a.id",
                Account.class
        );
        return query.setParameter("clientId", clientId).getResultList();
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        TypedQuery<Account> query = entityManager.createQuery(
                "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber",
                Account.class
        );
        query.setParameter("accountNumber", accountNumber);
        query.setMaxResults(1);
        List<Account> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }
}
