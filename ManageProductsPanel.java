import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageProductsPanel extends JPanel {
    private StoreApp app;
    private DefaultListModel<String> productModel;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public ManageProductsPanel(StoreApp app) {
        this.app = app;
        this.productModel = new DefaultListModel<>();

        setLayout(new BorderLayout(10, 10));

        JLabel manageLabel = new JLabel("Manage Products", JLabel.CENTER);
        manageLabel.setFont(new Font("Arial", Font.BOLD, 24));

        tableModel = new DefaultTableModel();
        productTable = new JTable(tableModel);
        JScrollPane productScrollPane = new JScrollPane(productTable);

        JButton addButton = new JButton("Add Product");
        JButton removeButton = new JButton("Remove Selected");
        JButton searchButton = new JButton("Search Products");
        JButton backButton = new JButton("Back to Admin");

        searchField = new JTextField(20);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("Enter product name:");
                String price = JOptionPane.showInputDialog("Enter product price:");
                String description = JOptionPane.showInputDialog("Enter product description:");
                if (name != null && price != null && description != null) {
                    DatabaseHelper.addProduct(name, Double.parseDouble(price), description);
                    app.reloadProducts();
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = productTable.getSelectedRow();
                if (selectedIndex != -1) {
                    String productName = (String) tableModel.getValueAt(selectedIndex, 0);
                    if (DatabaseHelper.removeProduct(productName)) {
                        tableModel.removeRow(selectedIndex);
                    } else {
                        JOptionPane.showMessageDialog(ManageProductsPanel.this, "Failed to delete product", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText();
            if (searchTerm != null && !searchTerm.isEmpty()) {
                try (ResultSet rs = DatabaseHelper.searchProducts(searchTerm)) {
                    tableModel.setRowCount(0);
                    if (rs != null) {
                        int rowCount = 0;
                        while (rs.next()) {
                            String name = rs.getString("name");
                            double price = rs.getDouble("price");
                            String description = rs.getString("description");
                            tableModel.addRow(new Object[]{name, price, description});
                            rowCount++;
                        }
                        if (rowCount == 0) {
                            JOptionPane.showMessageDialog(this, "No products found with the search term '" + searchTerm + "'", "Search Results", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Error searching products: ResultSet is null", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error searching products: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.showPanel("Admin");
            }
        });

        JPanel panel = new JPanel();
        panel.add(addButton);
        panel.add(removeButton);
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(backButton);

        add(manageLabel, BorderLayout.NORTH);
        add(productScrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        loadProducts();
    }

    private void loadProducts() {
        try (ResultSet rs = DatabaseHelper.getProducts()) {
            tableModel.addColumn("Name");
            tableModel.addColumn("Price");
            tableModel.addColumn("Description");
            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String description = rs.getString("description");
                tableModel.addRow(new Object[]{name, price, description});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}