package MovieRecommendations;

import java.util.List;

public class Product {
    private int productID;
    private String productName;
    private int productYear;
    private List<String> productKeywords;
    // Input file contains ratings to 1 decimal
    private double productRating;
    // All prices in the input file are integer
    private double productPrice;

    Product(int id, String name, int year, List<String> keywords, double rating, double price) {
        this.productID = id;
        this.productName = name;
        this.productYear = year;
        this.productKeywords = keywords;
        this.productRating = rating;
        this.productPrice = price;
    }

    int getProductID() {
        return productID;
    }

    String getProductName() {
        return productName;
    }

    int getProductYear() {
        return productYear;
    }

    List<String> getProductKeywords() {
        return productKeywords;
    }

    double getProductRating() {
        return productRating;
    }

    double getProductPrice() {
        return productPrice;
    }

    // The following modifier methods are more in the spirit of scalability rather than the scope of the case itself
    void updateRating(double newRating) {
        productRating = newRating;
    }

    void updatePrice(double newPrice) {
        productPrice = newPrice;
    }

    // The following was used for manual testing
    public String toString() {
        return String.format("Product ID: %d\n" +
                "Product name: %s\n" +
                "Product year: %d\n" +
                "Product keywords: %s\n" +
                "Product rating: %.1f\n" +
                "Product price: %.2f",
                productID, productName, productYear, productKeywords.toString(), productRating, productPrice);
    }
}