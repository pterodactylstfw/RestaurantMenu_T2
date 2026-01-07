package unitbv.mip.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import unitbv.mip.config.PersistenceManager;
import unitbv.mip.model.RestaurantTable;

import java.util.Optional;

public class RestaurantTableRepository {

    private EntityManager getEntityManager() {
        return PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    }

    public Optional<RestaurantTable> findByTableNumber(int number) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<RestaurantTable> query = em.createQuery(
                    "SELECT t FROM RestaurantTable t WHERE t.tableNumber = :num",
                    RestaurantTable.class);
            query.setParameter("num", number);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public RestaurantTable findOrCreate(int number) {
        Optional<RestaurantTable> existing = findByTableNumber(number);
        if (existing.isPresent()) {
            return existing.get();
        } else {
            RestaurantTable newTable = new RestaurantTable(number);
            EntityManager em = getEntityManager();
            try {
                em.getTransaction().begin();
                em.persist(newTable);
                em.getTransaction().commit();
                return newTable;
            } finally {
                em.close();
            }
        }
    }

    public void updateStatus(int tableNumber, boolean isOccupied) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            RestaurantTable table = em.createQuery("SELECT t FROM RestaurantTable t WHERE t.tableNumber = :num", RestaurantTable.class)
                    .setParameter("num", tableNumber)
                    .getResultStream().findFirst().orElse(null);

            if (table == null) {
                table = new RestaurantTable(tableNumber);
                em.persist(table);
            }

            table.setOccupied(isOccupied);
            em.merge(table);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public boolean isTableOccupied(int tableNumber) {
        return findByTableNumber(tableNumber).map(RestaurantTable::isOccupied).orElse(false);
    }
}