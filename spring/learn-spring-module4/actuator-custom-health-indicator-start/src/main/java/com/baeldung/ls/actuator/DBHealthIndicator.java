package com.baeldung.ls.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DBHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        if (isDbUP()) {
            return Health.up().build();
        } else {
            return Health.down().withDetail("Error code", 503).build();
        }
    }

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    private boolean isDbUP(){
        return false;
    }
}
