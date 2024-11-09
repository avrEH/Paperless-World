import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SwingDigiLocker extends JFrame {
    private Map<String, String> users = new HashMap<>(); // Store users (username -> password)
    private Map<String, List<String>> userDocuments = new HashMap<>(); // Store user documents (username -> document paths)
    private String currentUser;

    private JTextField usernameField, passwordField;
    private JLabel messageLabel;
    private JPanel loginPanel, dashboardPanel;

    public SwingDigiLocker() {
        // Set up the main JFrame
        setTitle("DigiLocker");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize login screen
        loginPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        messageLabel = new JLabel("", SwingConstants.CENTER);

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);
        loginPanel.add(messageLabel);

        add(loginPanel);

        // Button listeners
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> register());

        // Set up dashboard layout
        dashboardPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton uploadButton = new JButton("Upload Document");
        JButton viewButton = new JButton("View Documents");
        JButton downloadButton = new JButton("Download Document");
        JButton deleteButton = new JButton("Delete Document");
        JButton logoutButton = new JButton("Logout");

        dashboardPanel.add(uploadButton);
        dashboardPanel.add(viewButton);
        dashboardPanel.add(downloadButton);
        dashboardPanel.add(deleteButton);
        dashboardPanel.add(logoutButton);

        uploadButton.addActionListener(e -> uploadDocument());
        viewButton.addActionListener(e -> viewDocuments());
        downloadButton.addActionListener(e -> downloadDocument());
        deleteButton.addActionListener(e -> deleteDocument());
        logoutButton.addActionListener(e -> logout());
    }

    // Registration logic
    private void register() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (users.containsKey(username)) {
            messageLabel.setText("Username already exists.");
        } else {
            users.put(username, password);
            userDocuments.put(username, new ArrayList<>());
            messageLabel.setText("User registered successfully!");
        }
    }

    // Login logic
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (users.containsKey(username) && users.get(username).equals(password)) {
            messageLabel.setText("Login successful!");
            currentUser = username;
            showDashboard();
        } else {
            messageLabel.setText("Invalid username or password.");
        }
    }

    private void showDashboard() {
        getContentPane().removeAll();
        add(dashboardPanel);
        revalidate();
        repaint();
    }

    private void uploadDocument() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            userDocuments.get(currentUser).add(selectedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Document uploaded: " + selectedFile.getName());
        }
    }

    private void viewDocuments() {
        List<String> documents = userDocuments.get(currentUser);
        if (documents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No documents found.");
        } else {
            StringBuilder docList = new StringBuilder("Uploaded Documents:\n");
            for (String doc : documents) {
                docList.append(doc).append("\n");
            }

            int option = JOptionPane.showOptionDialog(this, docList.toString(), "View Documents", 
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

            if (option == JOptionPane.CLOSED_OPTION) {
                return;
            }

            openDocument(documents.get(option));
        }
    }

    private void downloadDocument() {
        String docName = JOptionPane.showInputDialog("Enter document name to download:");
        List<String> documents = userDocuments.get(currentUser);
        boolean found = false;

        for (String doc : documents) {
            if (doc.contains(docName)) {
                JOptionPane.showMessageDialog(this, "Document found: " + doc);
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Document not found.");
        }
    }

    private void deleteDocument() {
        String docName = JOptionPane.showInputDialog("Enter document name to delete:");
        List<String> documents = userDocuments.get(currentUser);
        boolean found = false;

        Iterator<String> iterator = documents.iterator();
        while (iterator.hasNext()) {
            String doc = iterator.next();
            if (doc.contains(docName)) {
                iterator.remove();
                JOptionPane.showMessageDialog(this, "Document deleted: " + doc);
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Document not found.");
        }
    }

    private void logout() {
        currentUser = null;
        getContentPane().removeAll();
        add(loginPanel);
        revalidate();
        repaint();
        messageLabel.setText("Logged out successfully.");
    }

    // Method to open a document in the system's default viewer
    private void openDocument(String docPath) {
        try {
            File file = new File(docPath);
            if (file.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot open file. Desktop not supported.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "File not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error opening file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingDigiLocker app = new SwingDigiLocker();
            app.setVisible(true);
        });
    }
}
