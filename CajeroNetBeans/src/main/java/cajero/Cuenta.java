package cajero;

public class Cuenta {
    private final String numero;
    private final Cliente titular;
    private final double limiteDiario;
    private double saldo;
    private double retiradoHoy;

    public Cuenta(String numero, Cliente titular, double saldo, double limiteDiario) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldo;
        this.limiteDiario = limiteDiario;
    }

    public String getNumero() {
        return numero;
    }

    public Cliente getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public double getRetiradoHoy() {
        return retiradoHoy;
    }

    public double getLimiteDiario() {
        return limiteDiario;
    }

    public boolean tieneSaldo(double monto) {
        return saldo >= monto;
    }

    public boolean respetaLimiteDiario(double monto) {
        return retiradoHoy + monto <= limiteDiario;
    }

    // Estos cambios solo se invocan después de confirmar y completar la operación.
    public void aplicarRetiro(double monto) {
        saldo -= monto;
        retiradoHoy += monto;
    }

    public void aplicarDeposito(double monto) {
        saldo += monto;
    }
}
