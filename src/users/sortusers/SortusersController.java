package users.sortusers;

import books.listbooks.ListbooksController;
import entity.Book;
import entity.Client;
import entity.Order;
import entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import fxbookshop.HomeController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.Persistence;

public class SortusersController {

    @FXML
    private ComboBox<String> cbRating;

    @FXML
    private Button btnSortBooks;
    @FXML
    private TextArea taBooksRating;
    @FXML
    private TextField tfDateInput;

    private HomeController homeController;
    private EntityManagerFactory emf;
    private EntityManager em;
    
public SortusersController() {
    try {
        emf = Persistence.createEntityManagerFactory("SPTV22FXLibraryPU");
        em = emf.createEntityManager();
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Failed to initialize EntityManager: " + e.getMessage());
    }
}

    public HomeController getHomeController() {
        return homeController;
    }

    public void setHomeController(HomeController homecontroller) {
        this.homeController = homecontroller;
    }

    
@FXML
public void initialize() {
    assert cbRating != null : "fx:id=\"cbRating\" was not injected: check your FXML file 'Sortusers.fxml'.";
    assert btnSortBooks != null : "fx:id=\"btnSortBooks\" was not injected: check your FXML file 'Sortusers.fxml'.";
    assert taBooksRating != null : "fx:id=\"taBooksRating\" was not injected: check your FXML file 'Sortusers.fxml'.";
    assert tfDateInput != null : "fx:id=\"tfDateInput\" was not injected: check your FXML file 'Sortusers.fxml'.";

    cbRating.getItems().addAll("За все время", "За год", "За месяц", "За день");
    btnSortBooks.setOnAction(event -> handleDateInput());

    if (taBooksRating == null) {
        System.err.println("Failed to initialize taBooksRating from FXML");
    }
}


@FXML
public void handleDateInput() {
    String dateInput = tfDateInput.getText();
    String selectedOption = cbRating.getValue();

    DateTimeFormatter formatter;
    LocalDate date = null;

    try {
        switch (selectedOption) {
            case "За все время":
                break;
            case "За год":
                if (!dateInput.trim().isEmpty()) {
                    formatter = DateTimeFormatter.ofPattern("yyyy");
                    date = Year.parse(dateInput, formatter).atDay(1);
                }
                break;
            case "За месяц":
                if (!dateInput.trim().isEmpty()) {
                    formatter = DateTimeFormatter.ofPattern("MM.yyyy");
                    date = YearMonth.parse(dateInput, formatter).atDay(1);
                }
                break;
            case "За день":
                if (!dateInput.trim().isEmpty()) {
                    formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    date = LocalDate.parse(dateInput, formatter);
                }
                break;
        }

        if (selectedOption != null) {
            List<Order> orders;
            if (date != null || selectedOption.equals("За все время")) {
                orders = fetchOrdersByDateOption(selectedOption, date);
                if (orders.isEmpty()) {
                    displayRatings("No orders found for the selected date range.");
                } else {
                    String ratings = clientRating(orders);
                    if (ratings == null || ratings.isEmpty()) {
                        displayRatings("No ratings generated.");
                    } else {
                        displayRatings(ratings);
                    }
                }
            } else {
                showErrorAlert("Please enter a valid date.");
            }
        }
    } catch (DateTimeParseException e) {
        Logger.getLogger(ListbooksController.class.getName()).log(Level.SEVERE, "Invalid date format", e);
        showErrorAlert("Please enter the date in the correct format");
    }
}



    private void displayRatings(String ratings) {
        taBooksRating.setText(ratings);
    }

private List<Order> fetchOrdersByDateOption(String option, LocalDate date) {
    System.out.println("Option selected: " + option);
    if (date != null) {
        System.out.println("Date: " + date);
    } else {
        System.out.println("Date is null.");
    }

    List<Order> orders;
    switch (option) {
        case "За все время":
            orders = getListOrders();
            break;
        case "За год":
            orders = getListOrdersByYear(date.getYear());
            break;
        case "За месяц":
            orders = getListOrdersByYearAndMonth(date.getYear(), date.getMonthValue());
            break;
        case "За день":
            orders = getListOrdersByDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            break;
        default:
            orders = new ArrayList<>();
            break;
    }
    System.out.println("Orders found: " + orders.size());
    return orders;
}


private List<Order> getListOrders() {
    if (em == null) {
        System.out.println("EntityManager is null!");
        return Collections.emptyList();
    }
    List<Order> orders = em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
    System.out.println("Total orders: " + orders.size());
    return orders;
}

public List<Order> getListOrdersByYear(int year) {
    List<Order> orders = em.createQuery("SELECT order FROM Order order WHERE FUNCTION('YEAR', order.orderDate) = :year", Order.class)
            .setParameter("year", year)
            .getResultList();
    System.out.println("Orders in year " + year + ": " + orders.size());
    return orders;
}

public List<Order> getListOrdersByYearAndMonth(int year, int month) {
    List<Order> orders = em.createQuery("SELECT order FROM Order order WHERE FUNCTION('YEAR', order.orderDate) = :year AND FUNCTION('MONTH', order.orderDate) = :month", Order.class)
            .setParameter("year", year)
            .setParameter("month", month)
            .getResultList();
    System.out.println("Orders in " + year + "-" + month + ": " + orders.size());
    return orders;
}

public List<Order> getListOrdersByDate(int year, int month, int day) {
    List<Order> orders = em.createQuery("SELECT order FROM Order order WHERE EXTRACT(YEAR FROM order.orderDate) = :year AND EXTRACT(MONTH FROM order.orderDate) = :month AND EXTRACT(DAY FROM order.orderDate) = :day", Order.class)
            .setParameter("year", year)
            .setParameter("month", month)
            .setParameter("day", day)
            .getResultList();
    System.out.println("Orders on " + year + "-" + month + "-" + day + ": " + orders.size());
    return orders;
}


    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Date Format");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    
public String clientRating(List<Order> orders) {
    Map<Client, Integer> clientRatingMap = new HashMap<>();
    int bookOrdersTotal = 0;

    for (Order order : orders) {
        if (order == null) {
            System.out.println("Found a null order.");
            continue;
        }

        Client client = order.getUser().getClient();
        if (client == null) {
            System.out.println("Order with null client: " + order);
            continue;
        }

        Integer unitsSold = order.getQuantity();
        if (unitsSold == null) {
            System.out.println("Order with null quantity: " + order);
            continue;
        }

        System.out.println("Processing order: Client = " + client.getName() + " " + client.getSurname() + ", Units Sold = " + unitsSold);
        bookOrdersTotal += unitsSold;
        clientRatingMap.put(client, clientRatingMap.getOrDefault(client, 0) + unitsSold);
    }

    System.out.println("Total units sold across all orders: " + bookOrdersTotal);

    Map<Client, Integer> sortedClientRatingMap = clientRatingMap.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
            ));

    double percentRough;
    double percentRounded;
    StringBuilder ratingStringBuilder = new StringBuilder();
    int n = 1;

    for (Map.Entry<Client, Integer> entry : sortedClientRatingMap.entrySet()) {
        percentRough = entry.getValue() * 100.0 / bookOrdersTotal;
        percentRounded = Math.floor(percentRough * 100) / 100;

        ratingStringBuilder.append(n)
                .append(". ")
                .append(entry.getKey().getName())
                .append(" ")
                .append(entry.getKey().getSurname())
                .append(" - ")
                .append(entry.getValue())
                .append(" шт. (")
                .append(percentRounded)
                .append("%)\n");
        n++;
    }

    System.out.println("Generated Ratings:\n" + ratingStringBuilder.toString());

    return ratingStringBuilder.toString();
}


}
