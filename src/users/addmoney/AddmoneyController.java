package users.addmoney;

import entity.User;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import fxbookshop.HomeController;
import java.util.logging.Level;
import java.util.logging.Logger;
import users.userprofile.UserprofileController;

public class AddmoneyController implements Initializable {
    @FXML private Label lbBalance;
    @FXML private TextField tfAddMoney;
    @FXML private Button btConfirm;
    private HomeController homeController;
//    private User user;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User user = fxbookshop.FXBookShop.user;
        lbBalance.setText(String.valueOf(user.getClient().getBalance()));
    }    

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

//    public void setUser(User user) {
//        this.user = user;
//        if (lbBalance != null) {
//            lbBalance.setText(String.valueOf(user.getClient().getBalance()));
//        }
//    }

    @FXML
    public void updateBalance() {
        //User user = fxbookshop.FXBookShop.user;
        User user = homeController.getApp().getEntityManager().find(User.class, fxbookshop.FXBookShop.user.getId());
        String balanceText = tfAddMoney.getText().trim();

        if (balanceText.isEmpty()) {
            System.out.println("Text field is empty");
            return;
        }

        try {
            double balance = Double.parseDouble(balanceText);
            user.getClient().setBalance(user.getClient().getBalance() + balance);

            if (homeController != null) {
                if (homeController.getApp() != null) {
                    if (homeController.getApp().getEntityManager() != null) {
                        homeController.getApp().getEntityManager().getTransaction().begin();
                        homeController.getApp().getEntityManager().merge(user);
                        homeController.getApp().getEntityManager().getTransaction().commit();
                        homeController.getLbInfo().setText("User balance updated successfully!");
                    } else {
                        System.out.println("EntityManager is null");
                    }
                } else {
                    System.out.println("App is null");
                }
            } else {
                System.out.println("HomeController is null");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid balance format: " + balanceText);
        }
    }
}
