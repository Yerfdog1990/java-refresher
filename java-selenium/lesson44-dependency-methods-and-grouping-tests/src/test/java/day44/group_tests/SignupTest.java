package day44.group_tests;

import org.testng.annotations.Test;

public class SignupTest {

    @Test(priority = 1, groups = "Signup method")
    void signupByFacebook(){
        System.out.println("Signing up by Facebook");
    }

    @Test(priority = 2, groups = "Signup method")
    void signupByGoogle(){
        System.out.println("Signing up by Google");
    }

    @Test(priority = 3, groups = "Signup method")
    void signupByTwitter(){
        System.out.println("Signing up by Twitter");
    }

    @Test(priority = 4, groups = "Signup method")
    void signupByLinkedIn(){
        System.out.println("Signing up by LinkedIn");
    }

    @Test(priority = 5, groups = "Signup method")
    void signupByEmail(){
        System.out.println("Signing up by email");
    }
}
