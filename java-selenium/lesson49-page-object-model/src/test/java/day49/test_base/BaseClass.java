package day49.test_base;

import org.apache.commons.lang3.RandomStringUtils;

public class BaseClass {
    // Generate random string
    public String generateRandomString(){
        String randomString = RandomStringUtils.randomAlphabetic(5);
        return randomString;
    }

    // Generate random number
    public String generateRandomNumber(){
        String randomString = RandomStringUtils.randomNumeric(10);
        return randomString;
    }

    // Generate random email
    public String generateRandomEmail(){
        String randomString = generateRandomString();
        return randomString + "@gmail.com";
    }

    // Generate random password
    public String generateRandomPassword(){
        String randomString = RandomStringUtils.randomAlphabetic(3);
        String randomNumber = RandomStringUtils.randomNumeric(3);
        return (randomString + "#" + randomNumber);
    }
}
