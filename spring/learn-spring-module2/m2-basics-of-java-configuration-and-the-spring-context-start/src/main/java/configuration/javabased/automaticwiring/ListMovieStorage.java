package configuration.javabased.automaticwiring;

import configuration.MovieStorage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ListMovieStorage implements MovieStorage {
    private final List<String> movies = new ArrayList<>();

    public void addMovie(String movieName) {
        if (movies.contains(movieName)) {
            System.err.println(movieName + " is already added");
        } else {
            movies.add(movieName);
        }
    }

    public void findMovie(String movieName) {
        if (movies.contains(movieName)) {
            System.out.println(movieName + " is available");
        } else {
            System.err.println("Movie not found");
        }
    }

    public void editMovie(String oldName, String newName) {
        if (movies.contains(oldName)) {
            movies.set(movies.indexOf(oldName), newName);
            System.out.println("Movie renamed to " + newName);
        } else {
            System.err.println("Movie not found");
        }
    }
    public List<String> getAllMovies() {
        return movies;
    }
}