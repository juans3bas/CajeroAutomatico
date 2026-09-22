package cajero;

public class DatosIniciales {

    public static Banco crearBanco() {
        Banco banco = new Banco();

        Cliente ana = new Cliente("Ana Torres");
        Cliente juan = new Cliente("Juan Pérez");
        Cliente carlos = new Cliente("Carlos Gómez");

        Cuenta cuentaAna = new Cuenta("1001", ana, 1_500_000, ServicioCajero.LIMITE_DIARIO_POR_DEFECTO);
        Cuenta cuentaJuan = new Cuenta("1002", juan, 3_000_000, ServicioCajero.LIMITE_DIARIO_POR_DEFECTO);
        Cuenta cuentaCarlos = new Cuenta("1003", carlos, 800_000, ServicioCajero.LIMITE_DIARIO_POR_DEFECTO);

        banco.agregarTarjeta(new Tarjeta("1111222233334444", "1234", cuentaAna));
        banco.agregarTarjeta(new Tarjeta("5555666677778888", "2468", cuentaJuan));
        banco.agregarTarjeta(new Tarjeta("9999000011112222", "4321", cuentaCarlos));

        return banco;
    }
}
