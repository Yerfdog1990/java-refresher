package day44.dependency_methods;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;

public class HandlingDependencyMethodsTest {
    @Test(priority = 1)
    void openApp(){
        assertTrue(true);
        System.out.println("Opening the app");
    }

    @Test(priority = 2, dependsOnMethods = "openApp")
    void login(){
        assertTrue(false);
        System.out.println("Logging in");
    }

    @Test(priority = 3, dependsOnMethods = {"login", "openApp"})
    void search(){
        assertTrue(true);
        System.out.println("Searching");
    }

    @Test(priority = 4, dependsOnMethods = "login")
    void logout(){
        assertTrue(true);
        System.out.println("Logging out");
    }

    @Test(priority = 5, dependsOnMethods = "login")
    void closeApp(){
        assertTrue(true);
        System.out.println("Closing the app");
    }
}
