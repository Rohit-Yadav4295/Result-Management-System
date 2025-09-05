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
import java.awt.*;
import java.awt.Font;

import java.awt.event.*;
import java.awt.print.*;
import java.io.FileOutputStream;
import java.sql.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileNotFoundException;


public class MarksheetGenerator {
    private JFrame frame;
    private JTextField sidField, semesterField;
    private JTable marksheetTable;
    private JLabel studentDetailsLabel, semesterLabel, resultLabel;
    private JButton searchButton, saveButton, printButton, backButton;
    private Connection con;

    public MarksheetGenerator() {
        frame = new JFrame("Marksheet Generator");
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        topPanel.setBackground(new Color(240, 240, 240));

        sidField = new JTextField();
        semesterField = new JTextField();
        searchButton = new JButton("Search");
        styleButton(searchButton, new Color(0, 102, 204), Color.BLACK);

        topPanel.add(createStyledLabel("Enter Student ID (SID):"));
        topPanel.add(sidField);
        topPanel.add(createStyledLabel("Enter Semester:"));
        topPanel.add(semesterField);

        frame.add(topPanel, BorderLayout.NORTH);

        marksheetTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(marksheetTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bottomPanel.setBackground(new Color(240, 240, 240));

        JPanel detailsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        detailsPanel.setBackground(new Color(240, 240, 240));

        studentDetailsLabel = createStyledLabel("Student Details: ");
        semesterLabel = createStyledLabel("Semester: ");
        resultLabel = createStyledLabel("Result: ");

        detailsPanel.add(studentDetailsLabel);
        detailsPanel.add(semesterLabel);
        detailsPanel.add(resultLabel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBackground(new Color(240, 240, 240));

        saveButton = new JButton("Save PDF");
        printButton = new JButton("Print Marksheet");
        backButton = new JButton("Back");

        styleButton(saveButton, new Color(0, 153, 51), Color.BLACK);
        styleButton(printButton, new Color(255, 153, 0), Color.BLACK);
        styleButton(backButton, new Color(204, 0, 0), Color.BLACK);

        buttonsPanel.add(searchButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(printButton);
        buttonsPanel.add(backButton);

        bottomPanel.add(detailsPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> fetchData());
        saveButton.addActionListener(e -> savePDF());
        printButton.addActionListener(e -> printMarksheet());
        backButton.addActionListener(e -> {
            frame.setVisible(false);
            new AdminJob(); // Assuming AdminJob is another class in your application
        });

        frame.setVisible(true);
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Database Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchData() {
        String sidText = sidField.getText().trim();
        String semesterText = semesterField.getText().trim();

        if (sidText.isEmpty() || semesterText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both Student ID and Semester.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int sid = Integer.parseInt(sidText);
            int semester = Integer.parseInt(semesterText);

            PreparedStatement studentPs = con.prepareStatement("SELECT * FROM students WHERE sid = ?");
            studentPs.setInt(1, sid);
            ResultSet studentRs = studentPs.executeQuery();

            if (studentRs.next()) {
                studentDetailsLabel.setText("Name: " + studentRs.getString("name") + ", Course: " + studentRs.getString("course"));
                semesterLabel.setText("Current Semester: " + studentRs.getString("semester"));
            } else {
                JOptionPane.showMessageDialog(frame, "Student not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PreparedStatement marksPs = con.prepareStatement("SELECT m.Sub_id, s.Sub_name, m.Marks FROM marks m JOIN subjects s ON m.Sub_id = s.Sub_id WHERE m.sid = ? AND m.semester = ?");
            marksPs.setInt(1, sid);
            marksPs.setInt(2, semester);
            ResultSet marksRs = marksPs.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Subject");
            model.addColumn("Marks");

            int totalMarks = 0, maxMarks = 0;
            while (marksRs.next()) {
                int marks = marksRs.getInt("marks");
                String subjectName = marksRs.getString("sub_name");
                model.addRow(new Object[]{subjectName, marks});
                totalMarks += marks;
                maxMarks += 100; // Assuming each subject has a total of 100 marks
            }

            double percentage = (double) totalMarks / maxMarks * 100;
            resultLabel.setText(String.format("Percentage: %.2f%%, Grade: %s, GPA: %.2f", percentage, calculateGrade(percentage), calculateGPA(percentage)));
            marksheetTable.setModel(model);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numeric values for Student ID and Semester.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error Fetching Data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String calculateGrade(double percentage) {
        return percentage >= 90 ? "A+" : percentage >= 80 ? "A" : percentage >= 70 ? "B+" : percentage >= 60 ? "B" : "C";
    }

    private double calculateGPA(double percentage) {
        double gpa = percentage / 10;
        return Math.min(gpa, 4.0); // Ensure GPA does not exceed 4.0
    }

    private void savePDF() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("Marksheet.pdf"));
            document.open();
            document.add(new Paragraph(studentDetailsLabel.getText()));
            document.add(new Paragraph(semesterLabel.getText()));
            document.add(new Paragraph(resultLabel.getText()));

            // Add table to PDF
            PdfPTable pdfTable = new PdfPTable(2);
            pdfTable.addCell("Subject");
            pdfTable.addCell("Marks");

            DefaultTableModel model = (DefaultTableModel) marksheetTable.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                pdfTable.addCell(model.getValueAt(i, 0).toString());
                pdfTable.addCell(model.getValueAt(i, 1).toString());
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(frame, "PDF Saved Successfully!");
        } catch (DocumentException | FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Error Saving PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "An unexpected error occurred while saving the PDF.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printMarksheet() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new Printable() {
                public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
                    if (pageIndex > 0) {
                        return NO_SUCH_PAGE;
                    }
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.translate(pf.getImageableX(), pf.getImageableY());
                    g2d.scale(0.5, 0.5); // Scale down for printing
                    frame.printAll(g);
                    return PAGE_EXISTS;
                }
            });
            if (job.printDialog()) {
                job.print();
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(frame, "Error Printing Marksheet: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        return label;
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MarksheetGenerator::new);
    }
}