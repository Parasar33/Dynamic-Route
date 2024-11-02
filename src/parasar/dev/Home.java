package parasar.dev;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
*  Front end of the application
* @author RISHAV
* @version 1.0
* @since 2024-09-05
*/
public class Home extends JFrame {

    private static final long serialVersionUID = 1L;

    private PathFinder mapPanel;
    private JTextArea timeTextArea;

    public Home() {
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

        JLabel titleLabel = new JLabel("ROUTE OPTIMISATION SIMULATOR");
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

        // Initialize the map panel with PathFinder from Board.java
        mapPanel = new PathFinder();
        mapPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        mapPanel.setBounds(2, 98, 1261, 625);

        // Add the mapPanel to the content pane
        getContentPane().add(mapPanel);

        JPanel updatesPanel = new JPanel();
        updatesPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        updatesPanel.setBounds(3, 725, 1259, 37);
        getContentPane().add(updatesPanel);
        updatesPanel.setLayout(new BorderLayout());

     // Create the JTextArea for displaying time
        timeTextArea = new JTextArea();
        timeTextArea.setEditable(false); // Make it read-only
        timeTextArea.setFont(new Font("Arial", Font.PLAIN, 16));

        // Set the initial text with solution status using mapPanel (the instance of PathFinder)
        timeTextArea.setText("Draw to Find Solution");

        // Add the JTextArea to the updatesPanel
        updatesPanel.add(timeTextArea, BorderLayout.CENTER);

     // Method to update the JTextArea with the solution status and time taken
        mapPanel.addTimeDisplayUpdater(() -> {
            long time = mapPanel.timeTaken(); // Call the timeTaken() method from the PathFinder instance
            // Update JTextArea with the solution status (noSolution()) and the time taken
            timeTextArea.setText(mapPanel.noSolution() + " - Time taken: " + time + " ms");
        });



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

        // Add action listeners for buttons
        startSimulation.addActionListener(e -> mapPanel.solvePath());
        resetSimulation.addActionListener(e -> {
            mapPanel.resetBoard();
            timeTextArea.setText("Time taken: 0 ms"); // Reset the time display when the board is reset
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Home frame = new Home();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
