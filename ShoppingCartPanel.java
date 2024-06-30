import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class ShoppingCartPanel extends JPanel {
    private StoreApp app;
    private DefaultTableModel cartTableModel;
    private JTable cartTable;

    public ShoppingCartPanel(StoreApp app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));

        JLabel cartLabel = new JLabel("Shopping Cart", JLabel.CENTER);
        cartLabel.setFont(new Font("Arial", Font.BOLD, 24));

        cartTableModel = new DefaultTableModel(new String[]{"Product Name", "Price"}, 0);
        cartTable = new JTable(cartTableModel);
        JScrollPane cartScrollPane = new JScrollPane(cartTable);

        JButton checkoutButton = new JButton("Checkout");
        JButton backButton = new JButton("Back to Products");
        JButton removeButton = new JButton("Remove");
        checkoutButton.addActionListener(e -> {

            JOptionPane.showMessageDialog(this, "Checkout successful!");
        });

        backButton.addActionListener(e -> app.showPanel("Product"));

        removeButton.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow!= -1) {
                String productName = (String) cartTable.getValueAt(selectedRow, 0);
                app.getCartItems().remove(productName);
                cartTableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Product removed from cart successfully!");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(checkoutButton);
        buttonPanel.add(backButton);
        buttonPanel.add(removeButton);

        add(cartLabel, BorderLayout.NORTH);
        add(cartScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadCartItems();
    }

    public void loadCartItems() {
        cartTableModel.setRowCount(0);
        for (String productName : app.getCartItems().keySet()) {
            double price = app.getCartItems().get(productName);
            cartTableModel.addRow(new Object[]{productName, price});
        }
    }
}