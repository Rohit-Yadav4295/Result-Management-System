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

public class SecondSemesterMarks extends JFrame {

    // Declare text fields at class level for resetting purposes
    private JTextField sidField;
    private JTextField cField;
    private JTextField faField;
    private JTextField engField;
    private JTextField mathField;
    private JTextField mpField;

    public SecondSemesterMarks() {
        setTitle("Second Semester Marks Entry");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2, 10, 10)); // Adjusted for 8 rows and 2 columns

        // Initialize Labels and Text Fields
        JLabel sidLabel = new JLabel("SID:");
        sidField = new JTextField();

        JLabel cLabel = new JLabel("C Programming:");
        cField = new JTextField();

        JLabel faLabel = new JLabel("Financial Accounting:");
        faField = new JTextField();

        JLabel engLabel = new JLabel("English-II:");
        engField = new JTextField();

        JLabel mathLabel = new JLabel("Mathematics-II:");
        mathField = new JTextField();

        JLabel mpLabel = new JLabel("Microprocessor:");
        mpField = new JTextField();

        JButton saveButton = new JButton("Save");
        JButton backButton = new JButton("Back");

        // Add components to the form
        add(sidLabel);
        add(sidField);

        add(cLabel);
        add(cField);

        add(faLabel);
        add(faField);

        add(engLabel);
        add(engField);

        add(mathLabel);
        add(mathField);

        add(mpLabel);
        add(mpField);

        add(saveButton);
        add(backButton);

        // Action Listener for Save Button
        saveButton.addActionListener(e -> {
            String sidText = sidField.getText().trim();
            String cText = cField.getText().trim();
            String faText = faField.getText().trim();
            String engText = engField.getText().trim();
            String mathText = mathField.getText().trim();
            String mpText = mpField.getText().trim();

            // Validate input
            if (sidText.isEmpty() || cText.isEmpty() || faText.isEmpty() || engText.isEmpty() || mathText.isEmpty() || mpText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields.");
                return;
            }

            try {
                int sid = Integer.parseInt(sidText);
                int cMarks = Integer.parseInt(cText);
                int faMarks = Integer.parseInt(faText);
                int engMarks = Integer.parseInt(engText);
                int mathMarks = Integer.parseInt(mathText);
                int mpMarks = Integer.parseInt(mpText);

                // Validate marks
                if (cMarks < 0 || cMarks > 100 || faMarks < 0 || faMarks > 100 ||
                    engMarks < 0 || engMarks > 100 || mathMarks < 0 || mathMarks > 100 || mpMarks < 0 || mpMarks > 100) {
                    JOptionPane.showMessageDialog(null, "Marks should be between 0 and 100.");
                    return;
                }

                // Check if SID exists and belongs to Semester 2
                if (!checkStudentSemester(sid)) {
                    JOptionPane.showMessageDialog(null, "SID does not exist or the student is not in Semester 2.");
                    return;
                }

                // Check if subjects are valid
                if (!areSubjectsValid()) {
                    JOptionPane.showMessageDialog(null, "One or more subjects are not valid for Semester 2.");
                    return;
                }

                // Check for data duplication
                if (isDuplicateEntry(sid)) {
                    JOptionPane.showMessageDialog(null, "Marks for this SID already exist in the Marks database.");
                    return;
                }

                // Save marks to the database
                if (saveMarksToDatabase(sid, cMarks, faMarks, engMarks, mathMarks, mpMarks)) {
                    JOptionPane.showMessageDialog(null, "Marks saved successfully!");
                    resetFields();  // Reset fields after successful insertion
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
        cField.setText("");
        faField.setText("");
        engField.setText("");
        mathField.setText("");
        mpField.setText("");
    }

    // Method to check if SID exists in the students table and is in Semester 2
    private boolean checkStudentSemester(int sid) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "")) {
                String query = "SELECT COUNT(*) FROM students WHERE sid = ? AND semester = 2";
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setInt(1, sid);

                ResultSet rs = pstmt.executeQuery();
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Method to check the validity of subjects for Semester 2
    private boolean areSubjectsValid() {
        int[] subIds = {6, 7, 8, 9, 10}; // Subject IDs for Semester 2
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "")) {
                String query = "SELECT COUNT(*) FROM subjects WHERE Sub_id = ? AND Semester = 2";
                for (int id : subIds) {
                    PreparedStatement pstmt = con.prepareStatement(query);
                    pstmt.setInt(1, id);
                    ResultSet rs = pstmt.executeQuery();
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        return false; // If any subject is not valid for Semester 2
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    // Method to check for data duplication
    private boolean isDuplicateEntry(int sid) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "")) {
                String query = "SELECT COUNT(*) FROM marks WHERE sid = ? AND Semester = 2";
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setInt(1, sid);

                ResultSet rs = pstmt.executeQuery();
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Method to save marks to the database
    private boolean saveMarksToDatabase(int sid, int c, int fa, int eng, int math, int mp) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "")) {
                String query = "INSERT INTO marks (sid, Sub_id, Marks, Semester) VALUES (?, ?, ?, 2)";
                PreparedStatement pstmt = con.prepareStatement(query);

                // Insert each subject's marks
                int[] subjects = {6, 7, 8, 9, 10}; // Subject IDs
                int[] marks = {c, fa, eng, math, mp}; // Marks to insert

                for (int i = 0; i < subjects.length; i++) {
                    pstmt.setInt(1, sid);
                    pstmt.setInt(2, subjects[i]);
                    pstmt.setInt(3, marks[i]);
                    pstmt.executeUpdate();
                }

                return true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SecondSemesterMarks::new);
    }
}