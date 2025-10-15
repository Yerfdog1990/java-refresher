package com.baeldung.lhj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baeldung.lhj.persistence.util.JpaUtil;

public class LhjApp {

    public static void main(final String... args) {
        try {
            Logger logger = LoggerFactory.getLogger(LhjApp.class);
            logger.info("Running Learn Hibernate and JPA App");
        } finally {
            JpaUtil.closeEntityManagerFactory();
        }
    }

}