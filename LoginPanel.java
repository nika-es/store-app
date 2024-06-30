import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private StoreApp app;

    public LoginPanel(StoreApp app) {
        this.app = app;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        JButton userLoginButton = new JButton("Login as User");
        JButton adminLoginButton = new JButton("Login as Admin");
        JButton registerButton = new JButton("Register");

        userLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                if (app.isUser(username, password)) {
                    app.showProductPanel();
                } else {
                    JOptionPane.showMessageDialog(LoginPanel.this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        adminLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                if (app.isAdmin(username, password)) {
                    app.showPanel("Admin");
                } else {
                    JOptionPane.showMessageDialog(LoginPanel.this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPanel.this, "Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (DatabaseHelper.usernameExists(username) || DatabaseHelper.emailExists(username)) {
                    JOptionPane.showMessageDialog(LoginPanel.this, "Username or email already exists", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String email = JOptionPane.showInputDialog(LoginPanel.this, "Enter email:");
                    if (email != null && !email.isEmpty()) {
                        if (DatabaseHelper.registerUser(username, password, email)) {
                            app.showUserProfile();
                        } else {
                            JOptionPane.showMessageDialog(LoginPanel.this, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(userLabel, gbc);
        gbc.gridx = 1;
        add(userField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passLabel, gbc);
        gbc.gridx = 1;
        add(passField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(userLoginButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(adminLoginButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(registerButton, gbc);
    }
}