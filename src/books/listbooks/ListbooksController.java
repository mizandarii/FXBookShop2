package books.listbooks;

import books.book.BookController;
import books.editbook.EditBookController;
import entity.Book;
import entity.Order;
import entity.User;
import java.io.IOException;
import java.time.Duration;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import fxbookshop.HomeController;

public class ListbooksController {

    private HomeController homeController;
    private BookController bookContoller;
    private Stage modalWindow;

    @FXML 
    private Label lbSale;

    @FXML
    private HBox hbListBooksContent;
    
    @FXML
    private TilePane tpListBooksContent;


    //    @FXML
    //private ScrollPane scrollPane;
        
  //      @FXML
//private GridPane gridPane;

    private EntityManagerFactory emf;
    private EntityManager em;

    public ListbooksController() {
        emf = Persistence.createEntityManagerFactory("SPTV22FXLibraryPU");
        em = emf.createEntityManager();
    }

    public ListbooksController(HomeController homeContoller) {
        this.homeController = homeContoller;
    }

    public BookController getBookContoller() {
        return bookContoller;
    }

    public void setBookContoller(BookController bookContoller) {
        this.bookContoller = bookContoller;
    }

    public HomeController getHomeContoller() {
        return homeController;
    }

    public void setHomeController(HomeController homeContoller) {
        this.homeController = homeContoller;
    }





    public void loadBooks() {
        Platform.runLater(this::printCountdown);

        List<Book> listBooks = homeController.getApp().getEntityManager()
                .createQuery("SELECT b FROM Book b", Book.class)
                .getResultList();

        try {
            tpListBooksContent.getChildren().clear();

            for (Book book : listBooks) {
                FXMLLoader bookLoader = new FXMLLoader();
                bookLoader.setLocation(getClass().getResource("/books/book/book.fxml"));
                VBox vbBookRoot = bookLoader.load();
                BookController bookController = bookLoader.getController();
                bookController.setListbooksController(this);
                bookController.setBook(book);

                vbBookRoot.setOnMouseEntered(event -> vbBookRoot.setCursor(Cursor.HAND));
                vbBookRoot.setOnMouseExited(event -> vbBookRoot.setCursor(Cursor.DEFAULT));
                vbBookRoot.setOnMouseClicked(event -> {
                    System.out.println("Выбрана книга: " + book.getBookName());
                    showBook(book);
                });

                tpListBooksContent.getChildren().add(vbBookRoot);
            }
            
            

        } catch (IOException ex) {
            Logger.getLogger(ListbooksController.class.getName()).log(Level.SEVERE, "not found book.fxml", ex);
        }

    }

    private void showBook(Book book) {
        try {
            modalWindow = new Stage();
            FXMLLoader bookLoader = new FXMLLoader();
            bookLoader.setLocation(getClass().getResource("/books/book/book.fxml"));
            VBox vbBookRoot = bookLoader.load();
            BookController bookController = bookLoader.getController();
            bookController.setBook(book);

            Button btTakeOn = new Button("Купить книгу");
            btTakeOn.setOnAction(event -> takeOnBookToReader(book));

            Button btEdit = new Button("Редактировать книгу");
            btEdit.setOnAction(event -> editBook(book));

            vbBookRoot.getChildren().add(btTakeOn);
            vbBookRoot.getChildren().add(btEdit);

            Scene scene = new Scene(vbBookRoot, 300, 350);
            modalWindow.setTitle(book.getBookName());
            modalWindow.initModality(Modality.WINDOW_MODAL);
            modalWindow.initOwner(getHomeContoller().getApp().getPrimaryStage());
            modalWindow.setScene(scene);
            modalWindow.show();
        } catch (IOException ex) {
            Logger.getLogger(ListbooksController.class.getName()).log(Level.SEVERE, "not found book.fxml", ex);
        }
    }


public void editBook(Book book) {
        User user = fxbookshop.FXBookShop.user;
    if (user == null) {
        getHomeContoller().getLbInfo().setText("Войдите в систему");
        getHomeContoller().login();
        modalWindow.close();
        return;
    }
    long id = user.getId();
    if(!fxbookshop.FXBookShop.user.getRoles().contains(fxbookshop.FXBookShop.ROLES.ADMINISTRATOR.toString())) {
        getHomeContoller().getLbInfo().setText("Данное действие доступно только администратору!");
        modalWindow.close();
        return;
        } else {
        try {
            modalWindow = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/books/editbook/editbook.fxml"));
            VBox vbEditBook = loader.load();
            
            // Ensure we have a controller and a book
            EditBookController controller = loader.getController();
            if (controller == null) {
                System.out.println("EditBookController is null");
                return;
            }
            controller.setHomeController(getHomeContoller());
            controller.setBook(book);
            
            
            Stage editBookStage = new Stage();
            editBookStage.setTitle(book.getBookName());
            editBookStage.initModality(Modality.WINDOW_MODAL);
            editBookStage.initOwner(getHomeContoller().getApp().getPrimaryStage());
            
            // Set the stage reference in the controller
            controller.setStage(editBookStage);
            
            Scene scene = new Scene(vbEditBook, 300, 250);
            modalWindow.setTitle(book.getBookName());
            modalWindow.initModality(Modality.WINDOW_MODAL);
            modalWindow.initOwner(getHomeContoller().getApp().getPrimaryStage());
            modalWindow.setScene(scene);
            modalWindow.show();
        } catch (IOException e) {
            System.out.println("Error opening edit book dialog: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }       }

    }

   private void takeOnBookToReader(Book book) {
    if (book == null) {
        getHomeContoller().getLbInfo().setText("Book object is null!");
        modalWindow.close();
        return;
    }

    if (book.getQuantity() <= 0) {
        getHomeContoller().getLbInfo().setText("Товара нет в наличии!");
        modalWindow.close();
        return;
    }

    User user = fxbookshop.FXBookShop.user;
    if (user == null) {
        getHomeContoller().getLbInfo().setText("Войдите в систему");
        getHomeContoller().login();
        modalWindow.close();
        return;
    }

    if (user.getClient() == null) {
        getHomeContoller().getLbInfo().setText("User client object is null!");
        modalWindow.close();
        return;
    }

    if (user.getClient().getBalance() >= book.getPrice()) {
        Order order = new Order();
        order.setBook(book);
        order.setUser(user);
        order.setOrderDate(new GregorianCalendar().getTime());
        order.setQuantity(1);
        book.setQuantity(book.getQuantity() - 1);

        user.getClient().setBalance(user.getClient().getBalance() - book.getPrice());

        getHomeContoller().getApp().getEntityManager().getTransaction().begin();
        getHomeContoller().getApp().getEntityManager().merge(book);
        getHomeContoller().getApp().getEntityManager().persist(order);
        getHomeContoller().getApp().getEntityManager().getTransaction().commit();
        getHomeContoller().getLbInfo()
                .setText("Книга " + book.getBookName()
                        + " продана покупателю: " + order.getUser().getClient().getName()
                        + " " + order.getUser().getClient().getSurname());
        modalWindow.close();
    } else {
        getHomeContoller().getLbInfo().setText("Недостаточно средств для покупки книги!");
        modalWindow.close();
    }
}


public void printCountdown() {
    LocalDateTime saleTime = LocalDateTime.of(2024, 6, 1, 12, 0);
    Duration remainingTime = Duration.between(LocalDateTime.now(), saleTime);
    long seconds = remainingTime.getSeconds();

    if (seconds > 0) {
        long days = seconds / (24 * 60 * 60);
        long hours = (seconds % (24 * 60 * 60)) / (60 * 60);
        long minutes = (seconds % (60 * 60)) / 60;

        String countdownText = "Скидка 50% на весь товар через: " + days + " дней " + hours + " часов " + minutes + " минут";
        //System.out.println("Countdown text: " + countdownText);

        Platform.runLater(() -> lbSale.setText(countdownText));
    } else {
        System.out.println("Sale has started!");
        Platform.runLater(() -> lbSale.setText("Sale has started!"));
    }
}







}


