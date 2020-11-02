package MovieRecommendations;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
 * This application creates a list of most popular movies based on user and product data, as well as recommends related movies for users in session.
 *
 * NOTE: the solution provided is solely based on the correct format of the input files. Therefore:
 *      - all edge cases ahd exceptions still need to be handled
 *      - unit tests have to be written
 */

public class Solution {

    private static final int NUMBER_OF_MOST_POPULAR_MOVIES = 3;

    private static String userInputDirectory = System.getProperty("user.dir") + "\\Movie product data\\Users.txt";
    private static String productInputDirectory = System.getProperty("user.dir") + "\\Movie product data\\Products.txt";
    private static String currentUserInputDirectory = System.getProperty("user.dir") + "\\Movie product " +
            "data\\CurrentUserSession.txt";


    public static void main(String[] args) throws FileNotFoundException {
        // Lists of Users and Products (i.e. movies in this case) are created and they are indexed by their respective
        // IDs (HashMap<movieID, User/Product>)
        Map<Integer, User> users = createListOfUsers(userInputDirectory);
        Map<Integer, Product> products = createListOfProducts(productInputDirectory);

        // 1) Solving for "recent popular products"
        List<Integer> mostPopularMoviesByID = getMostPopularMoviesByID(users, products);
        // The number of most popular movies to print is determined by the class constant (i.e. '3' in this case)
        printMostPopularMovies(NUMBER_OF_MOST_POPULAR_MOVIES, mostPopularMoviesByID, products);

        // 2) Solving for recommendations on current user session data
        // The following list structure represents HashMap<movieID, viewedMovieID>
        Map<Integer, Integer> currUsers = createListOfCurrentUserSession(currentUserInputDirectory);

        printRecommendationSectionTitle();
        // Calculate and print recommendations sorted by rating for every user in session
        Set<Integer> setOfCurrUsersByID = currUsers.keySet();
        for (int currUserID : setOfCurrUsersByID) {
            List<Product> recommendedProducts = calculateRecommendedProducts(currUserID, currUsers, products);
            sortProductsByRating(recommendedProducts);

            User currUser = users.get(currUserID);
            printRecommendationsForCurrentUser(currUser, recommendedProducts);
        }
    }


    private static Map<Integer, User> createListOfUsers(String directory) throws FileNotFoundException {
        List<String[]> listOfUsersString = parseInputFile(directory);

        // The following is input file format-specific as described in README.txt:
        // "id, name, viewed (products seperated by ;), purchased (products seperated by ;)"
        Map<Integer, User> listOfUsers = new HashMap<>();
        for (String[] user : listOfUsersString) {
            
            int userID = Integer.parseInt(user[0]);
            String userName = user[1];

            User newUser = new User(userID, userName);

            String viewedMoviesByIDString = user[2];
            addViewedMoviesByID(newUser, viewedMoviesByIDString);

            String purchasedMoviesByIDString = user[3];
            addPurchasedMoviesByID(newUser, purchasedMoviesByIDString);

            listOfUsers.put(newUser.getUserID(), newUser);
        }

        return listOfUsers;
    }

    // The following two methods could be reduced to one using boolean flag for whether the 'viewed' or 'purchased'
    // movies are passed in, but that would arguably impair code readability
    private static void addViewedMoviesByID(User user, String listOfViewedMoviesByIDasString) {
        String[] listOfViewedMoviesByIDString = listOfViewedMoviesByIDasString.split(";");
        for (String movie : listOfViewedMoviesByIDString) {
            user.addViewedMovieByID(Integer.parseInt(movie));
        }
    }

    private static void addPurchasedMoviesByID(User user, String listOfPurchasedMoviesByIDasString) {
        String[] listOfPurchasedMoviesByID = listOfPurchasedMoviesByIDasString.split(";");
        for (String movieByIDasString : listOfPurchasedMoviesByID) {
            user.addPurchasedMovieByID(Integer.parseInt(movieByIDasString));
        }
    }

    private static Map<Integer, Product> createListOfProducts(String directory) throws FileNotFoundException {
        // The input file for products has to be parsed a little bit differently (c.f. comment below). Therefore, the
        // 'parseInputFile' method cannot be utilized here.
        Scanner productInput = new Scanner(new File(directory));

        List<String[]> listOfProductsString = new ArrayList<>();
        while (productInput.hasNextLine()) {
            // Some movies have less than 5 possible keywords, so splitting the original string around empty keywords
            // would skip them, resulting in arrays of strings of varying length, which would make them difficult to
            // work with later. Therefore, the int parameter '10', controlling the number of times the "," pattern is
            // applied, is used so as to conform to the input file format
            String[] productString = productInput.nextLine().split(",", 10);
            trimElementsOfTheParsedLine(productString);
            listOfProductsString.add(productString);
        }

        // The following is input file format-specific as described in README.txt:
        // "id, name, year, keyword 1, keyword 2, keyword 3, keyword 4, keyword 5, rating, price"
        Map<Integer, Product> listOfProducts = new HashMap<>();
        for (String[] product : listOfProductsString) {
            // Parameters for the constructor
            int productID = Integer.parseInt(product[0]);
            String productName = product[1];
            int productYear = Integer.parseInt(product[2]);
            List<String> productKeywords = new ArrayList<>(Arrays.asList(product).subList(3, 8));
            double productRating = convertRatingToDecimal(product[8]);
            double productPrice = Integer.parseInt(product[9]);

            Product newProduct = new Product(productID, productName, productYear, productKeywords, productRating,
                    productPrice);

            listOfProducts.put(newProduct.getProductID(), newProduct);
        }

        return listOfProducts;
    }

    private static double convertRatingToDecimal(String ratingAsString) {
        String[] splitRatingAsString = ratingAsString.split("\\.");
        if (splitRatingAsString.length == 1) {
            return 1.0 * Integer.parseInt(splitRatingAsString[0]);
        } else {
            return 1.0 * Integer.parseInt(splitRatingAsString[0]) + Integer.parseInt(splitRatingAsString[1]) / 10.0;
        }
    }

    // This method creates a list of movie IDs of the most popular movies sorted in descending order
    private static List<Integer> getMostPopularMoviesByID(Map<Integer, User> users, Map<Integer, Product> products) {
        // Upon creating the object, a popularity table by MovieID is created and populated, which would possibly be
        // handy if we had a more dynamic case at hand
        MostPopularMoviesByID mostPopularMoviesByID = new MostPopularMoviesByID(users, products);
        return mostPopularMoviesByID.getSortedList();
    }

    private static void printMostPopularMovies(int numOfMoviesToPrint, List<Integer> listOfMostPopularMoviesByID, Map<Integer, Product> products) {
        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("Most popular movies:");
        System.out.println("----------------------------------------------");
        System.out.println();
        // The entire sorted list or only the specified number of most popular movies will be printed, whichever is
        // the smallest (i.e. handling ArrayIndexOutOfBounds case)
        int nrOfMoviesToPrint = Math.min(numOfMoviesToPrint, listOfMostPopularMoviesByID.size());
        for (int i = 0; i < nrOfMoviesToPrint; i++) {
            String movieTitle = products.get(listOfMostPopularMoviesByID.get(i)).getProductName();
            System.out.printf("%2d. %s\n", i + 1, movieTitle);
                                       // 'i + 1' to make and ordered list '1, 2, 3...' as opposed to '0, 1, 2...'
        }
    }

    private static Map<Integer, Integer> createListOfCurrentUserSession(String directory) throws FileNotFoundException {
        List<String[]> listOfCurrentUsersString = parseInputFile(directory);

        // The following is input file format-specific as described in README.txt:
        // "userid, productid"
        Map<Integer, Integer> listOfCurrentUsers = new HashMap<>();
        for (String[] currUser : listOfCurrentUsersString) {
            int userID = Integer.parseInt(currUser[0]);
            int viewedMovieID = Integer.parseInt(currUser[1]);
            listOfCurrentUsers.put(userID, viewedMovieID);
        }

        return listOfCurrentUsers;
    }

    private static List<String[]> parseInputFile(String directory) throws FileNotFoundException {
        Scanner input = new Scanner(new File(directory));

        List<String[]> listOfInputLinesAsArrayOfStrings = new ArrayList<>();
        while (input.hasNextLine()) {
            String[] currLine = input.nextLine().split(",");
            trimElementsOfTheParsedLine(currLine);
            listOfInputLinesAsArrayOfStrings.add(currLine);
        }
        return listOfInputLinesAsArrayOfStrings;
    }

    private static void trimElementsOfTheParsedLine(String[] elementsOfTheParsedLine) {
        for (int i = 0; i < elementsOfTheParsedLine.length; i++) {
            elementsOfTheParsedLine[i] = elementsOfTheParsedLine[i].trim();
        }
    }

    private static void printRecommendationSectionTitle() {
        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("Recommendations for current users:");
        System.out.println("----------------------------------------------");
        System.out.println();
    }

    private static List<Product> calculateRecommendedProducts(int currUserID, Map<Integer, Integer> currUsers, Map<Integer, Product> products) {
        int currProductID = currUsers.get(currUserID);
        Product currProduct = products.get(currProductID);
        List<String> relevantKeywords = currProduct.getProductKeywords();
        List<Integer> recommendedProductsByID = new ArrayList<>();
        // The purpose of the following loop is to look for products that match by at least one keyword describing
        // the movie that is currently viewed
        for (String keyword : relevantKeywords) {   // for every keyword
            for (int productID : products.keySet()) {   // run through all products
                boolean isProductCurrentlyViewed = productID == currProductID;
                boolean hasProductBeenIncluded = recommendedProductsByID.contains(productID);
                boolean productContainsSameKeyword = products.get(productID).getProductKeywords().contains(keyword);
                if (!isProductCurrentlyViewed && !hasProductBeenIncluded && productContainsSameKeyword) {
                    recommendedProductsByID.add(productID);
                }
            }
        }

        // Creating the list of movies from the calculated list of recommended movie IDs
        List<Product> recommendedProducts = new ArrayList<>();
        for (int productID : recommendedProductsByID) {
            recommendedProducts.add(products.get(productID));
        }

        return recommendedProducts;
    }

    private static void sortProductsByRating(List<Product> list) {
        Comparator<Product> RatingComparator = new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                double rankDiff = p1.getProductRating() - p2.getProductRating();
                if (rankDiff > 0) {
                    return 1;
                } else if (rankDiff == 0) {
                    return  0;
                } else {
                    return -1;
                }
            }
        };

        // Sort the list by rating
        list.sort(RatingComparator);
    }

    private static void printRecommendationsForCurrentUser(User currUser, List<Product> recommendedProducts) {
        System.out.print(currUser.getUserName());
        // If there are no other movies of similar genre, provide no recommendations (could be modified to suggest
        // next best)
        if (recommendedProducts.size() == 0) {
            System.out.println(" has no other recommendations.");
        } else {
            System.out.println(" might also like:");
            for (Product movie : recommendedProducts) {
                System.out.println("          " + movie.getProductName());
            }
        }
        System.out.println();
    }
}
