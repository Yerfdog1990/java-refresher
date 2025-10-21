package pattern.IoC.strategy_design;

import java.util.List;

// Strategy interface
public interface MovieStorage {
    void addMovie(String movieName);
    void findMovie(String movieName);
    void editMovie(String oldName, String newName);
    List<String> getAllMovies();
}
