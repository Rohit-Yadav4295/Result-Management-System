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

public class UpdateMarks {
    private JFrame frame;
    private JComboBox<String> semesterComboBox;
    private JTextField sidField;
    private JTable marksTable;
    private JButton searchButton, updateButton, backButton;
    private Connection con;

    public UpdateMarks() {
        frame = new JFrame("Update Marks");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Set Nimbus Look and Feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        frame.add(mainPanel);

        // Top panel for semester and SID input
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font largerFont = new Font("SansSerif", Font.PLAIN, 24);

        JLabel semesterLabel = new JLabel("Select Semester:");
        semesterLabel.setFont(largerFont);
        semesterComboBox = new JComboBox<>(new String[]{
            "Select Semester", "1", "2", "3", "4", "5", "6", "7", "8"
        });
        semesterComboBox.setFont(largerFont);

        JLabel sidLabel = new JLabel("Enter Student ID (SID):");
        sidLabel.setFont(largerFont);
        sidField = new JTextField(20);
        sidField.setFont(largerFont);

        searchButton = new JButton("Search");
        searchButton.setFont(largerFont);
        searchButton.setPreferredSize(new Dimension(200, 50));

        // Adding components to top panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(semesterLabel, gbc);

        gbc.gridx = 1;
        topPanel.add(semesterComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(sidLabel, gbc);

        gbc.gridx = 1;
        topPanel.add(sidField, gbc);

        gbc.gridx = 2;
        topPanel.add(searchButton, gbc);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Table for displaying marks
        marksTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only the Marks column is editable
            }
        };
        marksTable.setFont(largerFont);
        marksTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(marksTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel for Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        updateButton = new JButton("Update");
        updateButton.setFont(largerFont);
        updateButton.setPreferredSize(new Dimension(200, 50));

        backButton = new JButton("Back");
        backButton.setFont(largerFont);
        backButton.setPreferredSize(new Dimension(200, 50));

        bottomPanel.add(updateButton);
        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        searchButton.addActionListener(e -> fetchData());
        updateButton.addActionListener(e -> updateData());
        backButton.addActionListener(e -> {
            frame.setVisible(false);
            new AdminJob(); // Assuming AdminJob is another JFrame you are using for navigation
        });

        connectToDatabase();
        frame.setVisible(true);
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", ""); // Update credentials as needed
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void fetchData() {
        String semester = (String) semesterComboBox.getSelectedItem();
        String sid = sidField.getText().trim();

        if (semester.equals("Select Semester")) {
            JOptionPane.showMessageDialog(frame, "Please select a semester.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (sid.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid SID.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "SELECT * FROM marks WHERE sid = ? AND semester = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, sid);
            ps.setString(2, semester);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("SID");
            model.addColumn("Sub ID");
            model.addColumn("Semester");
            model.addColumn("Marks");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                model.addRow(new Object[]{
                    rs.getInt("sid"),
                    rs.getString("Sub_id"),
                    rs.getString("semester"),
                    rs.getString("Marks")
                });
            }

            marksTable.setModel(model);

            if (!hasData) {
                JOptionPane.showMessageDialog(frame, "No data found for SID: " + sid, "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error fetching data!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateData() {
        int selectedRow = marksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a row to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sid = marksTable.getValueAt(selectedRow, 0).toString();
        String subId = marksTable.getValueAt(selectedRow, 1).toString();
        String semester = marksTable.getValueAt(selectedRow, 2).toString();
        Object marksObject = marksTable.getValueAt(selectedRow, 3);

        if (marksObject == null || marksObject.toString().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Marks field cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String marks = marksObject.toString().trim();
        if (!isNumeric(marks) || Integer.parseInt(marks) < 0 || Integer.parseInt(marks) > 100) {
            JOptionPane.showMessageDialog(frame, "Marks must be between 0 and 100.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "UPDATE marks SET Marks = ? WHERE sid = ? AND Sub_id = ? AND semester = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, marks);
            ps.setString(2, sid);
            ps.setString(3, subId);
            ps.setString(4, semester);
            
            // Debugging statement to see the query execution
            System.out.println("Executing: " + ps.toString());
            
            int updated = ps.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(frame, "Marks updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Reset form after update
                sidField.setText(""); // Clear the SID field
                semesterComboBox.setSelectedIndex(0); // Reset semester selection
                DefaultTableModel model = (DefaultTableModel) marksTable.getModel();
                model.setRowCount(0); // Clear the table
                
                frame.repaint(); // Repaint UI for immediate effect
            } else {
                JOptionPane.showMessageDialog(frame, "No rows updated. Please check your input.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to check if a string is numeric
    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UpdateMarks::new);
    }
}