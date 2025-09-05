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
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DisplayStudents {
    private JFrame frame;
    private JTable studentsTable;
    private JButton backButton;
    private JButton searchButton;
    private JTextField searchField;
    private DefaultTableModel model;
    private Connection con;

    public DisplayStudents() {
        frame = new JFrame("Display Students");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Table to display data
        studentsTable = new JTable();
        model = new DefaultTableModel();
        studentsTable.setModel(model);
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Create a search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchPanel.add(searchField);

        // Search button
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(new Color(0, 102, 204)); // Blue background
        searchButton.setForeground(Color.BLACK); // Black text
        searchButton.setFocusPainted(false); // Remove focus border
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchStudents();
            }
        });
        searchPanel.add(searchButton);

        // Back button
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(204, 0, 0)); // Red background
        backButton.setForeground(Color.BLACK); // Black text
        backButton.setFocusPainted(false); // Remove focus border
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the current frame
                new AdminJob();
            }
        });
        searchPanel.add(backButton);

        // Add search panel to the frame
        frame.add(searchPanel, BorderLayout.NORTH);

        // Fetch and display records
        fetchAndDisplayRecords();

        frame.setVisible(true);
    }

    private void fetchAndDisplayRecords() {
        try {
            // Connect to the database
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");

            // Query to fetch all records from students table
            String query = "SELECT * FROM students";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            // Create table model
            model.setColumnIdentifiers(new String[]{"SID", "Name", "Course", "Address", "Contact", "Semester"});

            // Populate table model with data from ResultSet
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("sid"),
                        rs.getString("name"),
                        rs.getString("course"),
                        rs.getString("address"),
                        rs.getString("contact"),
                        rs.getString("semester")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error fetching records!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchStudents() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            // If search field is empty, reset the table to show all records
            studentsTable.setRowSorter(null);
            return;
        }

        // Use TableRowSorter to filter rows
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        studentsTable.setRowSorter(sorter);

        // Create a RowFilter to search by name, address, or contact
        RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + searchText, 1, 2, 3); // Columns: Name (1), Address (2), Contact (3)
        sorter.setRowFilter(rowFilter);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DisplayStudents::new);
    }
}