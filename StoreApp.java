import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StoreApp {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private UserProfilePanel userProfilePanel;
    private ShoppingCartPanel shoppingCartPanel;
    private AdminPanel adminPanel;
    private ManageProductsPanel manageProductsPanel;
    private ViewUsersPanel viewUsersPanel;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123";
    private HashMap<String, Double> cartItems;
    private File cartFile;

    public StoreApp() {
        DatabaseHelper.init();

        cartItems = new HashMap<>();
        cartFile = new File("cart.txt");
        loadCartItemsFromFile();

        frame = new JFrame("Store Application");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize the needed panels
        LoginPanel loginPanel = new LoginPanel(this);
        RegistrationPanel registrationPanel = new RegistrationPanel(this);
        userProfilePanel = new UserProfilePanel(this);
        ProductPanel productPanel = new ProductPanel(this);
        shoppingCartPanel = new ShoppingCartPanel(this);
        adminPanel = new AdminPanel(this);
        manageProductsPanel = new ManageProductsPanel(this);
        viewUsersPanel = new ViewUsersPanel(this);

        mainPanel.add(loginPanel, "Login");
        mainPanel.add(registrationPanel, "Register");
        mainPanel.add(userProfilePanel, "UserProfile");
        mainPanel.add(productPanel, "Product");
        mainPanel.add(shoppingCartPanel, "ShoppingCart");
        mainPanel.add(adminPanel, "Admin");
        mainPanel.add(manageProductsPanel, "ManageProducts");
        mainPanel.add(viewUsersPanel, "ViewUsers");


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(mainPanel);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveCartItemsToFile();
            }
        });
        frame.setVisible(true);


        showPanel("Login");
    }

    public void addToCart(String productName, double price) {
        cartItems.put(productName, price);
        saveCartItemsToFile(); // Save cart items to file
    }
    public void reloadProducts() {
        JPanel productPanel = getPanel("Product");
        if (productPanel != null && productPanel instanceof ProductPanel) {
            ProductPanel productInstance = (ProductPanel) productPanel;
            productInstance.loadProducts();
        }
    }


    private void loadCartItemsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(cartFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                cartItems.put(parts[0], Double.parseDouble(parts[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCartItemsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(cartFile))) {
            for (Map.Entry<String, Double> entry : cartItems.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public JPanel getPanel(String panelName) {
        for (Component comp : mainPanel.getComponents()) {
            if (panelName.equals(comp.getName())) {
                return (JPanel) comp;
            }
        }
        return null;
    }

    public void showProductPanel() {
        cardLayout.show(mainPanel, "Product");
    }

    public HashMap<String, Double> getCartItems() {
        return cartItems;
    }

    public void showUserProfile() {
        cardLayout.show(mainPanel, "UserProfile");
    }

    public boolean isAdmin(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }


    public void logout() {
        showPanel("Login");
    }

    public boolean isUser(String username, String password) {
        return DatabaseHelper.authenticateUser(username, password);
    }

    public ShoppingCartPanel getShoppingCartPanel() {
        return shoppingCartPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StoreApp());
    }
}