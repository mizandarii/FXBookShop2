/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authors.newauthor;

import books.editbook.EditBookController;
import books.newbook.NewbookController;
import entity.Author;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author user
 */
public class NewauthorController implements Initializable {

    private NewbookController newbookController;
    private EditBookController editBookController;
    @FXML private TextField tfFirstname;
    @FXML private TextField tfLastname;
    @FXML private Label lbInfoNewAuthor;
    private Stage stage;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    public void setNewbookController(NewbookController newbookController) {
        this.newbookController = newbookController;
    }

    public EditBookController getEditBookController() {
        return editBookController;
    }

    public void setEditBookController(EditBookController editBookController) {
        this.editBookController = editBookController;
    }
    
    
    
    @FXML private void clickAddAuthor(){
        if(tfFirstname.getText().isEmpty() || tfLastname.getText().isEmpty()){
            lbInfoNewAuthor.getStyleClass().clear();
            lbInfoNewAuthor.getStyleClass().add("infoError");
            lbInfoNewAuthor.setText("Заполните все поля формы");
            return;
        }
        Author author = new Author();
        author.setFistname(tfFirstname.getText());
        author.setLastname(tfLastname.getText());
        try {
            newbookController.getHomeController().getApp().getEntityManager().getTransaction().begin();
            newbookController.getHomeController().getApp().getEntityManager().persist(author);
            newbookController.getHomeController().getApp().getEntityManager().getTransaction().commit();
            newbookController.getHomeController().getLbInfo().getStyleClass().clear();
            newbookController.getHomeController().getLbInfo().getStyleClass().add("info");
            newbookController.getHomeController().getLbInfo().setText("Автор добавлен");
            
        } catch (Exception e) {
            newbookController.getHomeController().getLbInfo().getStyleClass().clear();
            newbookController.getHomeController().getLbInfo().getStyleClass().add("infoError");
            newbookController.getHomeController().getLbInfo().setText("Автора добавить не удалось");
        }
        this.stage.close();
    }

    public void setStage(Stage modalWindowsAddAuthors) {
        this.stage = modalWindowsAddAuthors;
    }
}
