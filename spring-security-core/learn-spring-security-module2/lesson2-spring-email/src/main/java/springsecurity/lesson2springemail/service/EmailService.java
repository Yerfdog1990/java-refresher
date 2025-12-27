// Java Program to Illustrate Creation Of
// Service Interface
package springsecurity.lesson2springemail.service;

// Importing required classes
import springsecurity.lesson2springemail.entity.EmailDetails;

// Interface
public interface EmailService {

    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);

    // Method
    // To send an email with an attachment
    String sendMailWithAttachment(EmailDetails details);
}
