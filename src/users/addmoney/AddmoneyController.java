/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package users.addmoney;

import entity.Author;
import entity.Book;
import entity.User;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import fxbookshop.HomeController;

/**
 * FXML Controller class
 *
 * @author user
 */
public class AddmoneyController implements Initializable {
    @FXML private Label lbBalance;
    @FXML private TextField tfAddMoney;
    @FXML private Button btConfirm;
    private HomeController homeController;
    private User user;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("ИНИЦИАЛИЗАЦИЯ");


    }    

    public HomeController getHomeController() {
        return homeController;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
    
    
    
public void setUser(User user) {
        this.user = user;
        if (user != null) {
            lbBalance.setText(String.valueOf(user.getClient().getBalance()));
            btConfirm.setOnAction(event -> updateBalance(user));
        }
        
                    
    lbBalance.setText(String.valueOf(user.getClient().getBalance()));
    if (user==null){
        System.out.println("user is null");
    }
    btConfirm.setOnAction(event -> updateBalance(user));
    }


public void updateBalance(User user) {
    String balanceText = tfAddMoney.getText().trim();
    
    // Check if the TextField is empty
    if (balanceText.isEmpty()) {
        // Display an error message or handle the empty case as needed
        System.out.println("text field is empty");
        return;
    }

    // Parse the balance value if the TextField is not empty
    try {
        double balance = Double.parseDouble(balanceText);
        user.getClient().setBalance(user.getClient().getBalance() + balance);

        homeController.getApp().getEntityManager().getTransaction().begin();
        homeController.getApp().getEntityManager().merge(user);
        homeController.getApp().getEntityManager().getTransaction().commit();

        homeController.getLbInfo().setText("User balance updated successfully!");
    } catch (NumberFormatException e) {
        // Handle the case where the input cannot be parsed as a double
        System.out.println("Invalid balance format: " + balanceText);
        // Display an error message or handle the invalid input as needed
    }
}
}

