package cajero;

public class Tarjeta {
    private final String numero;
    private final String pin;
    private final Cuenta cuenta;
    private boolean valida = true;
    private int intentosFallidos = 0;

    public Tarjeta(String numero, String pin, Cuenta cuenta) {
        this.numero = numero;
        this.pin = pin;
        this.cuenta = cuenta;
    }

    public String getNumero() {
        return numero;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public boolean isValida() {
        return valida;
    }

    public boolean verificarPin(String pinIngresado) {
        if (!valida) {
            return false;
        }

        if (pin.equals(pinIngresado)) {
            intentosFallidos = 0;
            return true;
        }

        intentosFallidos++;
        if (intentosFallidos >= 3) {
            valida = false;
        }
        return false;
    }

    public int getIntentosRestantes() {
        return Math.max(0, 3 - intentosFallidos);
    }

    public void desbloquear() {
        valida = true;
        intentosFallidos = 0;
    }
}
