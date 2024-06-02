/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxbookshop;


import admin.adminpanel.AdminpanelController;
import books.book.BookController;
import books.editbook.EditBookController;
import books.listbooks.ListbooksController;

import books.newbook.NewbookController;
import orders.listorders.ListordersController;
import books.sortbooks.SortbooksController;
import users.sortusers.SortusersController;
import entity.Book;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import users.listusers.ListusersController;
import users.loginform.LoginformController;
import users.newuser.NewuserController;
import users.userprofile.UserprofileController;



/**
 *
 * @author admin
 */
public class HomeController implements Initializable {
    private FXBookShop app;
    private Stage loginWindow;
    @FXML private Label lbHello;
    @FXML private Label lbInfo;
    @FXML private VBox vbContent;
    private ListordersController listordersController;
    
        private EntityManagerFactory emf;
    private EntityManager em;
    
    @FXML private EditBookController editBookController;
        public HomeController() {
        this.emf = Persistence.createEntityManagerFactory("SPTV22FXLibraryPU");
        this.em = emf.createEntityManager();
        this.app = app;
    }
    
        
        
    @FXML
    public void login(){
        loginWindow = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/users/loginform/loginform.fxml"));
        try {
            VBox vbLoginFormRoot = loader.load();
            LoginformController loginformController = loader.getController();
            loginformController.setHomeController(this);
            Scene scene = new Scene(vbLoginFormRoot,401,180);
            loginWindow.setTitle("Вход");
            loginWindow.initModality(Modality.WINDOW_MODAL);
            loginWindow.initOwner(getApp().getPrimaryStage());
            loginWindow.setScene(scene);
            loginWindow.show();
            
        } catch (Exception e) {
            
            System.out.println("error: "+e);
        }
        
    }
    
    @FXML
    private void addNewUser(){
         try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/users/newuser/newuser.fxml"));
            VBox vbNewUser = loader.load();
            NewuserController newuserController = loader.getController();
            newuserController.setHomeController(this);
            app.getPrimaryStage().setTitle("FXBookShop - Добавление нового пользователя");
            vbContent.getChildren().clear();
            vbContent.getChildren().add(vbNewUser);
            
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    @FXML
    public void showAdminPanel(){
        if(fxbookshop.FXBookShop.user == null){
            getLbInfo().getStyleClass().clear();
            getLbInfo().getStyleClass().add("infoError");
            getLbInfo().setText("Войдите в программу со своим логином!"); 
            return;
        }
        if(!fxbookshop.FXBookShop.user.getRoles().contains(fxbookshop.FXBookShop.ROLES.ADMINISTRATOR.toString())){
            getLbInfo().getStyleClass().clear();
            getLbInfo().getStyleClass().add("infoError");
            getLbInfo().setText("У вас нет прав на этот ресурс. Только для администраторов!"); 
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/admin/adminpanel/adminpanel.fxml"));
        try {
            VBox vbAdminpanelRoot = loader.load();
            AdminpanelController adminpanelController = loader.getController();
            adminpanelController.setHomeController(this);
            adminpanelController.loadUsers();
            adminpanelController.loadRoles();
            vbContent.getChildren().clear();
            vbContent.getChildren().add(vbAdminpanelRoot);
            
        } catch (Exception e) {
            System.out.println("error: "+e);
        }
    }
    
    
    
    
    @FXML
    private void userProfile() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/users/userprofile/userprofile.fxml"));
            VBox vbUserProfileRoot = loader.load();
            UserprofileController userprofileController = loader.getController();
            userprofileController.setHomeController(this);
            userprofileController.loadUser();
            app.getPrimaryStage().setTitle("FXBookShop - Профиль пользователя");
            vbContent.getChildren().clear();
            vbContent.getChildren().add(vbUserProfileRoot);
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void addNewBook(){
         try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/books/newbook/newbook.fxml"));
            VBox vbNewBook = loader.load();
            NewbookController newbookController = loader.getController();
            newbookController.setHomeController(this);
            app.getPrimaryStage().setTitle("FXBookShop - Добавление новой книги");
            this.lbInfo.setText("");
            vbContent.getChildren().clear();
            vbContent.getChildren().add(vbNewBook);
            
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
@FXML
private void bookRating() {
    try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/books/sortbooks/sortbooks.fxml"));
        VBox vbBookRating = loader.load();
        SortbooksController sortbooksController = loader.getController();
        sortbooksController.setHomeController(this);
        app.getPrimaryStage().setTitle("FXBookShop - Рейтинг книг");
        this.lbInfo.setText("");

        // Logging to debug
        System.out.println("Loaded sortbooks.fxml and got controller: " + (sortbooksController != null));

        // Ensure vbContent is not null and add content
        if (vbContent != null) {
            vbContent.getChildren().clear();
            vbContent.getChildren().add(vbBookRating);
            System.out.println("Added vbBookRating to vbContent.");
        } else {
            System.err.println("vbContent is null. Cannot add vbBookRating.");
        }

    } catch (IOException ex) {
        Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
    }
}

@FXML
private void userRating() {
    try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/users/sortusers/sortusers.fxml"));
        VBox vbBookRating = loader.load();
        SortusersController sortusersController = loader.getController();
        sortusersController.setHomeController(this);
        app.getPrimaryStage().setTitle("FXBookShop - Рейтинг пользователей");
        this.lbInfo.setText("");

        // Logging to debug
        System.out.println("Loaded sortusers.fxml and got controller: " + (sortusersController != null));

        // Ensure vbContent is not null and add content
        if (vbContent != null) {
            vbContent.getChildren().clear();
            vbContent.getChildren().add(vbBookRating);
            System.out.println("Added vbBookRating to vbContent.");
        } else {
            System.err.println("vbContent is null. Cannot add vbBookRating.");
        }

    } catch (IOException ex) {
        Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
    }
}

    @FXML 
    private void listUsers(){
         try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/users/listusers/listusers.fxml"));
            VBox vbListUsers = loader.load();
            ListusersController listusersController = loader.getController();
            listusersController.setHomeController(this);
            listusersController.loadUsers();
            app.getPrimaryStage().setTitle("FXBookShop - Список пользователей");
            this.lbInfo.setText("");
            vbContent.getChildren().clear();
            vbContent.getChildren().add(vbListUsers);
            
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML 
    private void listBooks(){
         try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/books/listbooks/listbooks.fxml"));
            VBox vbListBooks = loader.load();
            ListbooksController listbooksController = loader.getController();
            listbooksController.setHomeController(this);
            listbooksController.loadBooks();
            app.getPrimaryStage().setTitle("FXBookshop - Список книг");
            this.lbInfo.setText("");
            vbContent.getChildren().clear();
            vbContent.getChildren().add(vbListBooks);
            
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
public void listOrders() {
    try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/orders/listorders/listorders.fxml"));
        VBox vbListOrders = loader.load();
        ListordersController listordersController = loader.getController();
        listordersController.setHomeController(this);
        listordersController.loadDefaultOrders();  // Call the new method here
        app.getPrimaryStage().setTitle("FXBookShop - Список заказов");
        this.lbInfo.setText("");
        vbContent.getChildren().clear();
        vbContent.getChildren().add(vbListOrders);

    } catch (IOException ex) {
        Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
    }
}

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lbHello.setText("Добро пожаловать!");
        if (editBookController != null) {
            editBookController.setHomeController(this);
        }
       
    }    

    void setApp(FXBookShop app) {
        this.app = app;
    }

    public FXBookShop getApp() {
        return app;
    }

    public Label getLbInfo() {
        return lbInfo;
    }

    public Stage getLoginWindow() {
        return loginWindow;
    }

    void loadBooks() {
        List<Book> listBooks = app.getEntityManager()
                .createQuery("SELECT b FROM Book b")
                .getResultList();
        ObservableList books = FXCollections.observableArrayList(listBooks);
        HBox hbListBooks = new HBox();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/books/book/book.fxml"));
            VBox vbBook = loader.load();
            BookController bookController = loader.getController();
           
            
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
}
