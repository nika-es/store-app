import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class ProductPanel extends JPanel {
    private StoreApp app;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JTextField searchField;

    public ProductPanel(StoreApp app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));

        JLabel productLabel = new JLabel("Products", JLabel.CENTER);
        productLabel.setFont(new Font("Arial", Font.BOLD, 24));

        productTableModel = new DefaultTableModel(new String[]{"Product Name", "Description", "Price"}, 0);
        productTable = new JTable(productTableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane productScrollPane = new JScrollPane(productTable);

        searchField = new JTextField(20);
        searchField.addActionListener(e -> searchProducts());

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchProducts());

        JButton addToCartButton = new JButton("Add to Cart");
        JButton backButton = new JButton("Back to Login");
        JButton viewCartButton = new JButton("View Cart");

        addToCartButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow!= -1) {
                String productName = (String) productTable.getValueAt(selectedRow, 0);
                String description = (String) productTable.getValueAt(selectedRow, 1);
                double price = (double) productTable.getValueAt(selectedRow, 2);
                app.addToCart(productName, price);
                JOptionPane.showMessageDialog(this, "Product added to cart successfully!");

                app.getShoppingCartPanel().loadCartItems();
            }
        });
        JPanel buttonPanel2 = new JPanel(new FlowLayout());
        buttonPanel2.add(addToCartButton);
        buttonPanel2.add(backButton);
        buttonPanel2.add(viewCartButton);
        JButton userInfoButton = new JButton("User Info");
        userInfoButton.addActionListener(e -> app.showPanel("UserInfo"));
        buttonPanel2.add(userInfoButton);

        backButton.addActionListener(e -> app.showPanel("Login"));

        viewCartButton.addActionListener(e -> app.showPanel("ShoppingCart"));

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addToCartButton);
        buttonPanel.add(backButton);
        buttonPanel.add(viewCartButton);

        add(productLabel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.NORTH);
        add(productScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadProducts();
    }

    public void loadProducts() {
        productTableModel.setRowCount(0);
        try (ResultSet rs = DatabaseHelper.getProducts()) {
            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                productTableModel.addRow(new Object[]{name, description, price});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void searchProducts() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadProducts();
        } else {
            ResultSet rs = DatabaseHelper.searchProducts(searchTerm);
            try {
                productTableModel.setRowCount(0);
                boolean productFound = false;
                while (rs.next()) {
                    String productName = rs.getString("name").trim();
                    String description = rs.getString("description").trim();
                    double price = rs.getDouble("price");
                    productTableModel.addRow(new Object[]{productName, description, price});
                    productFound = true;
                }
                if (!productFound) {
                    JOptionPane.showMessageDialog(this, "No products found matching the search term.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}