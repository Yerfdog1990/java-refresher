package pattern.IoC.service_locator;

import pattern.IoC.strategy_design.MovieStorage;

public class MovieShop {
    private final MovieStorage storage;

    public MovieShop() {
        this.storage = (MovieStorage) ServiceLocator.getService("movieStorage");
    }

    public void demo() {
        storage.addMovie("Avengers");
        storage.editMovie("Avengers", "Avengers: Endgame");
        storage.findMovie("Avengers: Endgame");
        storage.getAllMovies();
    }

    public static void main(String[] args) {
        new MovieShop().demo();
    }
}
