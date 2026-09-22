package cajero;

import java.util.ArrayList;
import java.util.List;

public class Banco {
    private final List<Tarjeta> tarjetas = new ArrayList<>();

    public void agregarTarjeta(Tarjeta tarjeta) {
        tarjetas.add(tarjeta);
    }

    public Tarjeta buscarTarjeta(String numero) {
        for (Tarjeta tarjeta : tarjetas) {
            if (tarjeta.getNumero().equals(numero)) {
                return tarjeta;
            }
        }
        return null;
    }
}
