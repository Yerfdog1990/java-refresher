package day44.group_tests;

import org.testng.annotations.Test;

public class PaymentMethodTest {
    @Test(priority = 1, groups = {"Login method", "Payment method", "Functional"})
    void payByPaypal(){
        System.out.println("Paying by PayPal");
    }

    @Test(priority = 2, groups = {"Login method", "Payment method", "Functional"})
    void payByCreditCard(){
        System.out.println("Paying by Credit Card");
    }

    @Test(priority = 3, groups = {"Login method", "Payment method", "Functional"})
    void payByBankTransfer(){
        System.out.println("Paying by Bank Transfer");
    }

    @Test(priority = 4, groups = {"Login method", "Payment method", "Functional"})
    void payByApplePay(){
        System.out.println("Paying by Apple Pay");
    }

    @Test(priority = 5, groups = {"Login method", "Payment method", "Functional"})
    void payByStripe(){
        System.out.println("Paying by Stripe");
    }
}
