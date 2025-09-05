/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rohit
 */
public class AdminSession {
    private static String currentUser;

    public static void setCurrentUser(String user) {
        currentUser = user;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
        try {
            new java.io.FileWriter("session.txt", false).close(); // Clear session file
        } catch (Exception ignored) {
        }
    }
}

