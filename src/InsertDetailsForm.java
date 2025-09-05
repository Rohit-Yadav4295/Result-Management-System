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
import java.sql.*;

public class InsertDetailsForm extends JFrame {
    private JTextField sidField, nameField, addressField, contactField;
    private JComboBox<Integer> semesterComboBox;

    public InsertDetailsForm() {
        setTitle("Insert Details");
        setSize(1200, 900);  // Increased size for 4K resolution
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with padding and background color
        JPanel mainPanel = new JPanel(new GridLayout(8, 2, 20, 20));  // Adjusted spacing
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));  // Padding
        mainPanel.setBackground(new Color(240, 240, 240));  // Light gray background

        // Define fonts
        Font labelFont = new Font("Arial", Font.BOLD, 24);  // Larger font for labels
        Font fieldFont = new Font("Arial", Font.PLAIN, 22);  // Larger font for fields
        Font buttonFont = new Font("Arial", Font.BOLD, 22);  // Larger font for buttons

        // Labels and Fields
        JLabel sidLabel = new JLabel("SID:");
        sidLabel.setFont(labelFont);
        sidField = new JTextField();
        sidField.setFont(fieldFont);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        nameField = new JTextField();
        nameField.setFont(fieldFont);

        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(labelFont);
        JLabel courseValueLabel = new JLabel("BCA");
        courseValueLabel.setFont(fieldFont);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(labelFont);
        addressField = new JTextField();
        addressField.setFont(fieldFont);

        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setFont(labelFont);
        contactField = new JTextField();
        contactField.setFont(fieldFont);

        JLabel semesterLabel = new JLabel("Semester:");
        semesterLabel.setFont(labelFont);
        semesterComboBox = new JComboBox<>();
        semesterComboBox.setFont(fieldFont);
        for (int i = 1; i <= 8; i++) {
            semesterComboBox.addItem(i);
        }

        // Buttons
        JButton saveButton = new JButton("Save");
        JButton backButton = new JButton("Back");
        saveButton.setFont(buttonFont);
        backButton.setFont(buttonFont);

        // Button styling
        saveButton.setBackground(new Color(0, 102, 204));  // Blue background
        saveButton.setForeground(Color.BLACK);  // White text
        backButton.setBackground(new Color(204, 0, 0));  // Red background
        backButton.setForeground(Color.BLACK);  // White text

        Dimension buttonSize = new Dimension(200, 60);  // Larger button size
        saveButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);

        // Save Button Action
        saveButton.addActionListener(e -> {
            if (!validateInputs()) return;

            int sid = Integer.parseInt(sidField.getText());
            String name = nameField.getText();
            String address = addressField.getText();
            String contact = contactField.getText();
            int semester = (int) semesterComboBox.getSelectedItem();

            if (saveToDatabase(sid, name, "BCA", address, contact, semester)) {
                JOptionPane.showMessageDialog(this, "Details Saved Successfully!");
                resetFields();
            }
        });

        // Back Button Action
        backButton.addActionListener(e -> {
            dispose();
            new AdminJob();
        });

        // Add Components to the main panel
        mainPanel.add(sidLabel); mainPanel.add(sidField);
        mainPanel.add(nameLabel); mainPanel.add(nameField);
        mainPanel.add(courseLabel); mainPanel.add(courseValueLabel);
        mainPanel.add(addressLabel); mainPanel.add(addressField);
        mainPanel.add(contactLabel); mainPanel.add(contactField);
        mainPanel.add(semesterLabel); mainPanel.add(semesterComboBox);
        mainPanel.add(backButton); mainPanel.add(saveButton);

        // Add the main panel to the frame
        add(mainPanel);

        setVisible(true);
    }

    // Input validation method
    private boolean validateInputs() {
        String sidText = sidField.getText().trim();
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String contact = contactField.getText().trim();

        if (sidText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "SID cannot be empty.");
            return false;
        }
        try {
            Integer.parseInt(sidText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "SID must be a numeric value.");
            return false;
        }
        if (name.isEmpty() || address.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return false;
        }
        if (!contact.matches("[+\\d-]+") || contact.length() > 20) {
            JOptionPane.showMessageDialog(this, "Invalid Contact.");
            return false;
        }
        return true;
    }

    // Reset fields method
    private void resetFields() {
        sidField.setText("");
        nameField.setText("");
        addressField.setText("");
        contactField.setText("");
        semesterComboBox.setSelectedIndex(0);
    }

    // Method to save details to the database
    private boolean saveToDatabase(int sid, String name, String course, String address, String contact, int semester) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");

            // Check for duplication
            String checkQuery = "SELECT COUNT(*) FROM students WHERE sid = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setInt(1, sid);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "SID already exists.");
                return false;
            }

            // Insert data
            String query = "INSERT INTO students(sid, name, course, address, contact, semester) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, sid);
            pstmt.setString(2, name);
            pstmt.setString(3, course);
            pstmt.setString(4, address);
            pstmt.setString(5, contact);
            pstmt.setInt(6, semester);
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InsertDetailsForm::new);
    }
}