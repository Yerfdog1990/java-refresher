package pattern.IoC.strategy_design;

import java.util.ArrayList;
import java.util.List;

// Concrete strategy: List-based storage
public class ListMovieStorage implements MovieStorage {
    private final List<String> movies = new ArrayList<>();

    @Override
    public void addMovie(String movieName) {
        if (movies.contains(movieName)) {
            System.err.println(movieName + " is already added");
        } else {
            movies.add(movieName);
        }
    }

    @Override
    public void findMovie(String movieName) {
        if (movies.contains(movieName)) {
            System.out.println(movieName + " is available");
        } else {
            System.err.println("Movie not found");
        }
    }

    @Override
    public void editMovie(String oldName, String newName) {
        if (movies.contains(oldName)) {
            movies.set(movies.indexOf(oldName), newName);
            System.out.println("Movie renamed to " + newName);
        } else {
            System.err.println("Movie not found");
        }
    }

    @Override
    public List<String> getAllMovies() {
        return movies;
    }
}
