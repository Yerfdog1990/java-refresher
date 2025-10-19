package configuration.javabased.automaticwiring;

import configuration.MovieStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MovieShop {
    private final MovieStorage storage;

    // Constructor Injection
    @Autowired
    public MovieShop(MovieStorage storage) {
        this.storage = storage;
    }

    public void demo() {
        storage.addMovie("Matrix");
        storage.addMovie("Interstellar");
        storage.editMovie("Matrix", "Matrix Reloaded");
        storage.findMovie("Interstellar");
    }

//    public static void main(String[] args) {
//        // Without IoC — manual dependency creation
//        MovieStorage storage = new ListMovieStorage();
//        MovieShop shop = new MovieShop(storage);
//        shop.demo();
//    }
}
