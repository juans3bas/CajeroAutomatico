package cajero;

public class Admin {
    private final String usuario;
    private final String clave;

    public Admin(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public boolean autenticar(String usuarioIngresado, String claveIngresada) {
        return usuario.equals(usuarioIngresado) && clave.equals(claveIngresada);
    }
}
