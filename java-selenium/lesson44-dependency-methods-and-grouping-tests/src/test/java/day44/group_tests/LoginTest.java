package day44.group_tests;

import org.testng.annotations.Test;

public class LoginTest {
    @Test(priority = 1, groups = "Login method")
    void loginByFacebook(){
        System.out.println("Logging in by Facebook");
    }

    @Test(priority = 2, groups = "Login method")
    void loginByGoogle(){
        System.out.println("Logging in by Google");
    }

    @Test(priority = 3, groups = "Login method")
    void loginByTwitter(){
        System.out.println("Logging in by Twitter");
    }

    @Test(priority = 4, groups = "Login method")
    void loginByLinkedIn(){
        System.out.println("Logging in by LinkedIn");
    }

    @Test(priority = 5, groups = "Login method")
    void loginByEmail(){
        System.out.println("Logging in by email");
    }
}
