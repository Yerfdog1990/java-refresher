package configuration.xmlbased;

import configuration.MovieShop;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MovieAppXML {
    public static void main(String[] args) {
        // Create the IoC container
        ApplicationContext context = new ClassPathXmlApplicationContext("movies.xml");

        // Retrieve the main bean
        MovieShop movieShop = context.getBean(MovieShop.class);

        // Use the bean
        System.out.println("Xml-based configuration");
        movieShop.demo();
    }
}
