package beanlifecycle;

import beanlifecycle.phase.destroy.BeanC;
import beanlifecycle.phase.destroy.BeanD;
import beanlifecycle.phase.destroy.DestroyAppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BeanCycleApp {
    public static void main(String[] args) {
        // Create and configure the application
        SpringApplication app = new SpringApplication(BeanCycleApp.class);

        // Disable web environment since we don't need a web server
        app.setWebApplicationType(WebApplicationType.NONE);

        // Start the application and get the context
        ConfigurableApplicationContext context = app.run(args);

        try {
            // Your bean lifecycle demo code would go here
            System.out.println("Application started successfully!");

            BeanC beanC = context.getBean(BeanC.class);
            beanC.destroy();

            BeanD beanD = context.getBean(BeanD.class);
            beanD.destroy();

        } finally {
            // Close the context when done, which will trigger @PreDestroy methods
            context.close();
        }
    }
}