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

public class EightSemesterMarks extends JFrame {
    private JTextField sidField;
    private JTextField opField;
    private JTextField projectField;
    private JComboBox<String> electiveIIIBox;
    private JTextField electiveIIIMarksField;
    private JComboBox<String> electiveIVBox;
    private JTextField electiveIVMarksField;

    public EightSemesterMarks() {
        setTitle("Eight Semester Marks Entry");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2, 10, 10)); // Adjusted for 8 rows and 2 columns

        // Labels and Text Fields
        JLabel sidLabel = new JLabel("SID:");
        sidField = new JTextField();

        JLabel opLabel = new JLabel("Operations Research:");
        opField = new JTextField();

        JLabel projectLabel = new JLabel("Project III:");
        projectField = new JTextField();

        JLabel electiveiiiLabel = new JLabel("Elective III:");
        String[] electiveIIIOptions = {
            "Select Elective", 
            "Database Programming (ID: 44)",
            "Geographical Information System (ID: 45)",
            "Data Analysis and Visualization (ID: 46)",
            "Machine Learning (ID: 47)"
        };
        electiveIIIBox = new JComboBox<>(electiveIIIOptions);
        electiveIIIMarksField = new JTextField();

        JLabel electiveivLabel = new JLabel("Elective IV:");
        String[] electiveIVOptions = {
            "Select Elective", 
            "Multimedia System (ID: 48)",
            "Knowledge Engineering (ID: 49)",
            "Information Security (ID: 50)",
            "Internet of Things (ID: 51)"
        };
        electiveIVBox = new JComboBox<>(electiveIVOptions);
        electiveIVMarksField = new JTextField();

        JButton saveButton = new JButton("Save");
        JButton backButton = new JButton("Back");

        // Add components to the form
        add(sidLabel);
        add(sidField);
        add(opLabel);
        add(opField);
        add(projectLabel);
        add(projectField);
        add(electiveiiiLabel);
        add(electiveIIIBox);
        add(new JLabel("Marks:")); // Label for marks input for Elective III
        add(electiveIIIMarksField);
        add(electiveivLabel);
        add(electiveIVBox);
        add(new JLabel("Marks:")); // Label for marks input for Elective IV
        add(electiveIVMarksField);
        add(saveButton);
        add(backButton); // Add the Back button

        // Action Listener for Save Button
        saveButton.addActionListener(e -> {
            String sidText = sidField.getText().trim();
            String opText = opField.getText().trim();
            String projectText = projectField.getText().trim();
            String electiveIIIMarksText = electiveIIIMarksField.getText().trim();
            String electiveIVMarksText = electiveIVMarksField.getText().trim();

            // Validate input
            if (sidText.isEmpty() || opText.isEmpty() || projectText.isEmpty() || electiveIIIMarksText.isEmpty() || electiveIVMarksText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields.");
                return;
            }

            try {
                int sid = Integer.parseInt(sidText);
                int opMarks = Integer.parseInt(opText);
                int projectMarks = Integer.parseInt(projectText);
                int electiveIIIMarks = Integer.parseInt(electiveIIIMarksText);
                int electiveIVMarks = Integer.parseInt(electiveIVMarksText);

                // Validate marks
                if (opMarks < 0 || opMarks > 100 || projectMarks < 0 || projectMarks > 100 ||
                    electiveIIIMarks < 0 || electiveIIIMarks > 100 || electiveIVMarks < 0 || electiveIVMarks > 100) {
                    JOptionPane.showMessageDialog(null, "Marks must be between 0 and 100.");
                    return;
                }

                // Check if SID exists and belongs to Semester 8
                if (!checkStudentSemester(sid)) {
                    JOptionPane.showMessageDialog(null, "SID does not exist or the student is not in Semester 8.");
                    return;
                }

                // Check for data duplication
                if (isDuplicateEntry(sid)) {
                    JOptionPane.showMessageDialog(null, "Marks for this SID already exist in the Marks database.");
                    return;
                }

                // Get sub_ids for selected electives
                int electiveIIIId = getSubIdForElective((String) electiveIIIBox.getSelectedItem());
                int electiveIVId = getSubIdForElective((String) electiveIVBox.getSelectedItem());

                // Validate sub_ids before saving
                if (electiveIIIId == -1 || electiveIVId == -1) {
                    JOptionPane.showMessageDialog(null, "Please select valid electives.");
                    return;
                }

                // Save marks to the database
                if (saveMarksToDatabase(sid, opMarks, projectMarks, electiveIIIMarks, electiveIIIId, electiveIVMarks, electiveIVId)) {
                    JOptionPane.showMessageDialog(null, "Marks saved successfully!");
                    // Reset the fields after successful insertion
                     sidField.setText("");
                     opField.setText("");
                     projectField.setText("");
                     electiveIIIBox.setSelectedIndex(0);  // Reset selection to "Select Elective"
                     electiveIIIMarksField.setText("");
                     electiveIVBox.setSelectedIndex(0);   // Reset selection to "Select Elective"
                     electiveIVMarksField.setText("");
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
            new SemesterSelection(); // Ensure this leads back to your Semester Selection screen
        });

        setVisible(true);
    }

    // Method to determine sub_id based on selected elective
    private int getSubIdForElective(String elective) {
        if (elective == null || elective.equals("Select Elective")) {
            return -1; // Invalid selection
        }
        switch (elective) {
            case "Database Programming (ID: 44)": return 44;
            case "Geographical Information System (ID: 45)": return 45;
            case "Data Analysis and Visualization (ID: 46)": return 46;
            case "Machine Learning (ID: 47)": return 47;
            case "Multimedia System (ID: 48)": return 48;
            case "Knowledge Engineering (ID: 49)": return 49;
            case "Information Security (ID: 50)": return 50;
            case "Internet of Things (ID: 51)": return 51;
            default: return -1; // Should not reach here
        }
    }

    // Method to check if SID exists in the students table and is in Semester 8
    private boolean checkStudentSemester(int sid) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");
            String query = "SELECT COUNT(*) FROM students WHERE sid = ? AND semester = 8";
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
    private boolean saveMarksToDatabase(int sid, int op, int project, int electiveiiiMarks, int electiveiiiId, int electiveivMarks, int electiveivId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");
            String query = "INSERT INTO marks (sid, Sub_id, Marks, Semester) VALUES (?, ?, ?, 8)";

            PreparedStatement pstmt = con.prepareStatement(query);

            // Save for Operations Research
            pstmt.setInt(1, sid);
            pstmt.setInt(2, 42); // For Operations Research
            pstmt.setInt(3, op);
            pstmt.executeUpdate();

            // Save for Project III
            pstmt.setInt(1, sid);
            pstmt.setInt(2, 43); // For Project III
            pstmt.setInt(3, project);
            pstmt.executeUpdate();

            // Save for Elective III
            pstmt.setInt(1, sid);
            pstmt.setInt(2, electiveiiiId);
            pstmt.setInt(3, electiveiiiMarks);
            pstmt.executeUpdate();

            // Save for Elective IV
            pstmt.setInt(1, sid);
            pstmt.setInt(2, electiveivId);
            pstmt.setInt(3, electiveivMarks);
            pstmt.executeUpdate();

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
        SwingUtilities.invokeLater(EightSemesterMarks::new);
    }
}