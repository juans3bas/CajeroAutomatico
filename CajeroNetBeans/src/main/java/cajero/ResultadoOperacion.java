package cajero;

public class ResultadoOperacion {
    private final boolean exitosa;
    private final String mensaje;

    public ResultadoOperacion(boolean exitosa, String mensaje) {
        this.exitosa = exitosa;
        this.mensaje = mensaje;
    }

    public boolean isExitosa() {
        return exitosa;
    }

    public String getMensaje() {
        return mensaje;
    }
}
