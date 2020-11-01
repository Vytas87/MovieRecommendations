package MovieRecommendations;

import java.util.*;

/*
 * This class serves as a separate component calculating most popular movies
 */

class MostPopularMoviesByID {
    // The following field structure represents HashMap<movieID, popularity>, where popularity is determined by
    // counting the number of times a movie was viewed and purchased.
    private Map<Integer, Integer> mostPopularMoviesByID;

    MostPopularMoviesByID(Map<Integer, User> users, Map<Integer, Product> products) {
        // Initializing the popularity table
        mostPopularMoviesByID = new HashMap<>(products.size());
        for (Product product : products.values()) {
            mostPopularMoviesByID.put(product.getProductID(), 0);
        }

        // Filling in the popularity table
        calculatePopularityOfMovies(users);
    }

    private void calculatePopularityOfMovies(Map<Integer, User> users) {
        Set<Integer> userIDSet = users.keySet();
        for (int user : userIDSet) {
            // For every viewed movie, we add 1 in the tally table for that particular movie
            for (int viewedMovieID : users.get(user).getViewedMoviesByID()) {
                mostPopularMoviesByID.put(viewedMovieID, mostPopularMoviesByID.get(viewedMovieID) + 1);
            }
            // Same for purchased movies.
            // NOTE: Purchased movies could carry a larger weight in popularity score
            // (now it is double: 1 - for viewing, 1 - for purchasing)
            for (int purchasedMovieID : users.get(user).getPurchasedMoviesByID()) {
                mostPopularMoviesByID.put(purchasedMovieID, mostPopularMoviesByID.get(purchasedMovieID) + 1);
            }
        }
    }

    Map<Integer, Integer> getMostPopularMoviesByID() {
        return mostPopularMoviesByID;
    }

    List<Integer> getSortedList() {
        // The following is the process of sorting the popularityOfMoviesByID by values (as opposed to keys) of the Map
        // structure HashMap<movieID, popularity>

        // First, a set view of the mapped pairs is created
        Set<Map.Entry<Integer, Integer>> setOfMovieIDPopularityPairs = mostPopularMoviesByID.entrySet();
        // Then, it is converted into a list (for sorting)
        List<Map.Entry<Integer, Integer>> listOfMovieIDPopularityPairs = new ArrayList<>(setOfMovieIDPopularityPairs);
        // A comparator is created that can be used to "instruct" sorting by values (popularity)
        Comparator<Map.Entry<Integer, Integer>> popularityComparator = Map.Entry.comparingByValue();
        // Last step - sort in descending order by popularity
        listOfMovieIDPopularityPairs.sort(popularityComparator.reversed());

        // Eventually, movieIDs are extracted from the sorted list movieID-Popularity pairs
        List<Integer> listOfMostPopularMoviesByID = new ArrayList<>(listOfMovieIDPopularityPairs.size());
        for (Map.Entry<Integer, Integer> movieIDPopularityPair : listOfMovieIDPopularityPairs) {
            listOfMostPopularMoviesByID.add(movieIDPopularityPair.getKey());
        }
        return listOfMostPopularMoviesByID;
    }

}
