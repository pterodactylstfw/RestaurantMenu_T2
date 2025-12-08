package unitbv.mip.repository;

import jakarta.persistence.EntityManager;
import unitbv.mip.config.PersistenceManager;
import unitbv.mip.model.Product;

import java.util.List;

public class ProductRepository {
    private EntityManager getEntityManager(){
        return PersistenceManager
                .getInstance().
                getEntityManagerFactory().createEntityManager();
    }

    public void addProduct(Product product) {
        EntityManager entityManager = getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(product);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public List<Product> getAllProducts() {
        EntityManager entityManager = getEntityManager();
        try {
            return entityManager
                    .createQuery("SELECT p FROM Product p", Product.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public Product findByName(String name) {
        EntityManager em = getEntityManager();
        try {
            // folosesc stream pentru a returna null dacă nu gasesc nimic la fel
            return em.createQuery("SELECT p FROM Product p WHERE p.name = :name", Product.class)
                    .setParameter("name", name)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }
}
