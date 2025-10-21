package pattern.IoC.factory_design;

import pattern.IoC.strategy_design.MovieStorage;

public class MovieShop {
    private final MovieStorage storage;

    public MovieShop() {
        this.storage = MovieStorageFactory.createStorage("list");
    }

    public void demo() {
        storage.addMovie("Titanic");
        storage.editMovie("Titanic", "Titanic (1997)");
        storage.findMovie("Titanic (1997)");
    }

    public static void main(String[] args) {
        new MovieShop().demo();
    }
}