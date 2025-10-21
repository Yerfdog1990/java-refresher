package pattern.IoC.factory_design;

import pattern.IoC.strategy_design.MovieStorage;

import java.util.List;

public class DatabaseMovieStorage implements MovieStorage {
    @Override
    public void addMovie(String movieName) {

    }

    @Override
    public void findMovie(String movieName) {

    }

    @Override
    public void editMovie(String oldName, String newName) {

    }

    @Override
    public List<String> getAllMovies() {
        return List.of();
    }
}
