package unitbv.mip.repository;

import jakarta.persistence.EntityManager;
import unitbv.mip.config.PersistenceManager;
import unitbv.mip.model.Product;

import java.util.List;
import java.util.Optional;

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
            product.setActive(true);
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
                    .createQuery("SELECT p FROM Product p WHERE p.active = true", Product.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public void delete(Product product) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Product managedProduct = em.merge(product);

            managedProduct.setActive(false);

            em.merge(managedProduct);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<Product> findByName(String name) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p WHERE LOWER(p.name) = :name AND p.active = true", Product.class)
                    .setParameter("name", name.toLowerCase())
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    public void updateProduct(Product product) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(product);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
