package com.baeldung.ls.configuration;

import org.springframework.context.annotation.Import;

@Import({PersistenceConfig.class})
public class AppConfig {
}
