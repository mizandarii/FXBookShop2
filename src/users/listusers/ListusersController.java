/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package users.listusers;

import entity.User;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import fxbookshop.HomeController;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class ListusersController implements Initializable {

    private HomeController homeController;
    private ObservableList<User> users;
    @FXML private ListView lvListUsers;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void loadUsers() {
        List<User> listUsers = homeController.getApp().getEntityManager()
                .createQuery("SELECT u FROM User u")
                .getResultList();
        this.users = FXCollections.observableArrayList(listUsers);
        lvListUsers.getItems().addAll(this.users);
        lvListUsers.setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");
        lvListUsers.setCellFactory(new Callback<ListView<User>,ListCell<User>>(){
                @Override
                public ListCell<User> call(ListView<User> p) {
                    return new ListCell<User>(){
                       @Override
                       protected void updateItem(User user, boolean empty){
                           super.updateItem(user, empty);
                           if(user != null){
                               int index = getIndex();
                               setText((index + 1) + 
                                       ". "+
                                       user.getClient().getName() + " "+user.getClient().getSurname()+
                                       ". " +
                                       user.getClient().getPhone()+
                                       ". "+
                                       user.getLogin()+
                                       ". "+
                                       Arrays.toString(user.getRoles().toArray())
                                       );
                           }else{
                               setText(null);
                           }
                       }
                    };
                }
            });
    }
    
}
