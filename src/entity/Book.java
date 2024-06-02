package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Represents a book entity in the database with various attributes
 * such as id, book name, ISBN code, authors, year, price, and quantity.
 */
@Entity
@Table(name = "books")
public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bookName;
    private String isbnCode;
        @Lob
    private byte[] cover;

@OneToMany(orphanRemoval = false)
private List<Author> authors;



    private int year;
    private double price;
    private int quantity;
    private double orderRating;

    public Book() {
        // Initialize the authors list in the default constructor
        this.authors = new ArrayList<>();
    }

    public Book(Long id, String bookName, String isbnCode, int year, double price, int quantity, double orderRating, byte[] cover) {
        this.id = id;
        this.bookName = bookName;
        this.isbnCode = isbnCode;
        this.authors = new ArrayList<>(); // Initialize authors list
        this.year = year;
        this.price = price;
        this.quantity = quantity;
        this.orderRating = orderRating;
        this.cover = cover;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getIsbnCode() {
        return isbnCode;
    }

    public void setIsbnCode(String isbnCode) {
        this.isbnCode = isbnCode;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getOrderRating() {
        return orderRating;
    }

    public void setOrderRating(double orderRating) {
        this.orderRating = orderRating;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    
    @Override
    public String toString() {
        return "id: " + id + "\n" +
               "book name: " + bookName + "\n" +
               "ISBN code: " + isbnCode + "\n" +
               "authors: " + authors + "\n" +
               "published: " + year + "\n" +
               "price: " + price+
        "cover: " + cover;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Book other = (Book) obj;
        return Double.doubleToLongBits(price) == Double.doubleToLongBits(other.price) &&
               Objects.equals(bookName, other.bookName) &&
               Objects.equals(isbnCode, other.isbnCode) &&
               Objects.equals(authors, other.authors) &&
               Objects.equals(id, other.id) &&
               year == other.year;
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(id, bookName, isbnCode, authors, year, Double.doubleToLongBits(price));
        return 29 * hash;
    }
}
