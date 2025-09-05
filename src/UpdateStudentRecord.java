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
import javax.swing.border.EmptyBorder;

public class UpdateStudentRecord extends JFrame {

    public UpdateStudentRecord() {
        setTitle("Update Student Record");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800); // Adjusted size for 4K screen
        setLocationRelativeTo(null);

        // Set Nimbus Look and Feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding
        add(mainPanel);

        // Form panel with GridBagLayout for better control
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Set a larger font for better readability on 4K screens
        Font largerFont = new Font("SansSerif", Font.PLAIN, 24);

        // Initialize components
        JLabel sidLabel = new JLabel("Student ID:");
        sidLabel.setFont(largerFont);
        JTextField sidField = new JTextField(20);
        sidField.setFont(largerFont);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(largerFont);
        searchButton.setPreferredSize(new Dimension(200, 50)); // Set button size

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(largerFont);
        JTextField nameField = new JTextField(20);
        nameField.setFont(largerFont);
        nameField.setEditable(false);

        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(largerFont);
        JTextField courseField = new JTextField(20);
        courseField.setFont(largerFont);
        courseField.setEditable(false); // Course is non-editable

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(largerFont);
        JTextField addressField = new JTextField(20);
        addressField.setFont(largerFont);
        addressField.setEditable(false);

        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setFont(largerFont);
        JTextField contactField = new JTextField(20);
        contactField.setFont(largerFont);
        contactField.setEditable(false);

        JLabel semesterLabel = new JLabel("Semester:");
        semesterLabel.setFont(largerFont);
        JTextField semesterField = new JTextField(20);
        semesterField.setFont(largerFont);
        semesterField.setEditable(false); // Semester is non-editable

        JButton updateButton = new JButton("Update");
        updateButton.setFont(largerFont);
        updateButton.setPreferredSize(new Dimension(200, 50)); // Set button size
        updateButton.setEnabled(false);

        JButton backButton = new JButton("Back");
        backButton.setFont(largerFont);
        backButton.setPreferredSize(new Dimension(200, 50)); // Set button size

        // Add components to the form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(sidLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(sidField, gbc);

        gbc.gridx = 2;
        formPanel.add(searchButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(courseLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(courseField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(contactLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(contactField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(semesterLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(semesterField, gbc);

        // Add form panel to the main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);

        // Add button panel to the main panel
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Action Listener for Search Button
        searchButton.addActionListener(e -> {
            int sid;
            try {
                sid = Integer.parseInt(sidField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid Student ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");

                String query = "SELECT * FROM students WHERE sid = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, sid);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    courseField.setText(rs.getString("course"));
                    addressField.setText(rs.getString("address"));
                    contactField.setText(rs.getString("contact"));
                    semesterField.setText(rs.getString("semester"));

                    // Make fields editable except sid, course, and semester
                    nameField.setEditable(true);
                    courseField.setEditable(false); // Course is non-editable
                    addressField.setEditable(true);
                    contactField.setEditable(true);
                    semesterField.setEditable(false); // Semester is non-editable
                    updateButton.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Student not found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }

                con.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action Listener for Update Button
        updateButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String contact = contactField.getText().trim();

            // Validate fields
            if (name.isEmpty() || address.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int sid = Integer.parseInt(sidField.getText().trim());

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");

                String query = "UPDATE students SET name = ?, address = ?, contact = ? WHERE sid = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, address);
                pst.setString(3, contact);
                pst.setInt(4, sid);

                int rowsUpdated = pst.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Record updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    resetFields(sidField, nameField, courseField, addressField, contactField, semesterField, updateButton);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update record.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                con.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action Listener for Back Button
        backButton.addActionListener(e -> {
            setVisible(false);
            new AdminJob();
        });

        setVisible(true);
    }

    private void resetFields(JTextField sidField, JTextField nameField, JTextField courseField, JTextField addressField, JTextField contactField, JTextField semesterField, JButton updateButton) {
        sidField.setText("");
        nameField.setText("");
        courseField.setText("");
        addressField.setText("");
        contactField.setText("");
        semesterField.setText("");

        nameField.setEditable(false);
        courseField.setEditable(false);
        addressField.setEditable(false);
        contactField.setEditable(false);
        semesterField.setEditable(false);

        updateButton.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UpdateStudentRecord::new);
    }
}