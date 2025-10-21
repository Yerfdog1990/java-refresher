package beanlifecycle.phase.initialization;

import beanlifecycle.phase.destroy.BeanD;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("beanlifecycle.phase.usage")
public class InitializationAppConfig {
    @Bean(initMethod = "initialize")
    public BeanB beanB() {
        return new BeanB();
    }
}

