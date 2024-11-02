package code2;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/*
 *  Front end of the application
 * @author RISHAV
 * @version 2.0
 * @since 2024-09-20
 */
public class Home1 extends JFrame {

    private static final long serialVersionUID = 1L;

    private PathFinder mapPanel; // Keeping this as our main map panel
    private JTextArea timeTextArea; 

    public Home1() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        titlePanel.setBackground(new Color(192, 192, 192));
        titlePanel.setBounds(3, 3, 1258, 29);
        getContentPane().add(titlePanel);
        titlePanel.setLayout(null);

        JLabel titleLabel = new JLabel("ROUTE NAVIGATION OPTIMIZER");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Zig", Font.PLAIN, 30));
        titleLabel.setBounds(0, 0, 1280, 30);
        titlePanel.add(titleLabel);

        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        buttonsPanel.setBackground(new Color(192, 192, 192));
        buttonsPanel.setBounds(0, 29, 225, 69);
        getContentPane().add(buttonsPanel);
        buttonsPanel.setLayout(null);

        JPanel namesPanel = new JPanel();
        namesPanel.setBackground(new Color(192, 192, 192));
        namesPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        namesPanel.setBounds(227, 34, 1034, 63);
        getContentPane().add(namesPanel);
        namesPanel.setLayout(null);

        JTextArea namesArea = new JTextArea();
        namesArea.setFont(new Font("Zig", Font.PLAIN, 15));
        namesArea.setText(" Developed by, \r\n\r\n    32 Rishav Parasar   43 Tanishk Sonani   50 Shreesh Kulkarni   55 Aditya Mishra");
        namesArea.setEditable(false);
        namesArea.setBackground(new Color(192, 192, 192));
        namesArea.setBounds(3, 3, 1028, 57);
        namesArea.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        namesPanel.add(namesArea);
        JButton startSimulation = new JButton("START SIMULATION");
        startSimulation.setFont(new Font("Zig", Font.PLAIN, 15));
        startSimulation.setBackground(new Color(192, 192, 192));
        startSimulation.setBounds(3, 4, 219, 30);
        startSimulation.setBorder(new LineBorder(Color.BLACK, 2));
        buttonsPanel.add(startSimulation);

        JButton resetSimulation = new JButton("RESET SIMULATION");
        resetSimulation.setFont(new Font("Zig", Font.PLAIN, 15));
        resetSimulation.setBorder(new LineBorder(Color.BLACK, 2));
        resetSimulation.setBackground(Color.LIGHT_GRAY);
        resetSimulation.setBounds(3, 35, 219, 30);
        buttonsPanel.add(resetSimulation);

        // Initialize the map panel (PathFinder)
        mapPanel = new PathFinder(); // Use your existing PathFinder class
        mapPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        mapPanel.setBounds(2, 98, 1261, 625);
        getContentPane().add(mapPanel);

        JPanel updatesPanel = new JPanel();
        updatesPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        updatesPanel.setBounds(3, 725, 1259, 37);
        getContentPane().add(updatesPanel);
        updatesPanel.setLayout(new BorderLayout());

        timeTextArea = new JTextArea();
        timeTextArea.setEditable(false);
        timeTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        timeTextArea.setText("Draw to Find Solution");
        updatesPanel.add(timeTextArea, BorderLayout.CENTER);

        // Action listeners for buttons
        startSimulation.addActionListener(e -> {
            mapPanel.startRideSharingSimulation(); // Call the new method in PathFinder
            timeTextArea.setText("Simulation in progress...");
        });

        resetSimulation.addActionListener(e -> {
            mapPanel.reset(); // Call the reset method in PathFinder
            timeTextArea.setText("Simulation reset.");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Home1 frame = new Home1();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
