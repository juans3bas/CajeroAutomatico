package cajero;

public class Cajero {
    public static final double CAPACIDAD_MAXIMA = 500_000_000;
    private double efectivo;

    public Cajero(double efectivoInicial) {
        if (efectivoInicial < 0 || efectivoInicial > CAPACIDAD_MAXIMA) {
            throw new IllegalArgumentException("Efectivo inicial fuera del límite.");
        }
        this.efectivo = efectivoInicial;
    }

    public double getEfectivo() {
        return efectivo;
    }

    public boolean tieneEfectivo(double monto) {
        return efectivo >= monto;
    }

    public boolean puedeRecargar(double monto) {
        return monto > 0 && efectivo + monto <= CAPACIDAD_MAXIMA;
    }

    public void aplicarRetiro(double monto) {
        efectivo -= monto;
    }

    public void recargar(double monto) {
        if (!puedeRecargar(monto)) {
            throw new IllegalArgumentException("La recarga supera la capacidad máxima.");
        }
        efectivo += monto;
    }
}
