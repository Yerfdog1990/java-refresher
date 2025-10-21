package pattern.IoC.dependency_injection;

import pattern.IoC.strategy_design.ListMovieStorage;
import pattern.IoC.strategy_design.MovieStorage;

public class MovieShop {
    private final MovieStorage storage;

    // Constructor Injection
    public MovieShop(MovieStorage storage) {
        this.storage = storage;
    }

    public void demo() {
        storage.addMovie("Matrix");
        storage.addMovie("Interstellar");
        storage.editMovie("Matrix", "Matrix Reloaded");
        storage.findMovie("Interstellar");
    }

    public static void main(String[] args) {
        // Inject dependency manually (Spring or Guice would do this automatically)
        MovieStorage storage = new ListMovieStorage();
        MovieShop shop = new MovieShop(storage);
        shop.demo();
    }
}
