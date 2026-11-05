
package servicio;

import java.util.Optional;

import entidad.Actividad;
import entidad.Monitor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.TypedQuery;

public class ActividadServicio {

    private EntityManagerFactory emf;
    private MonitorServicio monitorServicio;

    public ActividadServicio(EntityManagerFactory emf) {
        this.emf = emf;
        this.monitorServicio = new MonitorServicio(emf);
    }

    public Long crearActividad(String nombre, String localidad,
                               java.time.LocalDateTime fechaHora,
                               int plazasTotales, Long monitorId) {

        if (plazasTotales <= 0) {
            throw new IllegalArgumentException(
                "Las plazas totales deben ser un entero positivo."
            );
        }

        Optional<Monitor> resultado = monitorServicio.buscarPorId(monitorId);

        if (!resultado.isPresent()) {
            throw new IllegalArgumentException(
                "No existe un monitor con el ID indicado."
            );
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Monitor monitor = em.find(Monitor.class, monitorId);

            Actividad actividad = new Actividad(
                nombre, localidad, fechaHora, plazasTotales, monitor
            );

            em.persist(actividad);
            tx.commit();

            return actividad.getId();

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    public List<Actividad> buscarDisponibles(
            String localidad, LocalDate dia, int plazasNecesarias) {

        if (localidad == null || localidad.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "La localidad no puede estar vacía."
            );
        }

        if (dia == null) {
            throw new IllegalArgumentException(
                "El día es obligatorio."
            );
        }

        if (plazasNecesarias <= 0) {
            throw new IllegalArgumentException(
                "El número de plazas debe ser positivo."
            );
        }

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Actividad> consulta = em.createQuery(
                "SELECT a FROM Actividad a " +
                "WHERE LOWER(a.localidad) = LOWER(:localidad) " +
                "AND a.fechaHora >= :inicio " +
                "AND a.fechaHora < :fin " +
                "AND a.plazasDisponibles >= :plazas " +
                "ORDER BY a.fechaHora ASC",
                Actividad.class
            );

            consulta.setParameter("localidad", localidad.trim());
            consulta.setParameter("inicio", dia.atStartOfDay());
            consulta.setParameter("fin", dia.plusDays(1).atStartOfDay());
            consulta.setParameter("plazas", plazasNecesarias);

            return consulta.getResultList();

        } finally {
            em.close();
        }
    }

    public List<Actividad> listarTodas() {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                "SELECT a FROM Actividad a " +
                "ORDER BY a.fechaHora ASC",
                Actividad.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

}
