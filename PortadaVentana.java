package magia;

import javax.swing.*;
import java.awt.*;

public class PortadaVentana extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PortadaVentana() {
		
        setTitle("Portada");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelTexto = new JPanel();
        panelTexto.setLayout(null);
        panelTexto.setSize(1000, 750);
        
        JLabel universidad = new JLabel("UNIVERSIDAD AUTÓNOMA del ESTADO de MÉXICO");
        universidad.setFont(new Font("Comic Sans MS", Font.BOLD, 33));
        universidad.setBounds(40, 100, 1050, 50);
        universidad.setForeground(Color.GREEN);
        
        JLabel facultad = new JLabel("FACULTAD de INGENIERÍA", JLabel.CENTER);
        facultad.setFont(new Font("Comic Sans MS", Font.ITALIC, 40));
        facultad.setBounds(-40, 200, 1050, 50);
        facultad.setForeground(Color.RED);
        
        JLabel examen = new JLabel("Proyecto FINAL", JLabel.CENTER);
        examen.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        examen.setBounds(-40, 300, 1050, 30);
        examen.setForeground(Color.DARK_GRAY);
        
        JLabel proyecto = new JLabel("SIMULADOR de ELEVADORES", JLabel.CENTER);
        proyecto.setFont(new Font("Comic Sans MS", Font.ROMAN_BASELINE, 50));
        proyecto.setBounds(-40, 390, 1050, 50);
        proyecto.setForeground(Color.BLUE);
        
        JLabel alumno = new JLabel("AUTOR: Alejandro Estrada Malvaez", JLabel.CENTER);
        alumno.setFont(new Font("Comic Sans MS", Font.LAYOUT_LEFT_TO_RIGHT, 30));
        alumno.setBounds(-40, 500, 1050, 45);
        
        panelTexto.add(universidad);
        panelTexto.add(facultad);
        panelTexto.add(examen);
        panelTexto.add(proyecto);
        panelTexto.add(alumno);
        
        JPanel panelBoton = new JPanel();
        panelBoton.setLayout(null);

        JButton btnIniciar = new JButton("Iniciar Simulación");
        btnIniciar.setFont(new Font("Comic Sans MS", Font.BOLD,25));
        btnIniciar.setBounds(335, 615, 300, 60);
        btnIniciar.addActionListener(e -> abrirSimulador());
        
        panelBoton.add(btnIniciar);

        add(panelTexto, BorderLayout.CENTER);
        add(panelBoton);
    }

    private void abrirSimulador() {
        // Abrir la ventana principal (simulador)
        ElevadorSimulador simulador = new ElevadorSimulador();
        simulador.setVisible(true);

        // Cerrar la ventana de portada
        dispose();
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PortadaVentana portada = new PortadaVentana();
            portada.setVisible(true);
        });
    }
}