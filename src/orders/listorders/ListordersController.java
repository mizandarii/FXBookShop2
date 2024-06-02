package orders.listorders;

import books.listbooks.ListbooksController;
import entity.Book;
import entity.Client;
import entity.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import fxbookshop.HomeController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListordersController {
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

    public ListordersController() {
        emf = Persistence.createEntityManagerFactory("SPTV22FXLibraryPU");
        em = emf.createEntityManager();
    }

    public HomeController getHomeController() {
        return homeController;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

@FXML
public void initialize() {
    cbRating.getItems().addAll("За все время", "За год", "За месяц", "За день");

    btnSortBooks.setOnAction(event -> handleDateInput());

    if (taBooksRating == null) {
        System.err.println("taBooksRating is not initialized in initialize!");
    }

    // Manually initialize if null
    if (taBooksRating == null) {
        taBooksRating = new TextArea();
        System.err.println("taBooksRating was manually initialized!");
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
                    displayOrders(orders);
                } else {
                    showErrorAlert("Please enter a valid date.");
                }
            }
        } catch (DateTimeParseException e) {
            Logger.getLogger(ListbooksController.class.getName()).log(Level.SEVERE, "Invalid date format", e);
            showErrorAlert("Please enter the date in the correct format");
        }
    }

    public void loadDefaultOrders() {
        List<Order> orders = getListOrders();
        displayOrders(orders);
    }

    private void displayOrders(List<Order> orders) {
        if (taBooksRating == null) {
            System.err.println("taBooksRating is null in displayOrders method!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        orders.sort(Comparator.comparing(Order::getOrderDate));

        int n = 1;
        for (Order order : orders) {
            Book book = order.getBook();
            Client client = order.getUser().getClient();
            if (client == null){
                System.out.println("client is null");
            }
            int unitsSold = order.getQuantity();
            Date orderDate = order.getOrderDate();
            LocalDate localDate = orderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String formattedDate = localDate.format(formatter);

            sb.append(n)
              .append(". Книга ")
              .append(book.getBookName())
              .append(", продана ")
              .append(client.getName())
                    .append(", ")
              .append(formattedDate)
              .append(" (")
              .append(unitsSold)
              .append(" шт)")
              .append("\n");

            n++;
        }

        taBooksRating.setText(sb.toString());
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
        return em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
    }

    public List<Order> getListOrdersByYear(int year) {
        return em.createQuery("SELECT o FROM Order o WHERE FUNCTION('YEAR', o.orderDate) = :year", Order.class)
                .setParameter("year", year)
                .getResultList();
    }

    public List<Order> getListOrdersByYearAndMonth(int year, int month) {
        return em.createQuery("SELECT o FROM Order o WHERE FUNCTION('YEAR', o.orderDate) = :year AND FUNCTION('MONTH', o.orderDate) = :month", Order.class)
                .setParameter("year", year)
                .setParameter("month", month)
                .getResultList();
    }

    public List<Order> getListOrdersByDate(int year, int month, int day) {
        return em.createQuery("SELECT o FROM Order o WHERE FUNCTION('YEAR', o.orderDate) = :year AND FUNCTION('MONTH', o.orderDate) = :month AND FUNCTION('DAY', o.orderDate) = :day", Order.class)
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
}
