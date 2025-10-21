package beanlifecycle.phase.destroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("beanlifecycle.phase.destroy")
public class DestroyAppConfig {
    @Bean(destroyMethod = "destroy")
    public BeanD beanD() {
        System.out.println("destroyMethod = \"destroy\": BeanD is about to be destroyed.");
        return new BeanD();
    }
}
