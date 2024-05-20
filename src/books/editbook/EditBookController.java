package books.editbook;

import authors.newauthor.NewauthorController;
import entity.Author;
import entity.Book;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import fxbookshop.HomeController;

public class EditBookController {

    private HomeController homeController;
    private ObservableList<Author> authors;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    

    private Book book;
    private Stage stage;

    @FXML
    private TextField tfTitle;
    @FXML
    private TextField tfPublishedYear;
    @FXML
    private TextField tfPrice;
    @FXML
    private TextField tfQuantity;
    @FXML
    private ListView lvAuthors;
    
    


public void setBook(Book book) {
    this.book = book;

    // Set text fields
    tfTitle.setText(book.getBookName());
    
    String yearText = String.valueOf(book.getYear());
    tfPublishedYear.setText(yearText);
    
    String priceText = String.valueOf(book.getPrice());
    tfPrice.setText(priceText);
    
    String quantityText = String.valueOf(book.getQuantity());
    tfQuantity.setText(quantityText);

    // Convert the list of authors to ObservableList
    List<Author> authorList = book.getAuthors();
    ObservableList<Author> observableAuthors = FXCollections.observableArrayList(authorList);
    
    // Apply the custom cell factory to the ListView
    lvAuthors.setCellFactory(new Callback<ListView<Author>, ListCell<Author>>() {
        @Override
        public ListCell<Author> call(ListView<Author> param) {
            return new ListCell<Author>() {
                @Override
                protected void updateItem(Author author, boolean empty) {
                    super.updateItem(author, empty);
                    if (empty || author == null) {
                        setText(null);
                    } else {
                        setText(author.getFistname() + " " + author.getLastname());
                    }
                }
            };
        }
    });
    
    // Set the ObservableList to the ListView
    lvAuthors.setItems(observableAuthors);
}



    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public HomeController getHomeController() {
        return homeController;
    }
    
    

    @FXML
    public void handleSave() {
        handleSave(book); // Pass the book directly
    }

public void handleSave(Book book) {
    if (book == null) {
        System.out.println("Error: Book is null");
        return;
    }

    if (homeController == null) {
        System.out.println("Error: HomeController is null");
        return;
    }

    try {
        // Validate book name
        String bookName = tfTitle.getText().trim();
        if (bookName.isEmpty()) {
            System.out.println("Error: Book name is empty");
            return; // Prevent further execution
        }
        book.setBookName(bookName);

        // Validate and parse the year
        String yearText = tfPublishedYear.getText().trim();
        int publishedYear = Integer.parseInt(yearText);
        book.setYear(publishedYear);

        // Validate and parse the price
        String priceText = tfPrice.getText().trim();
        double price = Double.parseDouble(priceText);
        book.setPrice(price);

        // Update the authors in the Book
        ObservableList<Author> selectedAuthors = FXCollections.observableArrayList(lvAuthors.getItems());
        book.setAuthors(new ArrayList<>(selectedAuthors));

        // Save changes to the database
        homeController.getApp().getEntityManager().getTransaction().begin();
        homeController.getApp().getEntityManager().merge(book);
        homeController.getApp().getEntityManager().getTransaction().commit();

        homeController.getLbInfo().setText("Book updated successfully!");

    } catch (NumberFormatException e) {
        System.out.println("Error parsing numeric value: " + e.getMessage());
    } catch (Exception e) {
        System.out.println("Unexpected error: " + e.getMessage());
    }
}

    
    

@FXML
private void clickAddAuthors() {
    // Get existing authors
    List<Author> listAuthors = getHomeController().getApp().getEntityManager()
            .createQuery("SELECT a FROM Author a")
            .getResultList();
    
    ObservableList<Author> observableAuthors = FXCollections.observableArrayList(listAuthors);
    
    ListView<Author> listViewAuthorsWindow = new ListView<>(observableAuthors);
    listViewAuthorsWindow.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

    listViewAuthorsWindow.setCellFactory((ListView<Author> authors) -> new ListCell<Author>() {
        @Override
        protected void updateItem(Author author, boolean empty) {
            super.updateItem(author, empty);
            if (author != null) {
                setText(author.getFistname() + " " + author.getLastname());
            } else {
                setText(null);
            }
        }
    });

    Stage modalWindow = new Stage();

    Button selectButton = new Button("Select");
    selectButton.setOnAction(event -> {
        ObservableList<Author> selectedAuthors = listViewAuthorsWindow.getSelectionModel().getSelectedItems();

        // Clear existing authors in lvAuthors
        lvAuthors.getItems().clear();
        
        // Add the selected authors to lvAuthors
        lvAuthors.getItems().addAll(selectedAuthors);

        // Update the authors in the Book entity
        if (book != null) {
            book.setAuthors(new ArrayList<>(selectedAuthors));
        }

        modalWindow.close(); // Close the modal window
    });

    HBox hbButtons = new HBox(selectButton);
    hbButtons.setSpacing(10);
    hbButtons.setAlignment(Pos.CENTER_RIGHT);

    VBox root = new VBox(listViewAuthorsWindow, hbButtons);
    Scene scene = new Scene(root, 300, 250);
    modalWindow.setTitle("Select Authors");
    modalWindow.initModality(Modality.WINDOW_MODAL);
    modalWindow.initOwner(homeController.getApp().getPrimaryStage());
    modalWindow.setScene(scene);
    modalWindow.show();
}



}
