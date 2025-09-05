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

public class FourthSemesterMarks extends JFrame {

    public FourthSemesterMarks() {
        setTitle("Fourth Semester Marks Entry");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(9, 2, 10, 10)); // Increased to 9 rows for Project-I

        // Labels and Text Fields
        JLabel sidLabel = new JLabel("SID:");
        JTextField sidField = new JTextField();

        JLabel osLabel = new JLabel("Operating System:");
        JTextField osField = new JTextField();

        JLabel nmLabel = new JLabel("Numerical Methods:");
        JTextField nmField = new JTextField();

        JLabel seLabel = new JLabel("Software Engineering:");
        JTextField seField = new JTextField();

        JLabel slLabel = new JLabel("Scripting Language:");
        JTextField slField = new JTextField();

        JLabel dbmsLabel = new JLabel("Database Management System:");
        JTextField dbmsField = new JTextField();
        
        JLabel projectLabel = new JLabel("Project-I:");
        JTextField projectField = new JTextField();

        JButton saveButton = new JButton("Save");
        JButton backButton = new JButton("Back");

        // Add components to the form
        add(sidLabel);
        add(sidField);

        add(osLabel);
        add(osField);

        add(nmLabel);
        add(nmField);

        add(seLabel);
        add(seField);

        add(slLabel);
        add(slField);

        add(dbmsLabel);
        add(dbmsField);
        
        add(projectLabel);
        add(projectField);

        add(saveButton);
        add(backButton); // Add the Back button

        // Action Listener for Save Button
        saveButton.addActionListener(e -> {
            String sidText = sidField.getText().trim();
            String osText = osField.getText().trim();
            String nmText = nmField.getText().trim();
            String seText = seField.getText().trim();
            String slText = slField.getText().trim();
            String dbmsText = dbmsField.getText().trim();
            String projectText = projectField.getText().trim();

            // Validate input
            if (sidText.isEmpty() || osText.isEmpty() || nmText.isEmpty() || seText.isEmpty() ||
                slText.isEmpty() || dbmsText.isEmpty() || projectText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields.");
                return;
            }

            try {
                int sid = Integer.parseInt(sidText);
                int osMarks = Integer.parseInt(osText);
                int nmMarks = Integer.parseInt(nmText);
                int seMarks = Integer.parseInt(seText);
                int slMarks = Integer.parseInt(slText);
                int dbmsMarks = Integer.parseInt(dbmsText);
                int projectMarks = Integer.parseInt(projectText);

                // Validate marks
                if (osMarks < 0 || osMarks > 100 || nmMarks < 0 || nmMarks > 100 ||
                    seMarks < 0 || seMarks > 100 || slMarks < 0 || slMarks > 100 ||
                    dbmsMarks < 0 || dbmsMarks > 100 || projectMarks < 0 || projectMarks > 100) {
                    JOptionPane.showMessageDialog(null, "Marks must be between 0 and 100.");
                    return;
                }

                // Check if SID exists in Semester 4
                if (!checkStudentSemester(sid)) {
                    JOptionPane.showMessageDialog(null, "SID does not exist or the student is not in Semester 4.");
                    return;
                }

                // Check for data duplication
                if (isDuplicateEntry(sid)) {
                    JOptionPane.showMessageDialog(null, "Marks for this SID already exist in the Marks database.");
                    return;
                }

                // Save marks to the database
                if (saveMarksToDatabase(sid, osMarks, nmMarks, seMarks, slMarks, dbmsMarks, projectMarks)) {
                    JOptionPane.showMessageDialog(null, "Marks saved successfully!");
                    resetFields(sidField, osField, nmField, seField, slField, dbmsField, projectField);
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

    // Method to reset fields after successful insertion
    private void resetFields(JTextField sidField, JTextField osField, JTextField nmField,
                             JTextField seField, JTextField slField, JTextField dbmsField,
                             JTextField projectField) {
        sidField.setText("");
        osField.setText("");
        nmField.setText("");
        seField.setText("");
        slField.setText("");
        dbmsField.setText("");
        projectField.setText("");
    }

    // Method to check if SID exists in the students table and is in Semester 4
    private boolean checkStudentSemester(int sid) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");
            String query = "SELECT COUNT(*) FROM students WHERE sid = ? AND semester = 4";
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
    private boolean saveMarksToDatabase(int sid, int os, int nm, int se, int sl, int dbms, int project) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");
            String query = "INSERT INTO marks (sid, Sub_id, Marks, Semester) VALUES (?, ?, ?, 4)";
            PreparedStatement pstmt = con.prepareStatement(query);
            
            // Array of subject IDs and corresponding marks
            int[] subIds = {16, 17, 18, 19, 20, 21}; // Sub_id values for the subjects
            int[] marks = {os, nm, se, sl, dbms, project}; // Marks for each subject
            
            // Insert marks for each subject
            for (int i = 0; i < subIds.length; i++) {
                pstmt.setInt(1, sid);
                pstmt.setInt(2, subIds[i]);
                pstmt.setInt(3, marks[i]);
                pstmt.executeUpdate();
            }

            pstmt.close();
            con.close();
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FourthSemesterMarks::new);
    }
}