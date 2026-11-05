
package servicio;

import entidad.Monitor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.Optional;

public class MonitorServicio {

    private EntityManagerFactory emf;

    public MonitorServicio(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Long crearMonitor(String nombre, String especialidad) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Monitor monitor = new Monitor(nombre, especialidad);
            em.persist(monitor);

            tx.commit();

            return monitor.getId();

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }
    public Optional<Monitor> buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            Monitor monitor = em.find(Monitor.class, id);
            return Optional.ofNullable(monitor);
        } finally {
            em.close();
        }
    }
}

