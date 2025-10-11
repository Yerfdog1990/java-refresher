package com.baeldung.lhj;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LhjApp {

    public static void main(final String... args) {
        EntityManagerFactory emf = null;
        try{
            Logger logger = LoggerFactory.getLogger(LhjApp.class);
            logger.info("Running hibernate and jpa entity manager");

            emf = Persistence.createEntityManagerFactory("LHJ");
        } finally {
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }

}
