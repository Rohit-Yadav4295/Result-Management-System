


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
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.regex.Pattern;

public class AdminHome extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection con;

    public AdminHome() {
        // Enable high-DPI scaling for 4K screens
        System.setProperty("sun.java2d.uiScale", "2.0"); // Adjust scale factor as needed

        setTitle("Admin Login");
        setSize(900, 700);  // Larger size for 4K screens
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for styling
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Admin Login"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);  // Increased spacing for 4K
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels and Fields
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));  // Larger font
        usernameField = new JTextField(30);  // Adjusted field width
        usernameField.setFont(new Font("Arial", Font.PLAIN, 18));  // Larger font

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 18));  // Larger font
        passwordField = new JPasswordField(30);  // Adjusted field width
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));  // Larger font

        // Buttons (larger and styled)
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");
        JButton signupButton = new JButton("Sign Up");

        Dimension buttonSize = new Dimension(200, 60);  // Larger button size for 4K
        loginButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);
        signupButton.setPreferredSize(buttonSize);

        // Font styling for buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 18);  // Larger font
        loginButton.setFont(buttonFont);
        backButton.setFont(buttonFont);
        signupButton.setFont(buttonFont);

        // Check for existing session
        checkExistingSession();

        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (!isValidEmail(username)) {
                    JOptionPane.showMessageDialog(null, "Invalid email format for username.", "Input Error", JOptionPane.WARNING_MESSAGE);
                } else if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both username and password.", "Input Error", JOptionPane.WARNING_MESSAGE);
                } else if (authenticateUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login Successful! Welcome, Admin.");
                    AdminSession.setCurrentUser(username); // Set session
                    saveSessionToFile(username); // Save session in file
                    setVisible(false);
                    new AdminJob(); // Open Admin Dashboard only on successful login
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Back button action
        backButton.addActionListener(e -> {
            dispose();
            new Home();
        });

        // Signup button action
        signupButton.addActionListener(e -> {
            dispose();
            new AdminRegister();
        });

        // Adding components to panel
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);
        gbc.gridy++;
        panel.add(usernameField, gbc);
        gbc.gridy++;
        panel.add(passwordLabel, gbc);
        gbc.gridy++;
        panel.add(passwordField, gbc);
        gbc.gridy++;
        panel.add(loginButton, gbc);
        gbc.gridy++;
        panel.add(backButton, gbc);
        gbc.gridy++;
        panel.add(signupButton, gbc);

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

    // Authenticate user from database
    private boolean authenticateUser(String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bca", "root", "");

            String hashedPassword = hashPassword(password);
            String sql = "SELECT * FROM admin WHERE username = ? AND password_hash = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if login successful
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return false;
        }
    }

    // Check if user is already logged in (Session via File)
    private void checkExistingSession() {
        try (BufferedReader br = new BufferedReader(new FileReader("session.txt"))) {
            String username = br.readLine();
            if (username != null && !username.isEmpty()) {
                AdminSession.setCurrentUser(username);
                JOptionPane.showMessageDialog(null, "Welcome back, " + username + "!", "Session Restored", JOptionPane.INFORMATION_MESSAGE);
                setVisible(false);
                new AdminJob(); // Redirect to dashboard
            }
        } catch (IOException ignored) {
        }
    }

    // Save session to a file (acts like a cookie)
    private void saveSessionToFile(String username) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("session.txt"))) {
            bw.write(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminHome::new);
    }
}