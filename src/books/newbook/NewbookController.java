/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package books.newbook;


import authors.newauthor.NewauthorController;
import entity.Author;
import entity.Book;
import entity.User;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import fxbookshop.HomeController;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class NewbookController implements Initializable {

    private HomeController homeController;
    private ObservableList<Author> authors;
    private VBox vbAuthorsRoot;
    @FXML private TextField tfTitle;
    @FXML private TextField tfPublishedYear;
    @FXML private TextField tfQuantity;
    @FXML private TextField tfPrice;
    
    @FXML private Button btAddAuthors;
    @FXML private ListView lvAuthors;
    @FXML private Button btAddBook;
    @FXML private Button btAddPicture;
    private File selectedFile;
    
    
    
    
    @FXML private void clickAddAuthors(){
        List<Author> listAuthors = getHomeController().getApp().getEntityManager()
                .createQuery("SELECT a FROM Author a")
                .getResultList();
        this.authors = FXCollections.observableArrayList(listAuthors);
        ListView<Author> listViewAuthorsWindow = new ListView<>(authors);
        listViewAuthorsWindow.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        listViewAuthorsWindow.setCellFactory((ListView<Author> authors) -> new ListCell<Author>() {
            @Override
            protected void updateItem(Author author, boolean empty) {
                super.updateItem(author, empty);
                if (author != null) {
                    setText(author.getFistname()+" "+author.getLastname());
                } else {
                    setText(null);
                }
            }
        });
        Stage modalWindows = new Stage();
        // Кнопка для получения выбранных авторов
        Button selectButton = new Button("Выбрать");
        selectButton.setOnAction(event -> {
            ObservableList<Author> selectedAuthors = listViewAuthorsWindow.getSelectionModel().getSelectedItems();
            
            lvAuthors.getItems().addAll(selectedAuthors);
            modalWindows.close();
        });
        Button btAddAuthors = new Button("Добавить авторов");
        btAddAuthors.setOnAction(event -> {
            Stage modalWindowsAddAuthors = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/authors/newauthor/newauthor.fxml"));
            try {
                VBox vbNewAuthor = loader.load();
                NewauthorController newauthorController=loader.getController();
                newauthorController.setNewbookController(this);
                newauthorController.setStage(modalWindowsAddAuthors);
                Scene scene = new Scene(vbNewAuthor,532,269);
                scene.getStylesheets().add(getClass().getResource("/authors/newauthor/newauthor.css").toExternalForm());
                modalWindowsAddAuthors.setTitle("Новый автор");
                modalWindowsAddAuthors.setScene(scene);
                modalWindowsAddAuthors.initModality(Modality.WINDOW_MODAL);
                modalWindowsAddAuthors.initOwner(homeController.getApp().getPrimaryStage());
                modalWindows.close();
                modalWindowsAddAuthors.show();
            } catch (Exception e) {
            }
            
        });
        HBox hbButtons = new HBox(selectButton, btAddAuthors);
        hbButtons.setSpacing(10);
        hbButtons.setAlignment(Pos.CENTER_RIGHT);
        VBox root = new VBox(listViewAuthorsWindow, hbButtons);
        Scene scene = new Scene(root,300, 250);
        modalWindows.setTitle("Список авторов");
        modalWindows.initModality(Modality.WINDOW_MODAL);
        modalWindows.initOwner(homeController.getApp().getPrimaryStage());
        modalWindows.setScene(scene);
        modalWindows.show();
    }
    @FXML private void clickAddBook(){

        if(tfTitle.getText().isEmpty() || tfPublishedYear.getText().isEmpty()
                    || tfQuantity.getText().isEmpty() || lvAuthors.getItems().isEmpty()){
            homeController.getLbInfo().getStyleClass().clear();
            homeController.getLbInfo().getStyleClass().add("infoError");
            homeController.getLbInfo().setText("Заполните все поля формы");
            return;
        }
        Book book = new Book();
        book.setBookName(tfTitle.getText());
        book.setYear(Integer.parseInt(tfPublishedYear.getText()));
        book.setQuantity(Integer.parseInt(tfQuantity.getText()));

        
        book.setPrice(Double.parseDouble(tfPrice.getText()));

        
if (lvAuthors.getItems() == null) {
    System.err.println("lvAuthors.getItems() is null");
    return; // Prevent proceeding if items is null
}

List<Author> bookAuthors = new ArrayList<>();
for (int i = 0; i < lvAuthors.getItems().size(); i++) {
    Author author = (Author) lvAuthors.getItems().get(i);
    bookAuthors.add(author);
}

if (book == null) {
    System.err.println("book is null");
    return;
}

if (book.getAuthors() == null) {
    System.err.println("book.getAuthors() is null");
    book.setAuthors(new ArrayList<>()); // Ensure it's initialized
}

if (bookAuthors == null) {
    System.err.println("bookAuthors is null");
    return;
}

if (lvAuthors.getItems() == null) {
    System.err.println("lvAuthors.getItems() is null");
    return; // Exit if null
}


        book.getAuthors().addAll(bookAuthors);
        
        try(FileInputStream fis = new FileInputStream(selectedFile)){
           byte[] fileContent = new byte[(int)selectedFile.length()];
           fis.read(fileContent);
           book.setCover(fileContent);
        }catch(Exception e){
            e.printStackTrace();
        }
        try {
            
            homeController.getApp().getEntityManager().getTransaction().begin();
                homeController.getApp().getEntityManager().persist(book);
            homeController.getApp().getEntityManager().getTransaction().commit();
            tfTitle.setText("");
            tfPublishedYear.setText("");
            tfQuantity.setText("");
            tfPrice.setText("");
            lvAuthors.getItems().clear();
            
            btAddPicture.setText("Выберите обложку");
            selectedFile=null;
            
            homeController.getLbInfo().getStyleClass().clear();
            homeController.getLbInfo().getStyleClass().add("info");
            homeController.getLbInfo().setText("Книга добавлена");
            
        } catch (Exception e) {
            homeController.getLbInfo().getStyleClass().clear();
            homeController.getLbInfo().getStyleClass().add("infoError");
            homeController.getLbInfo().setText("Книгу добавить не удалось");
        }
    }
    
    @FXML private List<Author> getListAuthors(){
        
        return new ArrayList<>();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lvAuthors.setCellFactory(new Callback<ListView<Author>,ListCell<Author>>(){
                @Override
                public ListCell<Author> call(ListView<Author> p) {
                    return new ListCell<Author>(){
                       @Override
                       protected void updateItem(Author author, boolean empty){
                           super.updateItem(author, empty);
                           if(author != null){
                               setText(author.getFistname() + " "+author.getLastname());
                           }else{
                               setText(null);
                           }
                       }
                    };
                }
            });
        btAddPicture.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");
            // Фильтры расширений файлов
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Текстовые файлы", "*.jpg"),
                    new FileChooser.ExtensionFilter("Все файлы", "*.*")
            );
            selectedFile = fileChooser.showOpenDialog(getHomeController().getApp().getPrimaryStage());
            btAddPicture.setText("Выбран файл: "+selectedFile.getName());
           
            if (selectedFile != null) {
                System.out.println("Выбранный файл: " + selectedFile.getAbsolutePath());
                // Здесь вы можете выполнить операции с выбранным файлом
            } else {
                System.out.println("Файл не выбран.");
            }
        });
    }  

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    private void createModalWindows() {
        
    }

    public HomeController getHomeController() {
        return homeController;
    }
    

    public ObservableList<Author> getAuthors() {
        return authors;
    }
    
}
