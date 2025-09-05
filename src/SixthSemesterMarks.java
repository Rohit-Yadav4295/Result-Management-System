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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SixthSemesterMarks extends JFrame {

    private JTextField sidField;
    private JTextField mbField;
    private JTextField dsField;
    private JTextField ecoField;
    private JTextField javaField;
    private JTextField npField;
    private JTextField projectField;

    public SixthSemesterMarks() {
        setTitle("Sixth Semester Marks Entry");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2, 10, 10));

        // Initialize Labels and Text Fields
        JLabel sidLabel = new JLabel("SID:");
        sidField = new JTextField();

        JLabel mbLabel = new JLabel("Mobile Programming:");
        mbField = new JTextField();

        JLabel dsLabel = new JLabel("Distributed System:");
        dsField = new JTextField();

        JLabel ecoLabel = new JLabel("Applied Economics:");
        ecoField = new JTextField();

        JLabel javaLabel = new JLabel("Advanced Java Programming:");
        javaField = new JTextField();

        JLabel npLabel = new JLabel("Network Programming:");
        npField = new JTextField();
        
        JLabel projectLabel = new JLabel("Project-II:");
        projectField = new JTextField();

        JButton saveButton = new JButton("Save");
        JButton backButton = new JButton("Back");

        // Add components to the form
        add(sidLabel);
        add(sidField);
        add(mbLabel);
        add(mbField);
        add(dsLabel);
        add(dsField);
        add(ecoLabel);
        add(ecoField);
        add(javaLabel);
        add(javaField);
        add(npLabel);
        add(npField);
        add(projectLabel);
        add(projectField);
        add(saveButton);
        add(backButton);

        // Action Listener for Save Button
        saveButton.addActionListener(e -> {
            String sidText = sidField.getText().trim();
            String mbText = mbField.getText().trim();
            String dsText = dsField.getText().trim();
            String ecoText = ecoField.getText().trim();
            String javaText = javaField.getText().trim();
            String npText = npField.getText().trim();
            String projectText = projectField.getText().trim();

            // Validate input
            if (sidText.isEmpty() || mbText.isEmpty() || dsText.isEmpty() || ecoText.isEmpty() ||
                javaText.isEmpty() || npText.isEmpty() || projectText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields.");
                return;
            }

            try {
                int sid = Integer.parseInt(sidText);
                int mbMarks = Integer.parseInt(mbText);
                int dsMarks = Integer.parseInt(dsText);
                int ecoMarks = Integer.parseInt(ecoText);
                int javaMarks = Integer.parseInt(javaText);
                int npMarks = Integer.parseInt(npText);
                int projectMarks = Integer.parseInt(projectText);

                // Validate marks
                if (mbMarks < 0 || mbMarks > 100 || dsMarks < 0 || dsMarks > 100 ||
                    ecoMarks < 0 || ecoMarks > 100 || javaMarks < 0 || javaMarks > 100 ||
                    npMarks < 0 || npMarks > 100 || projectMarks < 0 || projectMarks > 100) {
                    JOptionPane.showMessageDialog(null, "Marks must be between 0 and 100.");
                    return;
                }

                // Check if SID exists and belongs to Semester 6
                if (!checkStudentSemester(sid)) {
                    JOptionPane.showMessageDialog(null, "SID does not exist or the student is not in Semester 6.");
                    return;
                }

                // Check for data duplication
                if (isDuplicateEntry(sid)) {
                    JOptionPane.showMessageDialog(null, "Marks for this SID already exist in the Marks database.");
                    return;
                }

                // Save marks to the database
                if (saveMarksToDatabase(sid, mbMarks, 27) &&  // Mobile Programming
                    saveMarksToDatabase(sid, dsMarks, 28) &&  // Distributed System
                    saveMarksToDatabase(sid, ecoMarks, 29) &&  // Applied Economics
                    saveMarksToDatabase(sid, javaMarks, 30) &&  // Advanced Java Programming
                    saveMarksToDatabase(sid, npMarks, 31) &&    // Network Programming
                    saveMarksToDatabase(sid, projectMarks, 32)) { // Project-II
                    JOptionPane.showMessageDialog(null, "Marks saved successfully!");
                    resetFields(); // Reset fields after successful insertion
                } else {
                    JOptionPane.showMessageDialog(null, "Error saving marks.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numeric values for SID and marks.");
            }
        });

        // Action Listener for Back Button
        backButton.addActionListener(e -> {
            setVisible(false);
            new SemesterSelection(); 
        });

        setVisible(true);
    }

    // Method to reset all input fields
    private void resetFields() {
        sidField.setText("");
        mbField.setText("");
        dsField.setText("");
        ecoField.setText("");
        javaField.setText("");
        npField.setText("");
        projectField.setText("");
    }

    // Method to check if SID exists in the students table and is in Semester 6
    private boolean checkStudentSemester(int sid) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");
            String query = "SELECT COUNT(*) FROM students WHERE sid = ? AND semester = 6";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, sid);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            boolean exists = rs.getInt(1) > 0;

            rs.close();
            pstmt.close();
            con.close();
            return exists;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Method to check for data duplication
    private boolean isDuplicateEntry(int sid) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");
            String query = "SELECT COUNT(*) FROM marks WHERE sid = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, sid);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            boolean isDuplicate = rs.getInt(1) > 0;

            rs.close();
            pstmt.close();
            con.close();
            return isDuplicate;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Method to save marks to the database
    private boolean saveMarksToDatabase(int sid, int marks, int subId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");
            String query = "INSERT INTO marks (sid, Sub_id, Marks, Semester) VALUES (?, ?, ?, 6)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, sid);
            pstmt.setInt(2, subId);
            pstmt.setInt(3, marks);
            
            int rowsInserted = pstmt.executeUpdate();
            pstmt.close();
            con.close();
            return rowsInserted > 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SixthSemesterMarks::new);
    }
}