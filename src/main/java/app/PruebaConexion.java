
package app;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class PruebaConexion {

    public static void main(String[] args) {

        EntityManagerFactory emf = null;

        try {
            emf = Persistence.createEntityManagerFactory("ActivaPU");

            System.out.println("¡Conexión y configuración correctas!");

        } catch (Exception e) {
            System.out.println("Error al iniciar JPA o Hibernate:");
            e.printStackTrace();

        } finally {
            if (emf != null) {
                emf.close();
            }
        }
    }
}
