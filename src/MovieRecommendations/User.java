package MovieRecommendations;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int userID;
    private String userName;
    private List<Integer> viewedMoviesByID;
    private List<Integer> purchasedMoviesByID;

    User(int id, String name) {
        userID = id;
        userName = name;
        viewedMoviesByID = new ArrayList<>();
        purchasedMoviesByID = new ArrayList<>();
    }

    int getUserID() {
        return userID;
    }

    String getUserName() {
        return userName;
    }

    List<Integer> getViewedMoviesByID() {
        return viewedMoviesByID;
    }

    List<Integer> getPurchasedMoviesByID() {
        return purchasedMoviesByID;
    }

    // The following modifier methods are used to initialize the respective fields when parsing the input file
    void addViewedMovieByID(int movieID) {
        viewedMoviesByID.add(movieID);
    }

    void addPurchasedMovieByID(int movieID) {
        purchasedMoviesByID.add(movieID);
    }

    // The following was used for manual testing
    public String toString() {
        return String.format("User ID: %d\n" +
                "User name: %s\n" +
                "Viewed movies by ID: %s\n" +
                "Purchased movies by ID: %s",
                userID, userName, viewedMoviesByID.toString(), purchasedMoviesByID.toString());
    }
}
