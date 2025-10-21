package pattern.IoC.strategy_design;

// Context class
public class MovieShop {
    private final MovieStorage storage;

    public MovieShop(MovieStorage storage) {
        this.storage = storage;  // injected strategy
    }

    public void demo() {
        storage.addMovie("Harry Potter");
        storage.addMovie("Cobra Squad");
        storage.editMovie("Cobra Squad", "Cobra Strike");
        storage.findMovie("Harry Potter");

        System.out.println("\nAll movies:");
        int i = 1;
        for (String movie : storage.getAllMovies()) {
            System.out.println(i++ + ". " + movie);
        }
    }

    public static void main(String[] args) {
        MovieStorage storage = new ListMovieStorage();
        MovieShop shop = new MovieShop(storage); // inject the strategy
        shop.demo();
    }
}