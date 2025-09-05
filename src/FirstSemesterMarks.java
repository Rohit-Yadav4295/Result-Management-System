/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rohit
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FirstSemesterMarks extends JFrame {

    // Declare text fields at class level for resetting purposes
    private JTextField sidField;
    private JTextField cfaField;
    private JTextField satField;
    private JTextField engField;
    private JTextField mathField;
    private JTextField dlField;

    public FirstSemesterMarks() {
        setTitle("First Semester Marks Entry");
        setSize(1000, 800); // Adjusted size for 4K screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set a modern look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding
        add(mainPanel);

        // Form panel with GridBagLayout for better control
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Set a larger font for better readability on 4K screens
        Font largerFont = new Font("SansSerif", Font.PLAIN, 20);

        // Initialize Labels and Text Fields
        JLabel sidLabel = new JLabel("SID:");
        sidLabel.setFont(largerFont);
        sidField = new JTextField(20);
        sidField.setFont(largerFont);

        JLabel cfaLabel = new JLabel("Computer Fundamentals & Applications:");
        cfaLabel.setFont(largerFont);
        cfaField = new JTextField(20);
        cfaField.setFont(largerFont);

        JLabel satLabel = new JLabel("Society and Technology:");
        satLabel.setFont(largerFont);
        satField = new JTextField(20);
        satField.setFont(largerFont);

        JLabel engLabel = new JLabel("English-I:");
        engLabel.setFont(largerFont);
        engField = new JTextField(20);
        engField.setFont(largerFont);

        JLabel mathLabel = new JLabel("Mathematics-I:");
        mathLabel.setFont(largerFont);
        mathField = new JTextField(20);
        mathField.setFont(largerFont);

        JLabel dlLabel = new JLabel("Digital Logic:");
        dlLabel.setFont(largerFont);
        dlField = new JTextField(20);
        dlField.setFont(largerFont);

        // Add components to the form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(sidLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(sidField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(cfaLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(cfaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(satLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(satField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(engLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(engField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(mathLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(mathField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(dlLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(dlField, gbc);

        // Add form panel to the main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton saveButton = new JButton("Save");
        saveButton.setFont(largerFont);
        saveButton.setPreferredSize(new Dimension(150, 50)); // Set button size

        JButton backButton = new JButton("Back");
        backButton.setFont(largerFont);
        backButton.setPreferredSize(new Dimension(150, 50)); // Set button size

        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        // Add button panel to the main panel
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Action Listener for Save Button
        saveButton.addActionListener(e -> saveMarks());

        // Action Listener for Back Button
        backButton.addActionListener(e -> {
            setVisible(false);
            new SemesterSelection();
        });

        setVisible(true);
    }

    // Method to save marks
    private void saveMarks() {
        String sidText = sidField.getText().trim();
        String cfaText = cfaField.getText().trim();
        String satText = satField.getText().trim();
        String engText = engField.getText().trim();
        String mathText = mathField.getText().trim();
        String dlText = dlField.getText().trim();

        // Validate input
        if (sidText.isEmpty() || cfaText.isEmpty() || satText.isEmpty() || engText.isEmpty() || mathText.isEmpty() || dlText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill all fields.");
            return;
        }

        try {
            int sid = Integer.parseInt(sidText);
            int cfaMarks = Integer.parseInt(cfaText);
            int satMarks = Integer.parseInt(satText);
            int engMarks = Integer.parseInt(engText);
            int mathMarks = Integer.parseInt(mathText);
            int dlMarks = Integer.parseInt(dlText);

            // Validate marks
            if (cfaMarks < 0 || cfaMarks > 100 || satMarks < 0 || satMarks > 100 ||
                engMarks < 0 || engMarks > 100 || mathMarks < 0 || mathMarks > 100 || dlMarks < 0 || dlMarks > 100) {
                JOptionPane.showMessageDialog(null, "Marks should be between 0 and 100.");
                return;
            }

            // Check if SID exists and belongs to Semester 1
            if (!checkStudentSemester(sid)) {
                JOptionPane.showMessageDialog(null, "SID does not exist or the student is not in Semester 1.");
                return;
            }

            // Check for valid subjects and their semester
            if (!areSubjectsValid()) {
                JOptionPane.showMessageDialog(null, "One or more subjects are not valid for Semester 1.");
                return;
            }

            // Check for data duplication
            if (isDuplicateEntry(sid)) {
                JOptionPane.showMessageDialog(null, "Marks for this SID already exist in the Marks database.");
                return;
            }

            // Save marks to the database
            if (saveMarksToDatabase(sid, cfaMarks, satMarks, engMarks, mathMarks, dlMarks)) {
                JOptionPane.showMessageDialog(null, "Marks saved successfully!");
                resetFields();  // Reset fields after successful insertion
            } else {
                JOptionPane.showMessageDialog(null, "Error saving marks.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter valid numeric values for SID and marks.");
        }
    }

    // Method to reset all input fields
    private void resetFields() {
        sidField.setText("");
        cfaField.setText("");
        satField.setText("");
        engField.setText("");
        mathField.setText("");
        dlField.setText("");
    }

    // Method to check if SID exists in the students table and is in Semester 1
    private boolean checkStudentSemester(int sid) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "")) {
                String query = "SELECT COUNT(*) FROM students WHERE sid = ? AND semester = 1";
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

    // Method to check if subjects are valid for Semester 1
    private boolean areSubjectsValid() {
        int[] subIds = {1, 2, 3, 4, 5}; // Sub IDs for the subjects
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "")) {
                String query = "SELECT COUNT(*) FROM subjects WHERE Sub_id = ? AND Semester = 1";
                for (int id : subIds) {
                    PreparedStatement pstmt = con.prepareStatement(query);
                    pstmt.setInt(1, id);
                    ResultSet rs = pstmt.executeQuery();
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        return false; // If any subject is not valid for Semester 1
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
                String query = "SELECT COUNT(*) FROM marks WHERE sid = ? AND Semester = 1";
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
    private boolean saveMarksToDatabase(int sid, int cfa, int sat, int eng, int math, int dl) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "")) {
                String query = "INSERT INTO marks (sid, Sub_id, Marks, Semester) VALUES (?, ?, ?, 1)";
                PreparedStatement pstmt = con.prepareStatement(query);
                
                // Insert each subject's marks
                int[] subjects = {1, 2, 3, 4, 5}; // Subject IDs
                int[] marks = {cfa, sat, eng, math, dl}; // Marks to insert
                
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
        SwingUtilities.invokeLater(FirstSemesterMarks::new);
    }
}