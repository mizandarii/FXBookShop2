/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package books.book;


import books.listbooks.ListbooksController;
import entity.Author;
import entity.Book;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import fxbookshop.HomeController;




/**
 * FXML Controller class
 *
*/

public class BookController implements Initializable {

    private ListbooksController listbooksController;
    private HomeController homeController;
    @FXML private Label lbTitleBook;
    @FXML private Label lbAuthors;
    @FXML private Label lbPublishedYear;
    @FXML private Label lbQuantity;
    @FXML private Label lbPrice;
    //@FXML private Label lbRating;
    @FXML private ImageView ivCover;
    
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize any default values or styles
        lbTitleBook.setText("Title"); // Default text
        lbAuthors.setText("Authors");
        lbPublishedYear.setText("Year");
        lbQuantity.setText("Quantity");
        //lbRating.setText("--");

        // Set event handlers if needed (e.g., for interactivity)
    }

    public void setListbooksController(ListbooksController listbooksController) {
        this.listbooksController = listbooksController;
    }
    




public void setBook(Book book) {

    lbTitleBook.setText(book.getBookName());
        for (int i = 0; i < book.getAuthors().size(); i++) {
            Author author = book.getAuthors().get(i);
            lbAuthors.setText(author.getFistname()+" "+author.getLastname());
        }

    lbPublishedYear.setText(String.valueOf(book.getYear())); 
    lbQuantity.setText(String.valueOf(book.getQuantity()));
    lbPrice.setText(String.valueOf(book.getPrice()));
    //lbRating.setText(listbooksController.bookRating());
    ivCover.setImage(new Image(new ByteArrayInputStream(book.getCover())));
}


   
    
}
