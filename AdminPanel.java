import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminPanel extends JPanel {
    private StoreApp app;

    public AdminPanel(StoreApp app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));

        JLabel adminLabel = new JLabel("Admin Panel", JLabel.CENTER);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel userManagementPanel = new JPanel();
        userManagementPanel.setLayout(new BorderLayout(10, 10));

        JLabel userManagementLabel = new JLabel("User Management", JLabel.CENTER);
        userManagementLabel.setFont(new Font("Arial", Font.BOLD, 18));

        final ResultSet[] rs = {null};
        rs[0] = DatabaseHelper.getUsers();

        final DefaultTableModel[] tableModel = {null};
        try {
            tableModel[0] = DatabaseHelper.buildTableModel(rs[0]);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error building table model: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTable userTable = new JTable(tableModel[0]);
        JScrollPane userScrollPane = new JScrollPane(userTable);

        JButton deleteUserButton = new JButton("Delete User");
        deleteUserButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow!= -1) {
                String username = (String) userTable.getValueAt(selectedRow, 1);
                try {
                    if (DatabaseHelper.deleteUser(username)) {
                        JOptionPane.showMessageDialog(this, "User deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        rs[0] = DatabaseHelper.getUsers();
                        tableModel[0] = DatabaseHelper.buildTableModel(rs[0]);
                        userTable.setModel(tableModel[0]);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete user", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error updating table model: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        userManagementPanel.add(userManagementLabel, BorderLayout.NORTH);
        userManagementPanel.add(userScrollPane, BorderLayout.CENTER);
        userManagementPanel.add(deleteUserButton, BorderLayout.SOUTH);

        JPanel productManagementPanel = new JPanel();
        productManagementPanel.setLayout(new BorderLayout(10, 10));

        JLabel productManagementLabel = new JLabel("Product Management", JLabel.CENTER);
        productManagementLabel.setFont(new Font("Arial", Font.BOLD, 18));

        final ResultSet[] productRs = {null};
        productRs[0] = DatabaseHelper.getProducts();

        final DefaultTableModel[] productTableModel = {null};
        try {
            productTableModel[0] = DatabaseHelper.buildTableModel(productRs[0]);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error building product table model: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTable productTable = new JTable(productTableModel[0]);
        JScrollPane productScrollPane = new JScrollPane(productTable);

        JButton addProductButton = new JButton("Add Product");
        addProductButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter product name:");
            String price = JOptionPane.showInputDialog("Enter product price:");
            String description = JOptionPane.showInputDialog("Enter product description:");
            if (name!= null && price!= null && description!= null) {
                try {
                    double priceValue = Double.parseDouble(price);
                    if (DatabaseHelper.addProduct(name, priceValue, description)) {
                        JOptionPane.showMessageDialog(this, "Product added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        productRs[0] = DatabaseHelper.getProducts();
                        productTableModel[0] = DatabaseHelper.buildTableModel(productRs[0]);
                        productTable.setModel(productTableModel[0]);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add product", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error adding product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton deleteProductButton = new JButton("Delete Product");
        deleteProductButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow!= -1) {
                String productName = String.valueOf(productTable.getValueAt(selectedRow, 0));
                try {
                    if (DatabaseHelper.removeProduct(productName)) {
                        JOptionPane.showMessageDialog(this, "Product deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        ResultSet updatedProductRs = DatabaseHelper.getProducts();
                        DefaultTableModel updatedproductTableModel = DatabaseHelper.buildTableModel(updatedProductRs);
                        productTable.setModel(updatedproductTableModel);
                        productTable.clearSelection();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete product", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());

        JLabel searchLabel = new JLabel("Search Products:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText();
            if (searchTerm != null && !searchTerm.isEmpty()) {
                try (ResultSet rs2 = DatabaseHelper.searchProducts(searchTerm)) {
                    productTableModel[0].setRowCount(0);
                    while (rs2.next()) {
                        String name = rs2.getString("name");
                        double price = rs2.getDouble("price");
                        String description = rs2.getString("description");
                        productTableModel[0].addRow(new Object[]{name, price, description});
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error searching products: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        productManagementPanel.add(productManagementLabel, BorderLayout.NORTH);
        productManagementPanel.add(productScrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteProductButton);
        buttonPanel.add(addProductButton);
        buttonPanel.add(searchPanel);
        productManagementPanel.add(buttonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("User Management", userManagementPanel);
        tabbedPane.addTab("Product Management", productManagementPanel);

        add(adminLabel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> app.logout());

        add(logoutButton, BorderLayout.SOUTH);
    }
}