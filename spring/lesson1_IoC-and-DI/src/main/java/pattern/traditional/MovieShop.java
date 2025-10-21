package pattern.traditional;

import java.util.ArrayList;
import java.util.List;

public class MovieShop {

    /*
    "MovieShop" class is currently tightly coupled because:
        -All logic (add, find, display, future edits) lives in one class.
        -If you add new features like editMovie() or deleteMovie(), you must modify the same class, breaking the Open/Closed Principle.
        -There’s no separation of concerns — so testing or swapping behavior (e.g., using a database instead of a list) becomes difficult.

    Fortunately, we can address this problem by applying IoC. We’ll now gradually refactor your MovieShop into an IoC (Inversion of Control) architecture using four classic patterns:
        1.Strategy Pattern
        2.Service Locator Pattern
        3.Factory Pattern
        4.Dependency Injection (DI)
     */
    static List<String> movieList = new ArrayList<>();

    public static void main(String[] args) {
        addMovie("Harry Potter");
        addMovie("Cobra Squad");

        // Print the list once after adding all movies
        for (int i = 0; i < movieList.size(); i++) {
            System.out.println(i+1 + "." +movieList.get(i));
        }
        findMovie("Harry Potter");
        findMovie("Mother Africa");
    }

    public static void  addMovie(String movieName) {
        if (movieList.contains(movieName)) {
            System.err.println(movieName + " is already added.");
        } else {
            movieList.add(movieName);
        }
    }

    public static void findMovie(String movies) {
        if (movieList.contains(movies)) {
            System.out.println("\n" + movies + " is available");
        } else {
            System.err.println("\"" + movies + "\"" + " does not exist.");
        }
    }
}
