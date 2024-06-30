import javax.swing.*;
import java.awt.*;

public class UserProfilePanel extends JPanel {
    private StoreApp app;
    private String username;

    public UserProfilePanel(StoreApp app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));
        JLabel profileLabel = new JLabel("Registration Successful!", JLabel.CENTER);
        profileLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton backButton = new JButton("Back to Login");

        add(profileLabel, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        backButton.addActionListener(e -> app.showPanel("Login"));
    }
}