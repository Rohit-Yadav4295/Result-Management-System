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

public class SeventhSemesterMarks extends JFrame {
    private JTextField sidField;
    private JTextField cyberField;
    private JTextField cloudField;
    private JTextField internField;
    private JComboBox<String> elective1Box;
    private JTextField elective1Field;
    private JComboBox<String> elective2Box;
    private JTextField elective2Field;

    public SeventhSemesterMarks() {
        setTitle("Seventh Semester Marks Entry");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(9, 2, 10, 10));

        // Labels and Text Fields
        JLabel sidLabel = new JLabel("SID:");
        sidField = new JTextField();

        JLabel cyberLabel = new JLabel("Cyber Law and Professional Ethics:");
        cyberField = new JTextField();

        JLabel cloudLabel = new JLabel("Cloud Computing:");
        cloudField = new JTextField();

        JLabel internLabel = new JLabel("Internship:");
        internField = new JTextField();

        // Elective I
        JLabel elective1Label = new JLabel("Elective I:");
        String[] electives1 = {
            "Select Elective", 
            "Image Processing", 
            "Database Administration", 
            "Network Administration"
        };
        elective1Box = new JComboBox<>(electives1);
        elective1Field = new JTextField();

        // Elective II
        JLabel elective2Label = new JLabel("Elective II:");
        String[] electives2 = {
            "Select Elective", 
            "Advanced DotNet Technology", 
            "E-Governance", 
            "Artificial Intelligence "
        };
        elective2Box = new JComboBox<>(electives2);
        elective2Field = new JTextField();

        JButton saveButton = new JButton("Save");
        JButton backButton = new JButton("Back");

        // Add components to the form
        add(sidLabel);
        add(sidField);
        add(cyberLabel);
        add(cyberField);
        add(cloudLabel);
        add(cloudField);
        add(internLabel);
        add(internField);
        add(elective1Label);
        add(elective1Box);
        add(new JLabel("Marks:"));  // Label for marks input for Elective I
        add(elective1Field);
        add(elective2Label);
        add(elective2Box);
        add(new JLabel("Marks:"));  // Label for marks input for Elective II
        add(elective2Field);
        add(saveButton);
        add(backButton);

        // Action Listener for Save Button
        saveButton.addActionListener(e -> {
            String sidText = sidField.getText().trim();
            String cyberText = cyberField.getText().trim();
            String cloudText = cloudField.getText().trim();
            String internText = internField.getText().trim();
            String elective1Text = elective1Field.getText().trim();
            String elective2Text = elective2Field.getText().trim();

            // Validate input
            if (sidText.isEmpty() || cyberText.isEmpty() || cloudText.isEmpty() || internText.isEmpty() || 
                elective1Text.isEmpty() || elective2Text.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields.");
                return;
            }

            try {
                int sid = Integer.parseInt(sidText);
                int cyberMarks = Integer.parseInt(cyberText);
                int cloudMarks = Integer.parseInt(cloudText);
                int internMarks = Integer.parseInt(internText);
                int elective1Marks = Integer.parseInt(elective1Text);
                int elective2Marks = Integer.parseInt(elective2Text);

                // Validate marks
                if (cyberMarks < 0 || cyberMarks > 100 || cloudMarks < 0 || cloudMarks > 100 || 
                    internMarks < 0 || internMarks > 100 || elective1Marks < 0 || elective1Marks > 100 || 
                    elective2Marks < 0 || elective2Marks > 100) {
                    JOptionPane.showMessageDialog(null, "Marks must be between 0 and 100.");
                    return;
                }

                // Check if SID exists and belongs to Semester 7
                if (!checkStudentSemester(sid)) {
                    JOptionPane.showMessageDialog(null, "SID does not exist or the student is not in Semester 7.");
                    return;
                }

                // Check for data duplication
                if (isDuplicateEntry(sid)) {
                    JOptionPane.showMessageDialog(null, "Marks for this SID already exist in the Marks database.");
                    return;
                }

                // Determine sub_id based on selected electives
                int elective1SubId = getSubIdForElective((String) elective1Box.getSelectedItem());
                int elective2SubId = getSubIdForElective((String) elective2Box.getSelectedItem());

                // Save marks to the database
                if (saveMarksToDatabase(sid, cyberMarks, cloudMarks, internMarks, elective1Marks, elective1SubId, elective2Marks, elective2SubId)) {
                    JOptionPane.showMessageDialog(null, "Marks saved successfully!");
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
            new SemesterSelection(); // Assuming SemesterSelection is another class in your program
        });

        setVisible(true);
    }

    // Method to determine sub_id based on selected elective
    private int getSubIdForElective(String elective) {
        switch (elective) {
            case "Image Processing": return 36; 
            case "Database Administration": return 37;
            case "Network Administration": return 38;
            case "DotNet Technology": return 39;
            case "E-Governance": return 40;
            case "Artificial Intelligence": return 41;
            default: return -1; // Invalid selection for "Select Elective"
        }
    }

    // Method to check if SID exists in the students table and is in Semester 7
    private boolean checkStudentSemester(int sid) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");
            String query = "SELECT COUNT(*) FROM students WHERE sid = ? AND semester = 7";
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
            String query = "SELECT COUNT(*) FROM marks WHERE sid = ? AND Semester = 7";
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
    private boolean saveMarksToDatabase(int sid, int cyber, int cloud, int intern, int elective1, int elective1SubId, int elective2, int elective2SubId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");
            String query = "INSERT INTO marks (sid, Sub_id, Marks, Semester) VALUES (?, ?, ?, 7)";
            PreparedStatement pstmt = con.prepareStatement(query);

            // Save marks for each subject
            pstmt.setInt(1, sid);
            pstmt.setInt(2, 33); // Cyber Law and Professional Ethics
            pstmt.setInt(3, cyber);
            pstmt.executeUpdate();

            pstmt.setInt(2, 34); // Cloud Computing
            pstmt.setInt(3, cloud);
            pstmt.executeUpdate();

            pstmt.setInt(2, 35); // Internship
            pstmt.setInt(3, intern);
            pstmt.executeUpdate();

            // Save marks for Elective I if applicable
            if (elective1SubId != -1) {
                pstmt.setInt(1, sid);
                pstmt.setInt(2, elective1SubId);
                pstmt.setInt(3, elective1);
                pstmt.executeUpdate();
            }

            // Save marks for Elective II if applicable
            if (elective2SubId != -1) {
                pstmt.setInt(1, sid);
                pstmt.setInt(2, elective2SubId);
                pstmt.setInt(3, elective2);
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
        SwingUtilities.invokeLater(SeventhSemesterMarks::new);
    }
}