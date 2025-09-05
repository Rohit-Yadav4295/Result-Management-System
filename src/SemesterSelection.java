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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SemesterSelection extends JFrame {

    public SemesterSelection() {
        // Frame setup
        setTitle("Semester Selection");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Label for semester selection
        JLabel titleLabel = new JLabel("Select Semester", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Semester selection combo box
        JComboBox<String> semesterComboBox = new JComboBox<>();
        
        // Adding semesters to the combo box
        for (int i = 1; i <= 8; i++) {
            semesterComboBox.addItem("Semester " + i);
        }
        semesterComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Create a panel for combo box
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        comboPanel.add(semesterComboBox);
        mainPanel.add(comboPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        // Buttons
        JButton backButton = createStyledButton("Back", Color.RED);
        JButton selectButton = createStyledButton("Select", Color.GREEN);

        // Add buttons to button panel
        buttonPanel.add(backButton);
        buttonPanel.add(selectButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Select button action
        selectButton.addActionListener(e -> {
            String selectedSemester = (String) semesterComboBox.getSelectedItem();
            if (selectedSemester != null) {
                handleSemesterSelection(selectedSemester);
            }
        });

        // Back button action - LEFT EMPTY AS REQUESTED
        backButton.addActionListener(e -> {
            // TODO: Add your back button navigation logic here
            setVisible(false);
            new AdminJob();
        });

        // Final frame setup
        pack();
        setVisible(true);
    }

    // Create styled buttons
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        return button;
    }

    // Semester selection handler
    private void handleSemesterSelection(String semester) {
        switch (semester) {
            case "Semester 1":
                openSemester1();
                break;
            case "Semester 2":
                openSemester2();
                break;
            case "Semester 3":
                openSemester3();
                break;
            case "Semester 4":
                openSemester4();
                break;
            case "Semester 5":
                openSemester5();
                break;
            case "Semester 6":
                openSemester6();
                break;
            case "Semester 7":
                openSemester7();
                break;
            case "Semester 8":
                openSemester8();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid semester selection!");
                break;
        }
    }

    // Semester-specific methods
    private void openSemester1() {
        setVisible(false);
        new FirstSemesterMarks();
    }

    private void openSemester2() {
        setVisible(false);
        new SecondSemesterMarks();
    }

    private void openSemester3() {
        
        setVisible(false);
        new ThirdSemesterMarks();
    }

    private void openSemester4() {
        setVisible(false);
        new FourthSemesterMarks();
    }

    private void openSemester5() {
        setVisible(false);
        new FifthSemesterMarks();
    }

    private void openSemester6() {
        setVisible(false);
        new SixthSemesterMarks();
    }

    private void openSemester7() {
        setVisible(false);
       new SeventhSemesterMarks();
    }

    private void openSemester8() {
       setVisible(false);
       new EightSemesterMarks();
    }

    // Main method
    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> new SemesterSelection());
    }
}