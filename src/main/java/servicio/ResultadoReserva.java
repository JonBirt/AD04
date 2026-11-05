
package servicio;

public class ResultadoReserva {

    private Long idReserva;
    private int plazasRestantes;

    public ResultadoReserva(Long idReserva, int plazasRestantes) {
        this.idReserva = idReserva;
        this.plazasRestantes = plazasRestantes;
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public int getPlazasRestantes() {
        return plazasRestantes;
    }
}
