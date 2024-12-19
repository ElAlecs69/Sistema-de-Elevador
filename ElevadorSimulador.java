package magia;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ElevadorSimulador extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int MAX_PISOS = 8;
    private static final int ANCHO = 800;
    private static final int ALTO = 600;

    private JPanel panelElevadores;
    private JButton btnAgregarUsuario;
    private JButton btnIniciarSimulacion;

    private Semaphore semaforo = new Semaphore(2); // Máximo dos elevadores moviéndose a la vez
    private int idUsuario = 1;
    private JLabel[] elevadores;
    private boolean ladoIzquierdo = true; // Alternar entre lado izquierdo y derecho

    
    public ElevadorSimulador() {
        setTitle("Simulador de Elevadores");
        setSize(ANCHO, ALTO);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Panel de control
        JPanel panelControl = new JPanel();
        btnAgregarUsuario = new JButton("Agregar Usuario");
        btnIniciarSimulacion = new JButton("Iniciar Simulación");
        JButton btnReiniciar = new JButton("Reiniciar Simulación"); // Nuevo botón
        panelControl.add(btnAgregarUsuario);
        panelControl.add(btnIniciarSimulacion);
        panelControl.add(btnReiniciar); // Agregar botón al panel
        add(panelControl, BorderLayout.NORTH);

        // Panel de elevadores
        panelElevadores = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.GRAY);
                // Dibujar líneas horizontales para los pisos
                for (int i = 0; i <= MAX_PISOS; i++) {
                    int y = ALTO - 150 - i * 50;
                    g.drawLine(0, y, ANCHO, y);
                }
            }
        };
        panelElevadores.setLayout(null);
        panelElevadores.setBackground(Color.LIGHT_GRAY);
        add(panelElevadores, BorderLayout.CENTER);

        // Listeners
        btnAgregarUsuario.addActionListener(e -> agregarUsuario());
        btnIniciarSimulacion.addActionListener(e -> iniciarSimulacion());
        btnReiniciar.addActionListener(e -> reiniciarSimulacion()); // Listener para reiniciar

        // Crear elevadores automáticamente
        crearElevadores();
        agregarPisos();
    }

    private void reiniciarSimulacion() {
        // Restablecer el estado inicial
        idUsuario = 1; // Reiniciar contador de usuarios
        ladoIzquierdo = true; // Resetear el lado inicial
        semaforo = new Semaphore(2); // Restablecer el semáforo

        // Limpiar el panel de elevadores
        panelElevadores.removeAll();
        panelElevadores.repaint();

        // Volver a crear elevadores y pisos
        crearElevadores();
        agregarPisos();

        // Habilitar botones
        btnAgregarUsuario.setEnabled(true);
        btnIniciarSimulacion.setEnabled(true);
    }

    private void crearElevadores() {
        elevadores = new JLabel[2];
        for (int i = 0; i < 2; i++) {
            JLabel elevador = new JLabel("E" + (i + 1));
            elevador.setOpaque(true);
            elevador.setBackground(Color.RED);
            elevador.setForeground(Color.WHITE);
            elevador.setBounds(100 + i * 300, ALTO - 150, 50, 50);
            elevador.putClientProperty("pisoActual", 0);
            elevador.setName("Elevador" + (i + 1));
            panelElevadores.add(elevador);
            elevadores[i] = elevador;
        }
    }

    private void agregarPisos() {
        for (int i = 0; i <= MAX_PISOS; i++) {
            JLabel piso = new JLabel("Piso " + i);
            piso.setBounds(10, ALTO - 150 - i * 50, 50, 20);
            panelElevadores.add(piso);
        }
    }

    private void agregarUsuario() {
    	
        int pisoUsuario = Integer.parseInt(JOptionPane.showInputDialog(this, "¿En qué piso está el usuario? (0-8):"));
        
        if(pisoUsuario < 0 || pisoUsuario > 8) {
        	JOptionPane.showMessageDialog(this, "Ingrese un piso válido", "Error", JOptionPane.ERROR_MESSAGE);
        	agregarUsuario();
        }
        
        int pisoDestino = Integer.parseInt(JOptionPane.showInputDialog(this, "¿A qué piso desea ir el usuario? (0-8):"));
        
        if(pisoDestino < 0 || pisoDestino > 8) {
        	JOptionPane.showMessageDialog(this, "Ingrese un piso válido", "Error", JOptionPane.ERROR_MESSAGE);
        }

        while (pisoDestino == pisoUsuario) {
            pisoDestino = Integer.parseInt(JOptionPane.showInputDialog(this, "El usuario ya está en ese piso, elige otro como destino"));
        }

        JLabel usuario = new JLabel("U" + idUsuario++);
        usuario.setOpaque(true);
        usuario.setBackground(Color.BLUE);
        usuario.setForeground(Color.WHITE);

        String lado = ladoIzquierdo ? "izquierdo" : "derecho";
        if (ladoIzquierdo) {
            usuario.setBounds(20, ALTO - 150 - pisoUsuario * 50, 30, 30);
        } else {
            usuario.setBounds(300, ALTO - 150 - pisoUsuario * 50, 30, 30);
        }

        ladoIzquierdo = !ladoIzquierdo;

        usuario.putClientProperty("pisoActual", pisoUsuario);
        usuario.putClientProperty("pisoDestino", pisoDestino);
        usuario.putClientProperty("lado", lado);
        panelElevadores.add(usuario);
        panelElevadores.repaint();
    }

    private void iniciarSimulacion() {
        btnAgregarUsuario.setEnabled(false);
        btnIniciarSimulacion.setEnabled(false);

        ArrayList<JLabel> usuarios = new ArrayList<>();
        for (Component comp : panelElevadores.getComponents()) {
            if (comp instanceof JLabel && ((JLabel) comp).getText().startsWith("U")) {
                usuarios.add((JLabel) comp);
            }
        }

        asignarElevadoresSecuencialmente(usuarios);
    }

    private void asignarElevadoresSecuencialmente(ArrayList<JLabel> usuarios) {
        new Thread(() -> {
            try {
                for (JLabel usuario : usuarios) {
                    int pisoUsuario = (int) usuario.getClientProperty("pisoActual");
                    int pisoDestino = (int) usuario.getClientProperty("pisoDestino");
                    String lado = (String) usuario.getClientProperty("lado");
                    asignarElevador(usuario, pisoUsuario, pisoDestino, lado);

                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void asignarElevador(JLabel usuario, int pisoUsuario, int pisoDestino, String lado) {
        new Thread(() -> {
            try {
                semaforo.acquire(); // Adquirir el permiso para usar un elevador

                JLabel elevadorDisponible = null;

                // Buscar elevador disponible
                synchronized (elevadores) {
                    for (JLabel elevador : elevadores) {
                        boolean esElevadorCorrecto = 
                            (lado.equals("izquierdo") && elevador.getName().equals("Elevador1")) ||
                            (lado.equals("derecho") && elevador.getName().equals("Elevador2"));

                        boolean ocupado = elevador.getClientProperty("ocupado") != null 
                            && (Boolean) elevador.getClientProperty("ocupado");

                        if (esElevadorCorrecto && !ocupado) {
                            elevadorDisponible = elevador;
                            break;
                        }
                    }
                }

                /*if (elevadorDisponible == null) {
                    JOptionPane.showMessageDialog(this, "No hay elevadores disponibles en el lado " + lado + ".");
                    semaforo.release();
                    return;
                }
*/
                final JLabel finalElevadorDisponible = elevadorDisponible;

                // Marcar el elevador como ocupado
                synchronized (elevadores) {
                    finalElevadorDisponible.putClientProperty("ocupado", true);
                }

                // 1. Mover el elevador al piso del usuario
                moverElevador(finalElevadorDisponible, pisoUsuario, null);

                // 2. Mover al usuario dentro del elevador
                int usuarioDentroX = finalElevadorDisponible.getX() + 10; // Ajustar posición X dentro del elevador
                moverUsuario(usuario, usuarioDentroX, calcularY(pisoUsuario));

                // 3. Mover el elevador al piso de destino junto con el usuario
                moverElevador(finalElevadorDisponible, pisoDestino, usuario);

                // 4. Mover al usuario fuera del elevador
                int usuarioFueraX = finalElevadorDisponible.getX() + (lado.equals("izquierdo") ? -50 : 50);
                moverUsuario(usuario, usuarioFueraX, calcularY(pisoDestino));

                // Marcar al usuario como fuera del elevador
                SwingUtilities.invokeLater(() -> usuario.setVisible(true));

                // Marcar el elevador como libre
                synchronized (elevadores) {
                    finalElevadorDisponible.putClientProperty("ocupado", false);
                }

                semaforo.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void moverElevador(JLabel elevador, int pisoObjetivo, JLabel usuario) throws InterruptedException {
        int pisoActual = (int) elevador.getClientProperty("pisoActual");
        int yObjetivo = calcularY(pisoObjetivo);

        Timer timer = new Timer(20, null);
        int[] deltaY = {yObjetivo - elevador.getY()}; // Cambios en la posición Y (mutable)

        timer.addActionListener(e -> {
            int nuevoY = elevador.getY() + (int) Math.signum(deltaY[0]) * Math.min(Math.abs(deltaY[0]), 2);

            // Mover el elevador
            elevador.setLocation(elevador.getX(), nuevoY);

            // Mover al usuario junto con el elevador
            if (usuario != null) {
                usuario.setLocation(elevador.getX() + 10, nuevoY);
            }

            // Actualizar delta
            deltaY[0] = yObjetivo - nuevoY;

            if (deltaY[0] == 0) {
                timer.stop();
            }
        });

        timer.start();
        while (timer.isRunning()) {
            Thread.sleep(10);
        }

        elevador.putClientProperty("pisoActual", pisoObjetivo);
    }


    private void moverUsuario(JLabel usuario, int destinoX, int destinoY) throws InterruptedException {
        Point inicio = usuario.getLocation();
        int[] delta = {destinoX - inicio.x, destinoY - inicio.y}; // Usar un arreglo para valores mutables

        Timer timer = new Timer(20, null);
        timer.addActionListener(e -> {
            int nuevoX = usuario.getX() + (int) Math.signum(delta[0]) * Math.min(Math.abs(delta[0]), 2);
            int nuevoY = usuario.getY() + (int) Math.signum(delta[1]) * Math.min(Math.abs(delta[1]), 2);

            usuario.setLocation(nuevoX, nuevoY);

            // Actualizar delta
            delta[0] = destinoX - nuevoX;
            delta[1] = destinoY - nuevoY;

            if (delta[0] == 0 && delta[1] == 0) {
                timer.stop();
            }
        });

        timer.start();
        while (timer.isRunning()) {
            Thread.sleep(10);
        }
    }


    private int calcularY(int piso) {
        return ALTO - 150 - piso * 50;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PortadaVentana portada = new PortadaVentana();
            portada.setVisible(true);
        });
    }

}
