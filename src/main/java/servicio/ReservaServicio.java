
package servicio;

import entidad.Actividad;
import entidad.Cliente;
import entidad.Reserva;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;

public class ReservaServicio {

    private EntityManagerFactory emf;

    public ReservaServicio(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public ResultadoReserva crearReserva(Long clienteId, Long actividadId,
            int numeroPlazas) {

        if (clienteId == null || actividadId == null) {
            throw new IllegalArgumentException(
                "El ID del cliente y de la actividad son obligatorios."
            );
        }

        if (numeroPlazas <= 0) {
            throw new IllegalArgumentException(
                "El número de plazas debe ser positivo."
            );
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Cliente cliente = em.find(Cliente.class, clienteId);

            if (cliente == null) {
                throw new IllegalArgumentException(
                    "No existe un cliente con ese ID."
                );
            }

            Actividad actividad =
                em.find(Actividad.class, actividadId);

            if (actividad == null) {
                throw new IllegalArgumentException(
                    "No existe una actividad con ese ID."
                );
            }

            if (actividad.getPlazasDisponibles() < numeroPlazas) {
                throw new IllegalArgumentException(
                    "No hay suficientes plazas disponibles."
                );
            }

            actividad.setPlazasDisponibles(
                actividad.getPlazasDisponibles() - numeroPlazas
            );

            Reserva reserva = new Reserva(
                LocalDateTime.now(),
                numeroPlazas,
                actividad,
                cliente
            );

            em.persist(reserva);

            tx.commit();

            return new ResultadoReserva(
                reserva.getId(),
                actividad.getPlazasDisponibles()
            );

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }
    

    public int[] cancelarReserva(Long reservaId) {

        if (reservaId == null) {
            throw new IllegalArgumentException(
                "El ID de la reserva es obligatorio."
            );
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Reserva reserva = em.find(Reserva.class, reservaId);

            if (reserva == null) {
                throw new IllegalArgumentException(
                    "No existe una reserva con ese ID."
                );
            }

            Actividad actividad = reserva.getActividad();

            int plazasRecuperadas = reserva.getNumeroPlazas();

            actividad.setPlazasDisponibles(
                actividad.getPlazasDisponibles() + plazasRecuperadas
            );

            int plazasDisponibles = actividad.getPlazasDisponibles();

            em.remove(reserva);

            tx.commit();

            return new int[] {
                plazasRecuperadas,
                plazasDisponibles
            };

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

}
