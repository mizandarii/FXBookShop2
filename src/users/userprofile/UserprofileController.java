package users.userprofile;

import books.listbooks.ListbooksController;
import entity.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import fxbookshop.HomeController;
import tools.PassEncrypt;
import users.addmoney.AddmoneyController;

public class UserprofileController implements Initializable {
    private HomeController homeController;
    @FXML private TextField tfFirstName;
    @FXML private TextField tfLastName;
    @FXML private TextField tfPhone;
    @FXML private TextField tfLogin;
    @FXML private TextField tfPassword;
    @FXML private TextField tfBalance;
    @FXML private Button btAddMoney;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("UserprofileController initialized");
    }    

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
        System.out.println("HomeController set in UserprofileController: " + homeController);
    }

    public HomeController getHomeController() {
        return homeController;
    }

    public void loadUser() throws Exception {
        System.out.println("Attempting to load user in UserprofileController");
        
        if (fxbookshop.FXBookShop.user == null) {
            throw new Exception("User not logged in");
        }

        if (homeController == null) {
            throw new NullPointerException("HomeController is null");
        }

        System.out.println("HomeController and user are not null");

        homeController.getLbInfo().setText("");
        tfFirstName.setText(fxbookshop.FXBookShop.user.getClient().getName());
        tfLastName.setText(fxbookshop.FXBookShop.user.getClient().getSurname());
        tfPhone.setText(fxbookshop.FXBookShop.user.getClient().getPhone());
        tfLogin.setText(fxbookshop.FXBookShop.user.getLogin());
        tfBalance.setText(roundBalance(fxbookshop.FXBookShop.user.getClient().getBalance()));

        btAddMoney.setOnAction(event -> {
            try {
                addMoney();
            } catch (Exception e) {
                Logger.getLogger(UserprofileController.class.getName()).log(Level.SEVERE, "Error adding money", e);
            }
        });
    }

    @FXML private void clickEditProfile() {
        //System.out.println("Edit Profile button clicked");
        try {
            User user = homeController.getApp().getEntityManager().find(User.class, fxbookshop.FXBookShop.user.getId());
            user.getClient().setName(tfFirstName.getText().trim());
            user.getClient().setSurname(tfLastName.getText().trim());
            user.getClient().setPhone(tfPhone.getText().trim());
            double balance = Double.parseDouble(tfBalance.getText().trim());
            user.getClient().setBalance(balance);

            if (!tfPassword.getText().isEmpty()) {
                PassEncrypt pe = new PassEncrypt();
                String encryptPass = pe.getEncryptPassword(tfPassword.getText().trim(), pe.getSalt());
                user.setPassword(encryptPass);
            }
            
            

            homeController.getApp().getEntityManager().getTransaction().begin();
            homeController.getApp().getEntityManager().merge(user.getClient());
            homeController.getApp().getEntityManager().merge(user);
            homeController.getApp().getEntityManager().getTransaction().commit();
            fxbookshop.FXBookShop.user = user;
            homeController.getLbInfo().getStyleClass().clear();
            homeController.getLbInfo().getStyleClass().add("info");
            homeController.getLbInfo().setText("Профиль изменен!");
            tfPassword.setText("");
        } catch (Exception e) {
            homeController.getLbInfo().getStyleClass().clear();
            homeController.getLbInfo().getStyleClass().add("infoError");
            homeController.getLbInfo().setText("Изменить профиль не удалось");
            homeController.login();
        }
    }

    public static String roundBalance(double balance) {
        double balanceRounded = Math.floor(balance * 100) / 100;
        return Double.toString(balanceRounded);
    }

    @FXML
    private void addMoney() {
        User user = fxbookshop.FXBookShop.user;
        //System.out.println("Attempting to add money");

        if (homeController == null) {
            //System.out.println("HomeController is null in addMoney method");
            throw new NullPointerException("HomeController is null");
        }

        if (homeController.getApp() == null) {
          //  System.out.println("HomeController's app is null in addMoney method");
            throw new NullPointerException("HomeController's app is null");
        }

        //System.out.println("HomeController and App are not null");

        try {
            //System.out.println("Loading FXML for addmoney.fxml");
            Stage modalWindow = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/users/addmoney/addmoney.fxml"));
            VBox root = loader.load();
            //System.out.println("FXML Loaded");

            AddmoneyController moneyController = loader.getController();
            
            moneyController.setHomeController(homeController);  
            moneyController.updateBalance(); 

            Scene scene = new Scene(root, 600, 201);
            modalWindow.setTitle(user.getLogin());
            modalWindow.initModality(Modality.WINDOW_MODAL);
            modalWindow.initOwner(getHomeController().getApp().getPrimaryStage());
            modalWindow.setScene(scene);
            modalWindow.show();
        } catch (IOException ex) {
            Logger.getLogger(UserprofileController.class.getName()).log(Level.SEVERE, "not found addmoney.fxml", ex);
        }
    }
}
