
package servicio;

import entidad.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.Locale;
import java.util.regex.Pattern;

public class ClienteServicio {

    private EntityManagerFactory emf;

    private static final Pattern EMAIL_VALIDO =
        Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public ClienteServicio(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Long crearCliente(String nombre, String email) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El nombre no puede estar vacío."
            );
        }

        if (email == null) {
            throw new IllegalArgumentException(
                "El email es obligatorio."
            );
        }

        nombre = nombre.trim();
        email = email.trim().toLowerCase(Locale.ROOT);

        if (!EMAIL_VALIDO.matcher(email).matches()) {
            throw new IllegalArgumentException(
                "El email no tiene un formato válido."
            );
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            Long duplicados = em.createQuery(
                "SELECT COUNT(c) FROM Cliente c " +
                "WHERE LOWER(c.email) = :email",
                Long.class
            )
            .setParameter("email", email)
            .getSingleResult();

            if (duplicados > 0) {
                throw new IllegalArgumentException(
                    "Ya existe un cliente con ese email."
                );
            }

            tx.begin();

            Cliente cliente = new Cliente(nombre, email);
            em.persist(cliente);

            tx.commit();

            return cliente.getId();

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
