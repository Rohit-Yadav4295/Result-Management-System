/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rohit
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;

public class DeleteRecord {
    private JFrame frame;
    private JTextField sidField;
    private JComboBox<String> semesterComboBox;
    private JButton searchButton, deleteButton, backButton;
    private JTable dataTable;
    private Connection con;

    public DeleteRecord() {
        frame = new JFrame("Delete Record");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Set a modern look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding
        frame.add(mainPanel);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Set a larger font for better readability on 4K screens
        Font largerFont = new Font("SansSerif", Font.PLAIN, 18);

        JLabel sidLabel = new JLabel("Enter Student ID (SID):");
        sidLabel.setFont(largerFont);
        sidField = new JTextField(20);
        sidField.setFont(largerFont);

        JLabel semesterLabel = new JLabel("Select Semester:");
        semesterLabel.setFont(largerFont);
        semesterComboBox = new JComboBox<>(new String[]{
                "Select Semester", "1", "2", "3", "4", "5", "6", "7", "8"
        }); // Semester as strings
        semesterComboBox.setFont(largerFont);

        // Add components to the input panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(sidLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(sidField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(semesterLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(semesterComboBox, gbc);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Table to display data
        dataTable = new JTable();
        dataTable.setFont(largerFont);
        dataTable.setRowHeight(30); // Increase row height for better visibility
        JScrollPane scrollPane = new JScrollPane(dataTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        searchButton = new JButton("Search");
        searchButton.setFont(largerFont);
        searchButton.setPreferredSize(new Dimension(150, 40)); // Set button size

        deleteButton = new JButton("Delete");
        deleteButton.setFont(largerFont);
        deleteButton.setPreferredSize(new Dimension(150, 40)); // Set button size

        backButton = new JButton("Back");
        backButton.setFont(largerFont);
        backButton.setPreferredSize(new Dimension(150, 40)); // Set button size

        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        searchButton.addActionListener(e -> searchRecord());
        deleteButton.addActionListener(e -> deleteRecord());
        backButton.addActionListener(e -> goBack());

        // Connect to the database directly in the constructor
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        frame.setVisible(true);
    }

    private void searchRecord() {
        String sidText = sidField.getText().trim();
        String semester = (String) semesterComboBox.getSelectedItem();

        // Validation: Ensure fields are not empty or invalid
        if (sidText.isEmpty() || semester.equals("Select Semester")) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid SID and select a semester.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int sid = Integer.parseInt(sidText); // Parse SID to an integer

            String query = "SELECT * FROM students WHERE sid = ? AND semester = ?";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, sid);
            ps.setString(2, semester);

            ResultSet rs = ps.executeQuery();

            // Prepare table model to display results
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("SID");
            model.addColumn("Name");
            model.addColumn("Course");
            model.addColumn("Address");
            model.addColumn("Contact");
            model.addColumn("Semester");

            boolean recordFound = false;
            while (rs.next()) {
                recordFound = true;
                model.addRow(new Object[]{
                        rs.getInt("sid"),
                        rs.getString("name"),
                        rs.getString("course"),
                        rs.getString("address"),
                        rs.getString("contact"),
                        rs.getString("semester")
                });
            }

            if (recordFound) {
                dataTable.setModel(model);
            } else {
                JOptionPane.showMessageDialog(frame, "No record found for SID: " + sid + " and Semester: " + semester, "Info", JOptionPane.INFORMATION_MESSAGE);
                dataTable.setModel(new DefaultTableModel()); // Clear table
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "SID must be a numeric value.", "Warning", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error searching record!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteRecord() {
        String sidText = sidField.getText().trim();
        String semester = (String) semesterComboBox.getSelectedItem();

        // Validation: Ensure fields are not empty or invalid
        if (sidText.isEmpty() || semester.equals("Select Semester")) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid SID and select a semester.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if a record is displayed in the table
        if (dataTable.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(frame, "Please search and select a record to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            int sid = Integer.parseInt(sidText); // Parse SID to an integer

            // Confirm deletion with the user
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return; // User canceled the deletion
            }

            // Step 1: Delete related records from the `marks` table
            String deleteMarksQuery = "DELETE FROM marks WHERE sid = ?";
            PreparedStatement deleteMarksStmt = con.prepareStatement(deleteMarksQuery);
            deleteMarksStmt.setInt(1, sid);
            int marksDeleted = deleteMarksStmt.executeUpdate();

            // Step 2: Delete the record from the `students` table
            String deleteStudentQuery = "DELETE FROM students WHERE sid = ? AND semester = ?";
            PreparedStatement deleteStudentStmt = con.prepareStatement(deleteStudentQuery);
            deleteStudentStmt.setInt(1, sid);
            deleteStudentStmt.setString(2, semester);

            int studentsDeleted = deleteStudentStmt.executeUpdate();

            if (studentsDeleted > 0) {
                JOptionPane.showMessageDialog(frame, "Record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetFields(); // Automatically reset fields after deletion
                dataTable.setModel(new DefaultTableModel()); // Clear table
            } else {
                JOptionPane.showMessageDialog(frame, "No record found to delete for SID: " + sid + " and Semester: " + semester, "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "SID must be a numeric value.", "Warning", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace(); // Print full stack trace
            JOptionPane.showMessageDialog(frame, "Error deleting record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace
            JOptionPane.showMessageDialog(frame, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        // Reset the input fields to default values
        sidField.setText(""); // Clear SID field
        semesterComboBox.setSelectedIndex(0); // Reset combo box to "Select Semester"
    }

    private void goBack() {
        frame.setVisible(false); // Hide the current frame
        // Navigation logic can be added here (e.g., go to a main menu)
        new AdminJob();
        frame.dispose(); // Release resources
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteRecord::new);
    }
}