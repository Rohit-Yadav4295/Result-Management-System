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

public class FifthSemesterMarks extends JFrame {

    public FifthSemesterMarks() {
        setTitle("Fifth Semester Marks Entry");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2, 10, 10)); // Adjusted for 8 rows and 2 columns

        // Labels and Text Fields
        JLabel sidLabel = new JLabel("SID:");
        JTextField sidField = new JTextField();

        JLabel misLabel = new JLabel("MIS and E-Business:");
        JTextField misField = new JTextField();

        JLabel dotnetLabel = new JLabel("DotNet Technology:");
        JTextField dotnetField = new JTextField();

        JLabel comnetLabel = new JLabel("Computer Networking:");
        JTextField comnetField = new JTextField();

        JLabel mgtLabel = new JLabel("Introduction to Management:");
        JTextField mgtField = new JTextField();

        JLabel cgLabel = new JLabel("Computer Graphics and Animation:");
        JTextField cgField = new JTextField();

        JButton saveButton = new JButton("Save");
        JButton backButton = new JButton("Back");

        // Add components to the form
        add(sidLabel);
        add(sidField);

        add(misLabel);
        add(misField);

        add(dotnetLabel);
        add(dotnetField);

        add(comnetLabel);
        add(comnetField);

        add(mgtLabel);
        add(mgtField);

        add(cgLabel);
        add(cgField);

        add(saveButton);
        add(backButton); // Add the Back button

        // Action Listener for Save Button
        saveButton.addActionListener(e -> {
            String sidText = sidField.getText().trim();
            String misText = misField.getText().trim();
            String dotnetText = dotnetField.getText().trim();
            String comnetText = comnetField.getText().trim();
            String mgtText = mgtField.getText().trim();
            String cgText = cgField.getText().trim();

            // Validate input
            if (sidText.isEmpty() || misText.isEmpty() || dotnetText.isEmpty() || comnetText.isEmpty() || 
                mgtText.isEmpty() || cgText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields.");
                return;
            }

            try {
                int sid = Integer.parseInt(sidText);
                int misMarks = Integer.parseInt(misText);
                int dotnetMarks = Integer.parseInt(dotnetText);
                int comnetMarks = Integer.parseInt(comnetText);
                int mgtMarks = Integer.parseInt(mgtText);
                int cgMarks = Integer.parseInt(cgText);

                // Validate marks
                if (misMarks < 0 || misMarks > 100 || dotnetMarks < 0 || dotnetMarks > 100 ||
                    comnetMarks < 0 || comnetMarks > 100 || mgtMarks < 0 || mgtMarks > 100 ||
                    cgMarks < 0 || cgMarks > 100) {
                    JOptionPane.showMessageDialog(null, "Marks must be between 0 and 100.");
                    return;
                }

                // Check if SID exists and belongs to Semester 5
                if (!checkStudentSemester(sid)) {
                    JOptionPane.showMessageDialog(null, "SID does not exist or the student is not in Semester 5.");
                    return;
                }

                // Check for data duplication
                if (isDuplicateEntry(sid)) {
                    JOptionPane.showMessageDialog(null, "Marks for this SID already exist in the Marks database.");
                    return;
                }

                // Save marks to the database
                if (saveMarksToDatabase(sid, misMarks, dotnetMarks, comnetMarks, mgtMarks, cgMarks)) {
                    JOptionPane.showMessageDialog(null, "Marks saved successfully!");
                    resetFields(sidField, misField, dotnetField, comnetField, mgtField, cgField);
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
    private void resetFields(JTextField sidField, JTextField misField, JTextField dotnetField,
                             JTextField comnetField, JTextField mgtField, JTextField cgField) {
        sidField.setText("");
        misField.setText("");
        dotnetField.setText("");
        comnetField.setText("");
        mgtField.setText("");
        cgField.setText("");
    }

    // Method to check if SID exists in the students table and is in Semester 5
    private boolean checkStudentSemester(int sid) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");

            String query = "SELECT COUNT(*) FROM students WHERE sid = ? AND semester = 5";
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
    private boolean saveMarksToDatabase(int sid, int mis, int dotnet, int comnet, int mgt, int cg) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");

            // Subject IDs mapping
            int[] subIds = {22, 23, 24, 25, 26}; // Sub_id values for each subject
            int[] marks = {mis, dotnet, comnet, mgt, cg}; // Marks for each subject

            String query = "INSERT INTO marks (sid, sub_id, marks, semester) VALUES (?, ?, ?, 5)";
            PreparedStatement pstmt = con.prepareStatement(query);

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
        SwingUtilities.invokeLater(FifthSemesterMarks::new);
    }
}