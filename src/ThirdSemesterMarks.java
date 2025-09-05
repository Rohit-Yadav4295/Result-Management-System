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

public class ThirdSemesterMarks extends JFrame {

    public ThirdSemesterMarks() {
        setTitle("Third Semester Marks Entry");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2, 10, 10)); // Adjusted for 8 rows and 2 columns

        // Labels and Text Fields
        JLabel sidLabel = new JLabel("SID:");
        JTextField sidField = new JTextField();

        JLabel dsaLabel = new JLabel("Data Structure and Algorithms:");
        JTextField dsaField = new JTextField();

        JLabel probabilityLabel = new JLabel("Probability and Statistics:");
        JTextField probabilityField = new JTextField();

        JLabel sadLabel = new JLabel("System Analysis and Design:");
        JTextField sadField = new JTextField();

        JLabel javaLabel = new JLabel("OOP in Java:");
        JTextField javaField = new JTextField();

        JLabel webLabel = new JLabel("Web Technology:");
        JTextField webField = new JTextField();

        JButton saveButton = new JButton("Save");
        JButton backButton = new JButton("Back");

        // Add components to the form
        add(sidLabel);
        add(sidField);

        add(dsaLabel);
        add(dsaField);

        add(probabilityLabel);
        add(probabilityField);

        add(sadLabel);
        add(sadField);

        add(javaLabel);
        add(javaField);

        add(webLabel);
        add(webField);

        add(saveButton);
        add(backButton); // Add the Back button

        // Action Listener for Save Button
        saveButton.addActionListener(e -> {
            String sidText = sidField.getText().trim();
            String dsaText = dsaField.getText().trim();
            String probabilityText = probabilityField.getText().trim();
            String sadText = sadField.getText().trim();
            String javaText = javaField.getText().trim();
            String webText = webField.getText().trim();

            // Validate input
            if (sidText.isEmpty() || dsaText.isEmpty() || probabilityText.isEmpty() || 
                sadText.isEmpty() || javaText.isEmpty() || webText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields.");
                return;
            }

            try {
                int sid = Integer.parseInt(sidText);
                int dsaMarks = Integer.parseInt(dsaText);
                int probabilityMarks = Integer.parseInt(probabilityText);
                int sadMarks = Integer.parseInt(sadText);
                int javaMarks = Integer.parseInt(javaText);
                int webMarks = Integer.parseInt(webText);

                // Validate marks
                if (dsaMarks < 0 || dsaMarks > 100 || probabilityMarks < 0 || 
                    probabilityMarks > 100 || sadMarks < 0 || sadMarks > 100 ||
                    javaMarks < 0 || javaMarks > 100 || webMarks < 0 || webMarks > 100) {
                    JOptionPane.showMessageDialog(null, "Marks must be between 0 and 100.");
                    return;
                }

                // Check if SID exists and belongs to Semester 3
                if (!checkStudentSemester(sid)) {
                    JOptionPane.showMessageDialog(null, "SID does not exist or the student is not in Semester 3.");
                    return;
                }

                // Check for data duplication
                if (isDuplicateEntry(sid)) {
                    JOptionPane.showMessageDialog(null, "Marks for this SID already exist in the Marks database.");
                    return;
                }

                // Save marks to the database
                if (saveMarksToDatabase(sid, dsaMarks, probabilityMarks, sadMarks, javaMarks, webMarks)) {
                    JOptionPane.showMessageDialog(null, "Marks saved successfully!");
                    resetFields(sidField, dsaField, probabilityField, sadField, javaField, webField);
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
    private void resetFields(JTextField sidField, JTextField dsaField, JTextField probabilityField,
                             JTextField sadField, JTextField javaField, JTextField webField) {
        sidField.setText("");
        dsaField.setText("");
        probabilityField.setText("");
        sadField.setText("");
        javaField.setText("");
        webField.setText("");
    }

    // Method to check if SID exists in the students table and is in Semester 3
    private boolean checkStudentSemester(int sid) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");

            String query = "SELECT COUNT(*) FROM students WHERE sid = ? AND semester = 3";
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
    private boolean saveMarksToDatabase(int sid, int dsa, int probability, int sad, int java, int web) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");

            // Subject IDs mapping
            int[] subjectIds = { 11, 12, 13, 14, 15 }; // Sub_id for subjects
            int[] marks = { dsa, probability, sad, java, web }; // Marks for subjects
            
            String query = "INSERT INTO marks (sid, sub_id, marks, semester) VALUES (?, ?, ?, 3)";
            PreparedStatement pstmt = con.prepareStatement(query);

            for (int i = 0; i < subjectIds.length; i++) {
                pstmt.setInt(1, sid);
                pstmt.setInt(2, subjectIds[i]);
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
        SwingUtilities.invokeLater(ThirdSemesterMarks::new);
    }
}