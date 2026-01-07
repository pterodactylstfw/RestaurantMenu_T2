package unitbv.mip.repository;

import jakarta.persistence.EntityManager;
import unitbv.mip.config.PersistenceManager;
import unitbv.mip.model.Order;
import unitbv.mip.model.User;

import java.util.List;

public class OrderRepository {

    private EntityManager getEntityManager(){
        return PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    public void saveOrder(Order order) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (order.getId() == null) {
                em.persist(order);
            } else {
                em.merge(order);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Eroare la salvarea comenzii", e);
        } finally {
            em.close();
        }
    }

    public List<Order> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT o FROM Order o", Order.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Order> findByWaiter(User waiter) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT o FROM Order o WHERE o.waiter = :waiter", Order.class)
                    .setParameter("waiter", waiter)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
