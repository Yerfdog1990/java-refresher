package pattern.IoC.factory_design;

import pattern.IoC.strategy_design.ListMovieStorage;
import pattern.IoC.strategy_design.MovieStorage;

class MovieStorageFactory {
    public static MovieStorage createStorage(String type) {
        return switch (type) {
            case "list" -> new ListMovieStorage();
            case "db" -> new DatabaseMovieStorage(); // hypothetical future class
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
}