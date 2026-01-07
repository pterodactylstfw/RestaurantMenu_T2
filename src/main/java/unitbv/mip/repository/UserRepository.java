package unitbv.mip.repository;

import jakarta.persistence.EntityManager;
import unitbv.mip.config.PersistenceManager;
import unitbv.mip.model.User;

import java.util.List;
import java.util.Optional;

public class UserRepository {
    private EntityManager getEntityManager(){
        return PersistenceManager
                .getInstance().
                getEntityManagerFactory().createEntityManager();
    }

    public Optional<User> findByUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            return Optional.ofNullable(user);
        } finally {
            em.close();
        }
    }

    public void save(User user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if(user.getId() == null)
                em.persist(user);
            else
                em.merge(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<User> findAllStaff() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class)
                    .setParameter("role", unitbv.mip.model.Role.STAFF)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(User user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User managedUser = em.merge(user);
            em.remove(managedUser);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
