


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

public class AdminJob extends JFrame {

    public AdminJob() {
        setTitle("Admin Job");
        setSize(900, 700); // Increased size for 4K resolution
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create the button panel with GridBagLayout for better control
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // Increased padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Increased spacing between buttons
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make buttons expand horizontally
        gbc.weightx = 1.0; // Allow buttons to take up available space

        // Create buttons with larger font
        Font buttonFont = new Font("Serif", Font.BOLD, 24); // Increased font size
        JButton insertDetailsButton = new JButton("Insert Details");
        insertDetailsButton.setFont(buttonFont);
        JButton insertMarksButton = new JButton("Insert Marks");
        insertMarksButton.setFont(buttonFont);
        JButton updateDetailsButton = new JButton("Update Details");
        updateDetailsButton.setFont(buttonFont);
        JButton updateMarksButton = new JButton("Update Marks");
        updateMarksButton.setFont(buttonFont);
        JButton deleteDetailsButton = new JButton("Delete Details");
        deleteDetailsButton.setFont(buttonFont);
        JButton deleteMarksButton = new JButton("Delete Marks");
        deleteMarksButton.setFont(buttonFont);
        JButton showDetailsButton = new JButton("Show Details");
        showDetailsButton.setFont(buttonFont);
        JButton showMarksButton = new JButton("Show Marks");
        showMarksButton.setFont(buttonFont);
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(buttonFont);

        // Add action listeners for separate functionalities
        insertDetailsButton.addActionListener(e -> openInsertDetails());
        insertMarksButton.addActionListener(e -> openInsertMarks());
        updateDetailsButton.addActionListener(e -> openUpdateDetails());
        updateMarksButton.addActionListener(e -> openUpdateMarks());
        deleteDetailsButton.addActionListener(e -> openDeleteDetails());
        deleteMarksButton.addActionListener(e -> openDeleteMarks());
        showDetailsButton.addActionListener(e -> openShowDetails());
        showMarksButton.addActionListener(e -> openShowMarks());
        logoutButton.addActionListener(e -> logout());

        // Add buttons to the button panel using GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(insertDetailsButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        buttonPanel.add(insertMarksButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        buttonPanel.add(updateDetailsButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        buttonPanel.add(updateMarksButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        buttonPanel.add(deleteDetailsButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        buttonPanel.add(deleteMarksButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        buttonPanel.add(showDetailsButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        buttonPanel.add(showMarksButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        buttonPanel.add(logoutButton, gbc);

        // Create the title panel with a label
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204)); // Set a background color
        JLabel titleLabel = new JLabel("Result Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48)); // Increased font size
        titleLabel.setForeground(Color.WHITE); // Set text color
        titlePanel.add(titleLabel);

        // Add the title panel and button panel to the main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Add the main panel to the frame
        add(mainPanel);

        setVisible(true);
    }

    // Placeholder methods for each functionality
    private void openInsertDetails() {
        setVisible(false);
        new InsertDetailsForm();
    }

    private void openInsertMarks() {
        setVisible(false);
        new SemesterSelection();
    }

    private void openUpdateDetails() {
        setVisible(false);
        new UpdateStudentRecord();
    }

    private void openUpdateMarks() {
        setVisible(false);
        new UpdateMarks();
    }

    private void openDeleteDetails() {
        setVisible(false);
        new DeleteRecord();
    }

    private void openDeleteMarks() {
        setVisible(false);
        new DeleteMarks();
    }

    private void openShowDetails() {
        setVisible(false);
        new DisplayStudents();
    }

    private void openShowMarks() {
        setVisible(false);
        new MarksheetGenerator();
    }

    private void logout() {
        JOptionPane.showMessageDialog(this, "Logout successful.");
        dispose();
        new Home(); // Go back to the Home screen
    }

    // Main method to test AdminJob class independently
    public static void main(String[] args) {
         if (args.length > 0 && args[0].equals("test")) {
           
        SwingUtilities.invokeLater(() -> new AdminJob()); // This launches AdminJob directly for testing
    }
    }
}