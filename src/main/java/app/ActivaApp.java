
package app;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import entidad.Actividad;
import servicio.ActividadServicio;
import servicio.MonitorServicio;
import servicio.ClienteServicio;
import servicio.ReservaServicio;
import servicio.ResultadoReserva;
import servicio.ReservaServicio;
import servicio.ActividadServicio;

public class ActivaApp {

    public static void main(String[] args) {

        EntityManagerFactory emf = null;
        Scanner teclado = new Scanner(System.in);

        try {
            emf = Persistence.createEntityManagerFactory("ActivaPU");

            MonitorServicio monitorServicio =
                    new MonitorServicio(emf);

            ActividadServicio actividadServicio =
                    new ActividadServicio(emf);

            ClienteServicio clienteServicio =
                    new ClienteServicio(emf);
            
            ReservaServicio reservaServicio =
                    new ReservaServicio(emf);

            System.out.println("=== ACTIVA ===");
            System.out.println("1. Crear monitor");
            System.out.println("2. Crear actividad");
            System.out.println("3. Buscar actividades disponibles");
            System.out.println("4. Crear cliente");
            System.out.println("5. Crear reserva");
            System.out.println("6. Cancelar reserva");
            System.out.println("7. Listar actividades");
            System.out.print("Elige una opción: ");

            String opcion = teclado.nextLine().trim();

            switch (opcion) {
                case "1":
                    crearMonitor(teclado, monitorServicio);
                    break;

                case "2":
                    crearActividad(teclado, actividadServicio);
                    break;

                case "3":
                    buscarActividades(teclado, actividadServicio);
                    break;

                case "4":
                    crearCliente(teclado, clienteServicio);
                    break;
                    
                case "5":
                    crearReserva(teclado, reservaServicio);
                    break;
                    
                case "6":
                    cancelarReserva(teclado, reservaServicio);
                    break;
                   
                case "7":
                    listarActividades(actividadServicio);
                    break;

                default:
                    System.out.println("Opción no válida.");
            }

        } catch (Exception e) {
            System.out.println("No se ha podido ejecutar la operación.");
            e.printStackTrace();

        } finally {
            teclado.close();

            if (emf != null) {
                emf.close();
            }
        }
    }

    private static void crearMonitor(
            Scanner teclado, MonitorServicio servicio) {

        System.out.println("=== ACTIVA · Crear monitor ===");

        System.out.print("Nombre del monitor: ");
        String nombre = teclado.nextLine().trim();

        System.out.print("Especialidad del monitor: ");
        String especialidad = teclado.nextLine().trim();

        if (nombre.isEmpty() || especialidad.isEmpty()) {
            System.out.println(
                "Error: el nombre y la especialidad no pueden estar vacíos."
            );
            return;
        }

        Long id = servicio.crearMonitor(nombre, especialidad);

        System.out.println("Monitor creado correctamente. ID: " + id);
    }

    private static void crearActividad(
            Scanner teclado, ActividadServicio servicio) {

        System.out.println("=== ACTIVA · Crear actividad ===");

        System.out.print("Nombre de la actividad: ");
        String nombre = teclado.nextLine().trim();

        System.out.print("Localidad: ");
        String localidad = teclado.nextLine().trim();

        System.out.println(
            "Introduce la fecha y hora con formato dd/MM/yyyy HH:mm"
        );
        System.out.print("Fecha y hora: ");
        String textoFecha = teclado.nextLine().trim();

        System.out.print("Plazas totales: ");
        String textoPlazas = teclado.nextLine().trim();

        System.out.print("ID del monitor: ");
        String textoMonitor = teclado.nextLine().trim();

        if (nombre.isEmpty() || localidad.isEmpty()) {
            System.out.println(
                "Error: nombre y localidad son obligatorios."
            );
            return;
        }

        try {
            DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);

            LocalDateTime fechaHora =
                LocalDateTime.parse(textoFecha, formato);

            int plazas = Integer.parseInt(textoPlazas);
            Long monitorId = Long.parseLong(textoMonitor);

            Long id = servicio.crearActividad(
                nombre, localidad, fechaHora, plazas, monitorId
            );

            System.out.println(
                "Actividad creada correctamente. ID: " + id
            );

        } catch (DateTimeParseException e) {
            System.out.println(
                "Error: la fecha debe tener formato dd/MM/yyyy HH:mm."
            );

        } catch (NumberFormatException e) {
            System.out.println(
                "Error: las plazas y el ID del monitor deben ser números válidos."
            );

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void buscarActividades(
            Scanner teclado, ActividadServicio servicio) {

        System.out.println(
            "=== ACTIVA · Buscar actividades disponibles ==="
        );

        System.out.print("Localidad: ");
        String localidad = teclado.nextLine().trim();

        System.out.print("Día (dd/MM/yyyy): ");
        String textoDia = teclado.nextLine().trim();

        System.out.print("Número de plazas necesarias: ");
        String textoPlazas = teclado.nextLine().trim();

        if (localidad.isEmpty()) {
            System.out.println(
                "Error: la localidad no puede estar vacía."
            );
            return;
        }

        try {
            DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

            LocalDate dia = LocalDate.parse(textoDia, formato);
            int plazas = Integer.parseInt(textoPlazas);

            if (plazas <= 0) {
                System.out.println(
                    "Error: el número de plazas debe ser positivo."
                );
                return;
            }

            var actividades =
                servicio.buscarDisponibles(localidad, dia, plazas);

            if (actividades.isEmpty()) {
                System.out.println(
                    "No hay actividades disponibles que coincidan."
                );
                return;
            }

            for (Actividad actividad : actividades) {
                System.out.println("------------------------------");
                System.out.println("ID: " + actividad.getId());
                System.out.println("Nombre: " + actividad.getNombre());
                System.out.println(
                    "Fecha y hora: " + actividad.getFechaHora()
                );
                System.out.println(
                    "Monitor: " + actividad.getMonitor().getNombre()
                );
                System.out.println(
                    "Plazas disponibles: "
                    + actividad.getPlazasDisponibles()
                );
            }

        } catch (DateTimeParseException e) {
            System.out.println(
                "Error: el día debe tener formato dd/MM/yyyy."
            );

        } catch (NumberFormatException e) {
            System.out.println(
                "Error: las plazas deben ser un número entero."
            );

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());

        } catch (Exception e) {
            System.out.println("No se ha podido realizar la búsqueda.");
            e.printStackTrace();
        }
    }

    private static void crearCliente(
            Scanner teclado, ClienteServicio servicio) {

        System.out.println("=== ACTIVA · Crear cliente ===");

        System.out.print("Nombre del cliente: ");
        String nombre = teclado.nextLine().trim();

        System.out.print("Email del cliente: ");
        String email = teclado.nextLine().trim();

        try {
            Long id = servicio.crearCliente(nombre, email);

            System.out.println(
                "Cliente creado correctamente. ID: " + id
            );

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void crearReserva(
            Scanner teclado, ReservaServicio servicio) {

        System.out.println("=== ACTIVA · Crear reserva ===");

        System.out.print("ID del cliente: ");
        String textoCliente = teclado.nextLine().trim();

        System.out.print("ID de la actividad: ");
        String textoActividad = teclado.nextLine().trim();

        System.out.print("Número de plazas: ");
        String textoPlazas = teclado.nextLine().trim();

        try {
            Long clienteId = Long.parseLong(textoCliente);
            Long actividadId = Long.parseLong(textoActividad);
            int plazas = Integer.parseInt(textoPlazas);


			ResultadoReserva resultado = servicio.crearReserva(
			    clienteId, actividadId, plazas
			);
			
			System.out.println(
			    "Reserva creada correctamente. ID: "
			    + resultado.getIdReserva()
			);
			
			System.out.println(
			    "Plazas restantes: " + resultado.getPlazasRestantes()
			);
			

        } catch (NumberFormatException e) {
            System.out.println(
                "Error: los IDs y el número de plazas deben ser números válidos."
            );

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());

        } catch (Exception e) {
            System.out.println(
                "No se ha podido crear la reserva. No se ha confirmado la operación."
            );
        }
    }
    

    private static void cancelarReserva(
            Scanner teclado, ReservaServicio servicio) {

        System.out.println("=== ACTIVA · Cancelar reserva ===");

        System.out.print("ID de la reserva: ");
        String textoId = teclado.nextLine().trim();

        try {
            Long reservaId = Long.parseLong(textoId);

            int[] resultado = servicio.cancelarReserva(reservaId);

            System.out.println("Reserva cancelada correctamente.");
            System.out.println(
                "Plazas recuperadas: " + resultado[0]
            );
            System.out.println(
                "Plazas disponibles: " + resultado[1]
            );

        } catch (NumberFormatException e) {
            System.out.println(
                "Error: el ID de la reserva debe ser un número válido."
            );

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());

        } catch (Exception e) {
            System.out.println(
                "No se ha podido cancelar la reserva."
            );
        }
    }
    	

    private static void listarActividades(
            ActividadServicio servicio) {

        System.out.println("=== ACTIVA · Listar actividades ===");

        try {
            var actividades = servicio.listarTodas();

            if (actividades.isEmpty()) {
                System.out.println("No hay actividades registradas.");
                return;
            }

            for (Actividad actividad : actividades) {
                System.out.println("------------------------------");
                System.out.println("ID: " + actividad.getId());
                System.out.println("Nombre: " + actividad.getNombre());
                System.out.println("Localidad: " + actividad.getLocalidad());
                System.out.println(
                    "Fecha y hora: " + actividad.getFechaHora()
                );
                System.out.println(
                    "Monitor: " + actividad.getMonitor().getNombre()
                );
                System.out.println(
                    "Plazas totales: " + actividad.getPlazasTotales()
                );
                System.out.println(
                    "Plazas disponibles: "
                    + actividad.getPlazasDisponibles()
                );
            }

        } catch (Exception e) {
            System.out.println("No se han podido listar las actividades.");
            e.printStackTrace();
        }
    }


}
