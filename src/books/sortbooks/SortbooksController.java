package books.sortbooks;

import books.listbooks.ListbooksController;
import entity.Book;
import entity.Order;
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

public class SortbooksController {

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
    
    public SortbooksController() {
        emf = Persistence.createEntityManagerFactory("SPTV22FXLibraryPU");
        em = emf.createEntityManager();
    }
    public HomeController getHomeController() {
        return homeController;
    }

    public void setHomeController(HomeController homecontroller) {
        this.homeController = homecontroller;
    }

    @FXML
    public void initialize() {
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
                    String ratings = bookRating(orders);
                    displayRatings(ratings);
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
        switch (option) {
            case "За все время":
                return getListOrders();
            case "За год":
                return getListOrdersByYear(date.getYear());
            case "За месяц":
                return getListOrdersByYearAndMonth(date.getYear(), date.getMonthValue());
            case "За день":
                return getListOrdersByDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            default:
                return getListOrders();
        }
    }

private List<Order> getListOrders() {
    if (em == null) {
        System.out.println("EntityManager is null!");
        return Collections.emptyList();
    }
    List<Order> orders = em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
    return orders;
}

    public List<Order> getListOrdersByYear(int year) {
        return em.createQuery("SELECT order FROM Order order WHERE FUNCTION('YEAR', order.orderDate) = :year", Order.class)
                .setParameter("year", year)
                .getResultList();
    }

    public List<Order> getListOrdersByYearAndMonth(int year, int month) {
        return em.createQuery("SELECT order FROM Order order WHERE FUNCTION('YEAR', order.orderDate) = :year AND FUNCTION('MONTH', order.orderDate) = :month", Order.class)
                .setParameter("year", year)
                .setParameter("month", month)
                .getResultList();
    }

    public List<Order> getListOrdersByDate(int year, int month, int day) {
        return em.createQuery("SELECT order FROM Order order WHERE EXTRACT(YEAR FROM order.orderDate) = :year AND EXTRACT(MONTH FROM order.orderDate) = :month AND EXTRACT(DAY FROM order.orderDate) = :day", Order.class)
                .setParameter("year", year)
                .setParameter("month", month)
                .setParameter("day", day)
                .getResultList();
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

    public String bookRating(List<Order> orders) {
        Map<Book, Integer> bookRatingMap = new HashMap<>();
        int booksTotal = 0;

        for (Order order : orders) {
            Book book = order.getBook();
            int unitsSold = order.getQuantity();

            // Debugging statement
            System.out.println("Book: " + book.getBookName() + ", Units Sold: " + unitsSold);

            booksTotal += unitsSold;
            bookRatingMap.put(book, bookRatingMap.getOrDefault(book, 0) + unitsSold);
        }

        Map<Book, Integer> sortedBookRatingMap = bookRatingMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        StringBuilder ratingStringBuilder = new StringBuilder();
        int n = 1;
        double percentRough;
        double percentRounded;

        for (Map.Entry<Book, Integer> entry : sortedBookRatingMap.entrySet()) {
            percentRough = (entry.getValue() * 100.0) / booksTotal;
            percentRounded = Math.floor(percentRough * 100) / 100;

            ratingStringBuilder.append(n)
                    .append(". ")
                    .append(entry.getKey().getBookName())
                    .append(" - ")
                    .append(entry.getValue())
                    .append(" шт. (")
                    .append(percentRounded)
                    .append("%)\n");
            n++;
        }

        return ratingStringBuilder.toString();
    }
}
