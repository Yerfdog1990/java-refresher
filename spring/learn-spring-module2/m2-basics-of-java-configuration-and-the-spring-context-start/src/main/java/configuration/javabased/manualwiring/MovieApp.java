package configuration.javabased.manualwiring;


import configuration.MovieShop;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MovieApp {
    public static void main(String[] args) {
        // Create the IoC container
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Retrieve the main bean
        MovieShop movieShop = context.getBean(MovieShop.class);

        // Use the bean
        System.out.println("Java-based annotation configuration");
        movieShop.demo();
    }
}
