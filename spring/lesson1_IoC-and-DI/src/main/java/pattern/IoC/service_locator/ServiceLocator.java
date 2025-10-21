package pattern.IoC.service_locator;

import pattern.IoC.strategy_design.ListMovieStorage;

import java.util.HashMap;
import java.util.Map;

class ServiceLocator {
    private static final Map<String, Object> services = new HashMap<>();

    static {
        services.put("movieStorage", new ListMovieStorage());
    }

    public static Object getService(String key) {
        return services.get(key);
    }
}