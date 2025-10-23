package com.baeldung.ls.persistence.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Autowired
    private DatabaseConfig dbConfig;

    public void connect() {
        System.out.println("Connecting to " + dbConfig.getUrl());
    }
}
