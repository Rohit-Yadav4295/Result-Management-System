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

public class DeleteMarks {
    private JFrame frame;
    private JTextField sidField;
    private JTextField semesterField;
    private JButton searchButton, deleteButton, backButton;
    private JTable dataTable;
    private Connection con;

    public DeleteMarks() {
        frame = new JFrame("Delete Marks Record");
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

        JLabel semesterLabel = new JLabel("Enter Semester:");
        semesterLabel.setFont(largerFont);
        semesterField = new JTextField(20);
        semesterField.setFont(largerFont);

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
        inputPanel.add(semesterField, gbc);

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
        searchButton.addActionListener(e -> searchMarks());
        deleteButton.addActionListener(e -> deleteMarks());
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

    private void searchMarks() {
        String sidText = sidField.getText().trim();
        String semesterText = semesterField.getText().trim();

        // Validation: Ensure SID and Semester are provided
        if (sidText.isEmpty() || semesterText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both SID and Semester.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int sid = Integer.parseInt(sidText); // Parse SID to an integer
            int semester = Integer.parseInt(semesterText); // Parse Semester to an integer

            String query = "SELECT * FROM marks WHERE sid = ? AND Semester = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, sid);
            ps.setInt(2, semester);

            ResultSet rs = ps.executeQuery();

            // Prepare table model to display results
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("SID");
            model.addColumn("Sub_ID");
            model.addColumn("Marks");
            model.addColumn("Semester");

            boolean recordFound = false;
            while (rs.next()) {
                recordFound = true;
                model.addRow(new Object[]{
                        rs.getInt("sid"),
                        rs.getInt("Sub_id"),
                        rs.getInt("Marks"),
                        rs.getInt("Semester")
                });
            }

            if (recordFound) {
                dataTable.setModel(model);
            } else {
                JOptionPane.showMessageDialog(frame, "No records found for SID: " + sid + " in Semester: " + semester, "Info", JOptionPane.INFORMATION_MESSAGE);
                dataTable.setModel(new DefaultTableModel()); // Clear table
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "SID and Semester must be numeric values.", "Warning", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error searching records!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteMarks() {
        int selectedRow = dataTable.getSelectedRow();

        // Ensure a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a record to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int sid = (int) dataTable.getValueAt(selectedRow, 0);
            int subId = (int) dataTable.getValueAt(selectedRow, 1);

            String query = "DELETE FROM marks WHERE sid = ? AND Sub_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, sid);
            ps.setInt(2, subId);

            int rowsDeleted = ps.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(frame, "Marks deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                ((DefaultTableModel) dataTable.getModel()).removeRow(selectedRow); // Remove row from table
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to delete the marks.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(frame, "Cannot delete this record because it is referenced in another table.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error deleting marks!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void goBack() {
        frame.setVisible(false); // Hide the current frame
        // Navigation logic can be added here
        new AdminJob(); // Assuming AdminJob is the previous screen
        frame.dispose(); // Release resources
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteMarks::new);
    }
}