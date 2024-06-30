import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

class ViewUsersPanel extends JPanel {
    private StoreApp app;

    public ViewUsersPanel(StoreApp app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));

        JLabel usersLabel = new JLabel("Users", JLabel.CENTER);
        usersLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> app.showPanel("Admin"));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> app.logout());

        JButton deleteUserButton = new JButton("Delete User");
        deleteUserButton.addActionListener(e -> deleteUser());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(deleteUserButton);

        try (ResultSet rs = DatabaseHelper.getUsers()) {
            if (rs != null) {
                DefaultTableModel tableModel = DatabaseHelper.buildTableModel(rs);
                JTable usersTable = new JTable(tableModel);
                usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                usersTable.getSelectionModel().addListSelectionListener(e -> {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    int selectedRow = usersTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        deleteUserButton.setEnabled(true);
                    } else {
                        deleteUserButton.setEnabled(false);
                    }
                });
                JScrollPane scrollPane = new JScrollPane(usersTable);
                add(usersLabel, BorderLayout.NORTH);
                add(scrollPane, BorderLayout.CENTER);
                add(buttonPanel, BorderLayout.SOUTH);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to retrieve users", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve users", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        int selectedRow = ((JTable) ((JScrollPane) getComponent(1)).getViewport().getView()).getSelectedRow();
        if (selectedRow >= 0) {
            int userId = (int) ((DefaultTableModel) ((JTable) ((JScrollPane) getComponent(1)).getViewport().getView()).getModel()).getValueAt(selectedRow, 0);
            DatabaseHelper.deleteUser(String.valueOf(userId));
            JOptionPane.showMessageDialog(this, "User deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            app.showPanel("ViewUsers");
        }
    }
}