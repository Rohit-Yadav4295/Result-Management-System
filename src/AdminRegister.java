

/**
 *
 * @author rohit
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.regex.Pattern;

public class AdminRegister extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection con;

    public AdminRegister() {
        setTitle("Admin Registration");
        setSize(900, 700);  // Increased window size for 4K resolution
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for styling
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Admin Registration"));
        panel.setBackground(new Color(240, 240, 240));  // Light gray background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);  // Increased spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Font for labels and fields
        Font labelFont = new Font("Arial", Font.BOLD, 20);  // Larger font for labels
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);  // Larger font for fields

        // Labels and Fields
        JLabel usernameLabel = new JLabel("Username (Email):");
        usernameLabel.setFont(labelFont);
        usernameField = new JTextField(25);  // Adjusted field width
        usernameField.setFont(fieldFont);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordField = new JPasswordField(25);  // Adjusted field width
        passwordField.setFont(fieldFont);

        // Buttons
        JButton submitButton = new JButton("Submit");
        JButton backButton = new JButton("Back");

        Dimension buttonSize = new Dimension(200, 60);  // Larger button size
        submitButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);

        Font buttonFont = new Font("Arial", Font.BOLD, 18);  // Larger font for buttons
        submitButton.setFont(buttonFont);
        backButton.setFont(buttonFont);

        // Submit button action
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (!isValidEmail(username)) {
                    JOptionPane.showMessageDialog(null, "Invalid email format for username.", "Input Error", JOptionPane.WARNING_MESSAGE);
                } else if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both username and password.", "Input Error", JOptionPane.WARNING_MESSAGE);
                } else {
                    if (storeCredentials(username, password)) {
                        JOptionPane.showMessageDialog(null, "Registration Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new AdminHome(); // Redirect to Admin Home page
                    } else {
                        JOptionPane.showMessageDialog(null, "Error during registration. Try again later.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Back button action
        backButton.addActionListener(e -> {
            dispose();
            new AdminHome(); // Go back to Admin Home
        });

        // Adding components to the panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);
        gbc.gridy++;
        panel.add(usernameField, gbc);
        gbc.gridy++;
        panel.add(passwordLabel, gbc);
        gbc.gridy++;
        panel.add(passwordField, gbc);
        gbc.gridy++;
        panel.add(submitButton, gbc);
        gbc.gridy++;
        panel.add(backButton, gbc);

        add(panel);
        setVisible(true);
    }

    // Validate email format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    // Hash password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Store credentials in the database
    private boolean storeCredentials(String username, String password) {
        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");

            String hashedPassword = hashPassword(password);
            String sql = "INSERT INTO admin (username, password_hash) VALUES (?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Returns true if the insertion is successful
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminRegister::new);
    }
}