package cajero;

public class ServicioCajero {
    public static final double MINIMO_RETIRO = 10_000;
    public static final double LIMITE_DIARIO_POR_DEFECTO = 2_000_000;

    private final Banco banco;
    private final Cajero cajero;

    public ServicioCajero(Banco banco, Cajero cajero) {
        this.banco = banco;
        this.cajero = cajero;
    }

    public Tarjeta validarTarjeta(String numero) {
        Tarjeta tarjeta = banco.buscarTarjeta(numero);
        if (tarjeta == null) {
            return null;
        }
        return tarjeta.isValida() ? tarjeta : null;
    }

    public ResultadoOperacion validarRetiro(Cuenta cuenta, double monto) {
        if (monto <= 0) {
            return new ResultadoOperacion(false, "El monto debe ser mayor que cero.");
        }

        if (monto % MINIMO_RETIRO != 0) {
            return new ResultadoOperacion(false, "El retiro debe ser múltiplo de $10.000.");
        }

        if (!cuenta.tieneSaldo(monto)) {
            return new ResultadoOperacion(false, "Saldo insuficiente.");
        }

        if (!cuenta.respetaLimiteDiario(monto)) {
            return new ResultadoOperacion(false,
                    "Supera el límite diario de retiro de " + dinero(cuenta.getLimiteDiario()) + ".");
        }

        if (!cajero.tieneEfectivo(monto)) {
            return new ResultadoOperacion(false,
                    "El cajero no tiene suficiente efectivo disponible.");
        }

        return new ResultadoOperacion(true, "Retiro válido.");
    }

    /*
     * IMPORTANTE:
     * Este método es el único punto donde se modifican saldo, efectivo y retiro diario.
     * La interfaz primero valida y pide confirmación. Si el usuario cancela, este método
     * nunca se ejecuta y ningún dato financiero cambia.
     */
    public ResultadoOperacion ejecutarRetiro(Cuenta cuenta, double monto) {
        ResultadoOperacion validacion = validarRetiro(cuenta, monto);

        if (!validacion.isExitosa()) {
            return validacion;
        }

        cuenta.aplicarRetiro(monto);
        cajero.aplicarRetiro(monto);

        return new ResultadoOperacion(true,
                "Retiro completado. Entregue " + dinero(monto) + ".");
    }

    public ResultadoOperacion depositar(Cuenta cuenta, double monto) {
        if (monto <= 0) {
            return new ResultadoOperacion(false, "El monto debe ser mayor que cero.");
        }

        if (monto % MINIMO_RETIRO != 0) {
            return new ResultadoOperacion(false, "El monto debe ser múltiplo de $10.000.");
        }

        cuenta.aplicarDeposito(monto);
        return new ResultadoOperacion(true, "Depósito realizado correctamente.");
    }

    public static String dinero(double valor) {
        return String.format("$%,.0f", valor).replace(',', '.');
    }

    public Cajero getCajero() {
        return cajero;
    }
}
