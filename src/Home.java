/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rohit
 */
import javax.swing.*;
import java.awt.*;

public class Home extends JFrame {

    public Home() {
        // Enable high-DPI scaling
        System.setProperty("sun.java2d.uiScale", "2.0"); // Adjust the scale factor as needed

        // Set the title of the window
        setTitle("Home Page");

        // Set the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the layout manager
        setLayout(new BorderLayout());

        // Create a panel for the top section (Image + Title)
        JPanel topPanel = new JPanel(new BorderLayout());

        // Load image correctly from resources
        ImageIcon imageIcon = null;

        try {
            imageIcon = new ImageIcon(getClass().getResource("/rmgmtsystem.jpg")); // Image must be inside src/

            // Scale the image to fit properly
            Image image = imageIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH); // Increased size for 4K
            imageIcon = new ImageIcon(image);

        } catch (Exception e) {
            System.out.println("Error: Image not found!");
        }

        // Create the image label
        JLabel imageLabel = (imageIcon != null) ? new JLabel(imageIcon, JLabel.CENTER) : new JLabel("Image Not Found", JLabel.CENTER);

        // Create a label for the title with bold and large font
        JLabel titleLabel = new JLabel("<html><h1><b>Result Management System</b></h1></html>", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 500)); // Increased font size for 4K

        // Add image and title to topPanel
        topPanel.add(imageLabel, BorderLayout.NORTH);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Create a panel for the button to control its size
        JPanel buttonPanel = new JPanel();
        
        // Create the Admin button (larger size for 4K)
        JButton adminButton = new JButton("Admin");
        adminButton.setPreferredSize(new Dimension(160, 60)); // Set larger size for 4K
        adminButton.setFont(new Font("Serif", Font.BOLD, 24)); // Increased font size for 4K

        // Add an ActionListener to the button
        adminButton.addActionListener(e -> {
            dispose(); // Close the current window
            new AdminHome(); // Open the AdminHome window
        });

        // Add the button to the panel
        buttonPanel.add(adminButton);

        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER); // Use buttonPanel instead of directly adding the button

        // Set the size of the frame
        setSize(1000, 800); // Increased size for 4K

        // Center the frame on the screen
        setLocationRelativeTo(null);

        // Make the frame visible
        setVisible(true);
    }

    public static void main(String[] args) {
        // Create the frame on the Event Dispatch Thread
        SwingUtilities.invokeLater(Home::new);
    }
}