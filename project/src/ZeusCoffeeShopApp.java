import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ZeusCoffeeShopApp extends Application {

    private ArrayList<MenuItem> menuItems = new ArrayList<>();
    private ArrayList<OrderItem> orders = new ArrayList<>();
    private Map<String, Integer> inventory = new HashMap<>();

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private ListView<String> menuListView;
    private ListView<String> orderListView;
    private Button addToOrderButton;
    private Button customizeOrderButton;
    private Button processOrderButton;

    private Stage primaryStage;

    private boolean isAdmin = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Zeous Coffee Shop");

        initializeMenu();
        initializeInventory();

        createLoginScreen();
    }



    private void createLoginScreen() {
        VBox loginBox = new VBox(10);
        loginBox.setPadding(new Insets(20, 20, 20, 20));
        loginBox.setPrefHeight(500);


        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.TOP_CENTER);
        Label titleLabel = new Label("Zeous Coffee Shop");
        titleLabel.setId("title-label");
        titleBox.getChildren().add(titleLabel);

        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();

        loginButton = new Button("Login");
        loginButton.setOnAction(event -> handleLogin());

        loginBox.getChildren().addAll(titleBox, usernameLabel, usernameField, passwordLabel, passwordField, loginButton);

        Scene loginScene = new Scene(loginBox, 500, 300);
        loginScene.getStylesheets().add(getClass().getResource("black-theme.css").toExternalForm());
        primaryStage.setScene(loginScene);


        primaryStage.setOnShown(event -> {

            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), titleLabel);
            fadeTransition.setFromValue(0.0);
            fadeTransition.setToValue(1.0);
            fadeTransition.play();
        });


        primaryStage.show();
    }




    private void initializeMenu() {
        menuItems.add(new MenuItem("Espresso", 2.50));
        menuItems.add(new MenuItem("Latte", 3.00));
        menuItems.add(new MenuItem("Cappuccino", 3.50));
        menuItems.add(new MenuItem("Mocha", 4.00));
        menuItems.add(new MenuItem("Americano", 2.75));
        menuItems.add(new MenuItem("Flat White", 3.25));
        menuItems.add(new MenuItem("Macchiato", 2.90));
        menuItems.add(new MenuItem("Affogato", 4.50));
        menuItems.add(new MenuItem("Irish Coffee", 5.50));
        menuItems.add(new MenuItem("Turkish Coffee", 4.75));
        menuItems.add(new MenuItem("Caramel Macchiato", 4.25));
        menuItems.add(new MenuItem("Cortado", 3.75));
        menuItems.add(new MenuItem("Cuban Espresso", 3.90));
        menuItems.add(new MenuItem("White Chocolate Mocha", 4.50));
        menuItems.add(new MenuItem("Decaf Coffee", 2.25));
        menuItems.add(new MenuItem("Iced Coffee", 3.50));
    }

    private void initializeInventory() {
        for (MenuItem menuItem : menuItems) {
            inventory.put(menuItem.getName(), 10);
        }
    }

    private void initializeCoffeeShopApp() {
        BorderPane borderPane = new BorderPane();

        menuListView = new ListView<>(FXCollections.observableArrayList(getMenuNamesWithPrices()));
        menuListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    getStyleClass().add("menu-item");
                }
            }
        });

        orderListView = new ListView<>(FXCollections.observableArrayList(getOrderDetails()));
        addToOrderButton = new Button("Add to Order");
        customizeOrderButton = new Button("Customize Order");
        processOrderButton = new Button("Process Order");

        Button removeFromOrderButton = new Button("Remove from Order");
        removeFromOrderButton.setOnAction(event -> handleRemoveFromOrder());


        addToOrderButton.setOnAction(event -> handleAddToOrder());
        customizeOrderButton.setOnAction(event -> handleCustomizeOrder());
        processOrderButton.setOnAction(event -> handleProcessOrder());

        VBox leftVBox = new VBox(new Label("Menu"), menuListView);
        VBox rightVBox = new VBox(new Label("Order"), orderListView);
        rightVBox.getChildren().addAll(addToOrderButton, customizeOrderButton, removeFromOrderButton, processOrderButton);

        GridPane buttonGrid = new GridPane();
        buttonGrid.setVgap(10);
        buttonGrid.setHgap(10);

        buttonGrid.add(addToOrderButton, 0, 0);
        buttonGrid.add(customizeOrderButton, 1, 0);
        buttonGrid.add(removeFromOrderButton, 0, 1);
        buttonGrid.add(processOrderButton, 1, 1);

        rightVBox.getChildren().add(buttonGrid);

        borderPane.setLeft(leftVBox);
        borderPane.setRight(rightVBox);

        Scene coffeeShopScene = new Scene(borderPane, 600, 400);
        coffeeShopScene.getStylesheets().add(getClass().getResource("black-theme.css").toExternalForm());
        primaryStage.setScene(coffeeShopScene);
        primaryStage.show();
    }

    private ArrayList<String> getMenuNamesWithPrices() {
        ArrayList<String> menuNames = new ArrayList<>();
        for (MenuItem menuItem : menuItems) {
            menuNames.add(String.format("%s - $%.2f", menuItem.getName(), menuItem.getPrice()));
        }
        return menuNames;
    }


    private ArrayList<String> getOrderDetails() {
        ArrayList<String> orderDetails = new ArrayList<>();
        for (OrderItem orderItem : orders) {
            orderDetails.add(orderItem.toString());
        }
        return orderDetails;
    }

    private void handleRemoveFromOrder() {
        String selectedItem = orderListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            OrderItem selectedOrderItem = orders.get(orderListView.getSelectionModel().getSelectedIndex());
            orders.remove(selectedOrderItem);
            updateOrderListView();
        } else {
            showAlert("No Item Selected", "Please select an item from the order to remove.");
        }
    }



    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (isValidAdmin(username, password)) {
            isAdmin = true;
            initializeCoffeeShopApp();
        } else if (isValidUser(username, password)) {
            isAdmin = false;
            initializeCoffeeShopApp();
        } else {
            showAlert("Login Failed", "Invalid username or password. Please try again.");
        }
    }

    private boolean isValidAdmin(String username, String password) {
        return username.equals("admin") && password.equals("adminpassword");
    }

    private boolean isValidUser(String username, String password) {

        return username.equals("user");
    }


    private void handleAddToOrder() {
        String selectedItem = menuListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {

            String itemName = selectedItem.split(" - ")[0];

            MenuItem selectedMenuItem = findMenuItem(itemName);
            if (selectedMenuItem != null) {
                OrderItem orderItem = new OrderItem(itemName, 1, selectedMenuItem);
                orders.add(orderItem);
                updateOrderListView();
            } else {
                showAlert("Item Not Added", "Unable to add the selected item to the order.");
            }
        }
    }


    private MenuItem findMenuItem(String itemName) {
        for (MenuItem menuItem : menuItems) {
            if (menuItem.getName().equals(itemName)) {
                return menuItem;
            }
        }
        return null;
    }






    private void handleCustomizeOrder() {
        String selectedItem = orderListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            OrderItem selectedOrderItem = orders.get(orderListView.getSelectionModel().getSelectedIndex());
            if (isAdmin) {
                showCustomizationDialog(selectedOrderItem);
            } else {
                showAlert("Access Denied", "You don't have permission to customize orders.");
            }
        }
    }

    private void handleProcessOrder() {
        double totalAmount = calculateTotalAmount();
        System.out.println("Total Amount: $" + totalAmount);

        String orderSummary = getOrderSummary();

        orders.clear();
        updateOrderListView();

        initializeInventory();


        showAlert("Order Completed", "Thank You!\nEnjoy your coffee at Zeous Coffee Shop!\n\n" + orderSummary);
    }

    private String getOrderSummary() {
        StringBuilder summary = new StringBuilder("Ordered Items:\n");
        for (OrderItem orderItem : orders) {
            summary.append(orderItem.toString()).append("\n");
        }
        double totalAmount = calculateTotalAmount();
        summary.append("\nTotal Amount: $").append(String.format("%.2f", totalAmount));
        return summary.toString();
    }




    private double calculateTotalAmount() {
        double total = 0;
        for (OrderItem orderItem : orders) {
            total += orderItem.getTotalPrice();
        }
        return total;

    }

    private void updateOrderListView() {
        orderListView.setItems(FXCollections.observableArrayList(getOrderDetails()));
    }

    private void showCustomizationDialog(OrderItem orderItem) {

        showAlert("Customization Options", "Implement customization options here.");
    }

    private void showThankYouDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Completed");
        alert.setHeaderText("Thank You!");
        alert.setContentText("Enjoy your coffee at Zeous Coffee Shop!");
        alert.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static class MenuItem {
        private String name;
        private double price;

        public MenuItem(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }

    private static class OrderItem {
        private String name;
        private int quantity;
        private MenuItem menuItem;

        public OrderItem(String name, int quantity, MenuItem menuItem) {
            this.name = name;
            this.quantity = quantity;
            this.menuItem = menuItem;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getTotalPrice() {
            return quantity * menuItem.getPrice();
        }

        @Override
        public String toString() {
            return String.format("%s x%d - $%.2f", name, quantity, getTotalPrice());
        }
    }
}

