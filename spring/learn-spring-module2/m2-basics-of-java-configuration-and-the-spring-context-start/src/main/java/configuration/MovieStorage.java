package configuration;

import java.util.List;

public interface MovieStorage {
    void addMovie(String movieName);
    void findMovie(String movieName);
    void editMovie(String oldName, String newName);
    List<String> getAllMovies();
}
