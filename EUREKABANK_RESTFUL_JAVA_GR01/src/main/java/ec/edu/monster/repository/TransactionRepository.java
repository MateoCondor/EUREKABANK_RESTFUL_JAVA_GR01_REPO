package ec.edu.monster.repository;

import ec.edu.monster.model.Transaction;
import ec.edu.monster.model.TransactionType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class TransactionRepository {

    @PersistenceContext(unitName = "ec.edu.monster_EUREKABANK_RESTFUL_JAVA_GR01_war_1.0PU")
    private EntityManager entityManager;

    public void create(Transaction transaction) {
        entityManager.persist(transaction);
    }

    public List<Transaction> findByAccountId(Long accountId) {
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.sourceAccount.id = :accountId "
                        + "OR t.targetAccount.id = :accountId ORDER BY t.date DESC",
                Transaction.class
        );
        return query.setParameter("accountId", accountId).getResultList();
    }

    /**
     * Returns the total amount operated by a source account for a given type
     * within a date range (used to enforce daily limits).
     */
    public BigDecimal sumDailyAmount(Long accountId, TransactionType type,
            LocalDateTime startOfDay, LocalDateTime endOfDay) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
                "SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t "
                        + "WHERE t.sourceAccount.id = :accountId "
                        + "AND t.type = :type "
                        + "AND t.date >= :startOfDay "
                        + "AND t.date < :endOfDay",
                BigDecimal.class
        );
        query.setParameter("accountId", accountId);
        query.setParameter("type", type);
        query.setParameter("startOfDay", startOfDay);
        query.setParameter("endOfDay", endOfDay);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
}

