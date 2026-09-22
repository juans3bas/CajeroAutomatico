package cajero;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Banco banco = DatosIniciales.crearBanco();

            // Simulación: el cajero inicia con $500.000.000.
            Cajero cajero = new Cajero(Cajero.CAPACIDAD_MAXIMA);

            ServicioCajero servicio = new ServicioCajero(banco, cajero);

            Admin admin = new Admin("admin", "admin123");

            InterfazCajero interfaz =
                    new InterfazCajero(banco, cajero, servicio, admin);

            interfaz.setVisible(true);
        });
    }
}
