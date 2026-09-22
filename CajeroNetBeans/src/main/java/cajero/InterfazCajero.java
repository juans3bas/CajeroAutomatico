package cajero;

import javax.swing.*;
import java.awt.*;

public class InterfazCajero extends JFrame {
    private final Banco banco;
    private final Cajero cajero;
    private final ServicioCajero servicio;
    private final Admin admin;

    private Tarjeta tarjetaActual;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel principal = new JPanel(cardLayout);

    private final JLabel lblEstado = new JLabel("Ingrese su tarjeta para comenzar.");

    public InterfazCajero(Banco banco, Cajero cajero, ServicioCajero servicio, Admin admin) {
        this.banco = banco;
        this.cajero = cajero;
        this.servicio = servicio;
        this.admin = admin;

        setTitle("Cajero Automático - Banco");
        setSize(720, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        principal.add(crearInicio(), "inicio");
        principal.add(crearMenu(), "menu");
        principal.add(crearAdmin(), "admin");

        add(principal);
        cardLayout.show(principal, "inicio");
    }

    private JPanel crearInicio() {
        JPanel panel = panelBase();

        JLabel titulo = new JLabel("CAJERO AUTOMÁTICO", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel instrucciones = new JLabel("Ingrese su número de tarjeta", SwingConstants.CENTER);
        JPasswordField txtTarjeta = new JPasswordField();
        txtTarjeta.setHorizontalAlignment(JTextField.CENTER);
        txtTarjeta.setFont(new Font("Arial", Font.PLAIN, 20));

        JButton btnIngresar = new JButton("Ingresar");
        JButton btnAdmin = new JButton("Administrador");

        btnIngresar.addActionListener(e -> {
            String numero = new String(txtTarjeta.getPassword()).trim();

            if (numero.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar una tarjeta.");
                return;
            }

            Tarjeta tarjeta = servicio.validarTarjeta(numero);

            if (tarjeta == null) {
                JOptionPane.showMessageDialog(this,
                        "La tarjeta no pertenece a este banco o está invalidada.",
                        "Tarjeta no válida",
                        JOptionPane.ERROR_MESSAGE);
                txtTarjeta.setText("");
                return;
            }

            String pin = solicitarPin(tarjeta);

            if (pin != null) {
                tarjetaActual = tarjeta;
                lblEstado.setText("Sesión iniciada: " + tarjeta.getCuenta().getTitular().getNombre());
                cardLayout.show(principal, "menu");
            }

            txtTarjeta.setText("");
        });

        btnAdmin.addActionListener(e -> mostrarLoginAdmin());

        JPanel centro = new JPanel(new GridLayout(5, 1, 10, 10));
        centro.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));
        centro.add(titulo);
        centro.add(instrucciones);
        centro.add(txtTarjeta);
        centro.add(btnIngresar);
        centro.add(btnAdmin);

        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }

    private String solicitarPin(Tarjeta tarjeta) {
        while (tarjeta.isValida() && tarjeta.getIntentosRestantes() > 0) {
            JPasswordField campoPin = new JPasswordField();
            campoPin.setHorizontalAlignment(JTextField.CENTER);

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    campoPin,
                    "Ingrese su PIN - intentos restantes: " + tarjeta.getIntentosRestantes(),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return null;
            }

            String pin = new String(campoPin.getPassword());

            if (tarjeta.verificarPin(pin)) {
                return pin;
            }

            if (!tarjeta.isValida()) {
                JOptionPane.showMessageDialog(this,
                        "Se agotaron los 3 intentos. La tarjeta ha sido invalidada.",
                        "Tarjeta invalidada",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            JOptionPane.showMessageDialog(this,
                    "PIN incorrecto. Intentos restantes: " + tarjeta.getIntentosRestantes(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }

    private JPanel crearMenu() {
        JPanel panel = panelBase();

        JLabel titulo = new JLabel("MENÚ PRINCIPAL", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 25));

        JButton retirar = new JButton("Retirar dinero");
        JButton depositar = new JButton("Depositar dinero");
        JButton cerrar = new JButton("Cerrar sesión");

        retirar.addActionListener(e -> mostrarRetiro());
        depositar.addActionListener(e -> mostrarDeposito());
        cerrar.addActionListener(e -> {
            tarjetaActual = null;
            lblEstado.setText("Ingrese su tarjeta para comenzar.");
            cardLayout.show(principal, "inicio");
        });

        JPanel botones = new JPanel(new GridLayout(4, 1, 10, 10));
        botones.setBorder(BorderFactory.createEmptyBorder(40, 160, 40, 160));
        botones.add(retirar);
        botones.add(depositar);
        botones.add(cerrar);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(lblEstado, BorderLayout.SOUTH);
        panel.add(botones, BorderLayout.CENTER);

        return panel;
    }

    private void mostrarRetiro() {
        Cuenta cuenta = tarjetaActual.getCuenta();

        JDialog dialogo = new JDialog(this, "Seleccionar monto de retiro", true);
        dialogo.setSize(500, 330);
        dialogo.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridLayout(3, 2, 12, 12));
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JButton b10 = new JButton("$10.000");
        JButton b20 = new JButton("$20.000");
        JButton b30 = new JButton("$30.000");
        JButton b100 = new JButton("$100.000");
        JButton otro = new JButton("Otro valor");
        JButton cancelar = new JButton("Cancelar");

        b10.addActionListener(e -> confirmarRetiro(10_000, dialogo));
        b20.addActionListener(e -> confirmarRetiro(20_000, dialogo));
        b30.addActionListener(e -> confirmarRetiro(30_000, dialogo));
        b100.addActionListener(e -> confirmarRetiro(100_000, dialogo));

        otro.addActionListener(e -> {
            String texto = JOptionPane.showInputDialog(dialogo,
                    "Ingrese un valor múltiplo de $10.000:");
            if (texto == null) return;

            try {
                double monto = Double.parseDouble(texto.replace(".", "").replace(",", ""));
                confirmarRetiro(monto, dialogo);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "Ingrese un número válido.");
            }
        });

        cancelar.addActionListener(e -> dialogo.dispose());

        p.add(b10);
        p.add(b20);
        p.add(b30);
        p.add(b100);
        p.add(otro);
        p.add(cancelar);

        dialogo.add(p);
        dialogo.setVisible(true);
    }

    private void confirmarRetiro(double monto, JDialog dialogo) {
        Cuenta cuenta = tarjetaActual.getCuenta();

        // Primera fase: solo validación. NO se modifica ningún dato.
        ResultadoOperacion validacion = servicio.validarRetiro(cuenta, monto);

        if (!validacion.isExitosa()) {
            JOptionPane.showMessageDialog(dialogo, validacion.getMensaje(),
                    "Retiro no realizado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                dialogo,
                "Va a retirar " + ServicioCajero.dinero(monto) + ".\n\n"
                        + "Saldo actual: " + ServicioCajero.dinero(cuenta.getSaldo()) + "\n"
                        + "Saldo después del retiro: " + ServicioCajero.dinero(cuenta.getSaldo() - monto)
                        + "\n\n¿Confirma la operación?",
                "Confirmar retiro",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(dialogo,
                    "Operación cancelada. No se modificó el saldo ni el efectivo del cajero.");
            return;
        }

        // Segunda fase: aquí sí se modifica la información.
        ResultadoOperacion resultado = servicio.ejecutarRetiro(cuenta, monto);

        JOptionPane.showMessageDialog(dialogo, resultado.getMensaje(),
                "Resultado", resultado.isExitosa()
                        ? JOptionPane.INFORMATION_MESSAGE
                        : JOptionPane.ERROR_MESSAGE);

        if (resultado.isExitosa()) {
            dialogo.dispose();

            if (cajero.getEfectivo() == 0) {
                JOptionPane.showMessageDialog(this,
                        "El cajero se quedó sin efectivo. Un administrador debe recargarlo.",
                        "Cajero sin dinero",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void mostrarDeposito() {
        String texto = JOptionPane.showInputDialog(this,
                "Ingrese el valor a depositar (múltiplo de $10.000):");

        if (texto == null) return;

        try {
            double monto = Double.parseDouble(texto.replace(".", "").replace(",", ""));

            if (monto <= 0 || monto % ServicioCajero.MINIMO_RETIRO != 0) {
                JOptionPane.showMessageDialog(this,
                        "El depósito debe ser positivo y múltiplo de $10.000.");
                return;
            }

            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Confirma el depósito de " + ServicioCajero.dinero(monto) + "?",
                    "Confirmar depósito",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmar == JOptionPane.YES_OPTION) {
                ResultadoOperacion resultado =
                        servicio.depositar(tarjetaActual.getCuenta(), monto);
                JOptionPane.showMessageDialog(this, resultado.getMensaje());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido.");
        }
    }

    private void mostrarLoginAdmin() {
        JTextField usuario = new JTextField();
        JPasswordField clave = new JPasswordField();

        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.add(new JLabel("Usuario:"));
        p.add(usuario);
        p.add(new JLabel("Contraseña:"));
        p.add(clave);

        int opcion = JOptionPane.showConfirmDialog(
                this, p, "Acceso de administrador",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion == JOptionPane.OK_OPTION &&
                admin.autenticar(usuario.getText(), new String(clave.getPassword()))) {
            cardLayout.show(principal, "admin");
        } else if (opcion == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas.",
                    "Administrador", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearAdmin() {
        JPanel panel = panelBase();

        JLabel titulo = new JLabel("PANEL DE ADMINISTRADOR", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 25));

        JLabel efectivo = new JLabel("", SwingConstants.CENTER);
        efectivo.setFont(new Font("Arial", Font.PLAIN, 18));

        JButton recargar = new JButton("Recargar efectivo");
        JButton desbloquear = new JButton("Desbloquear tarjeta");
        JButton salir = new JButton("Cerrar panel");

        recargar.addActionListener(e -> {
            String texto = JOptionPane.showInputDialog(this,
                    "Ingrese el monto de la recarga (simulación):");

            if (texto == null) return;

            try {
                double monto = Double.parseDouble(texto.replace(".", "").replace(",", ""));

                if (monto <= 0) {
                    JOptionPane.showMessageDialog(this, "El monto debe ser mayor que cero.");
                    return;
                }

                if (!cajero.puedeRecargar(monto)) {
                    JOptionPane.showMessageDialog(this,
                            "La recarga supera la capacidad máxima de $500.000.000.");
                    return;
                }

                int confirmar = JOptionPane.showConfirmDialog(
                        this,
                        "¿Confirmar recarga de " + ServicioCajero.dinero(monto) + "?",
                        "Confirmar recarga",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmar == JOptionPane.YES_OPTION) {
                    cajero.recargar(monto);
                    efectivo.setText("Efectivo disponible: " +
                            ServicioCajero.dinero(cajero.getEfectivo()));
                    JOptionPane.showMessageDialog(this, "Recarga realizada.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido.");
            }
        });

        desbloquear.addActionListener(e -> mostrarDesbloqueoTarjeta());

        salir.addActionListener(e -> cardLayout.show(principal, "inicio"));

        JPanel centro = new JPanel(new GridLayout(5, 1, 12, 12));
        centro.setBorder(BorderFactory.createEmptyBorder(60, 130, 60, 130));
        centro.add(titulo);
        centro.add(efectivo);
        centro.add(recargar);
        centro.add(desbloquear);
        centro.add(salir);

        panel.add(centro, BorderLayout.CENTER);

        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                efectivo.setText("Efectivo disponible: " +
                        ServicioCajero.dinero(cajero.getEfectivo()));
            }
        });

        efectivo.setText("Efectivo disponible: " +
                ServicioCajero.dinero(cajero.getEfectivo()));

        return panel;
    }

    private void mostrarDesbloqueoTarjeta() {
        JPasswordField campo = new JPasswordField();

        int opcion = JOptionPane.showConfirmDialog(
                this, campo, "Número de tarjeta a desbloquear",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) return;

        String numero = new String(campo.getPassword()).trim();
        if (numero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un número de tarjeta.");
            return;
        }

        Tarjeta tarjeta = banco.buscarTarjeta(numero);
        if (tarjeta == null) {
            JOptionPane.showMessageDialog(this, "La tarjeta no pertenece a este banco.",
                    "Tarjeta no encontrada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tarjeta.isValida()) {
            JOptionPane.showMessageDialog(this, "La tarjeta ya se encuentra desbloqueada.");
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Desea desbloquear la tarjeta?", "Confirmar desbloqueo",
                JOptionPane.YES_NO_OPTION);

        if (confirmar == JOptionPane.YES_OPTION) {
            tarjeta.desbloquear();
            JOptionPane.showMessageDialog(this,
                    "La tarjeta ha sido desbloqueada correctamente.");
        }
    }

    private JPanel panelBase() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return panel;
    }
}
