package unitbv.mip.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class PersistenceManager {
    private static final PersistenceManager INSTANCE = new PersistenceManager();

    private final EntityManagerFactory emf;

    private PersistenceManager() {
        try {
            this.emf = Persistence.createEntityManagerFactory("RestaurantPU");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Eroare la inițializarea JPA");
        }
    }

    public static PersistenceManager getInstance() {
        return INSTANCE;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
