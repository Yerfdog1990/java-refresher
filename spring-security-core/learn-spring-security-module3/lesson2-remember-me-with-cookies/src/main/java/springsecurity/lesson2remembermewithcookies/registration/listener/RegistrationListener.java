package springsecurity.lesson2remembermewithcookies.registration.listener;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import springsecurity.lesson2remembermewithcookies.persistance.model.Student;
import springsecurity.lesson2remembermewithcookies.persistance.service.IStudentService;
import springsecurity.lesson2remembermewithcookies.registration.OnRegistrationCompleteEvent;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final JavaMailSender mailSender;
    private final IStudentService service;
    private final Environment env;

    @Autowired
    public RegistrationListener(JavaMailSender mailSender, IStudentService service, Environment env) {
        this.mailSender = mailSender;
        this.service = service;
        this.env = env;
    }

    @Override
    public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final OnRegistrationCompleteEvent event) {

        final Student student = event.getStudent();
        final String token = UUID.randomUUID().toString();
        service.createVerificationTokenForUser(student, token);

        final SimpleMailMessage email = constructEmailMessage(event, student, token);
        mailSender.send(email);
    }

    //

    private SimpleMailMessage constructEmailMessage(final OnRegistrationCompleteEvent event, final Student student, final String token) {
        final String recipientAddress = student.getEmail();
        final String subject = "Registration Confirmation";
        final String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Please open the following URL to verify your account: \r\n" + confirmationUrl);
        email.setFrom(env.getProperty("spring.mail.username"));
        return email;
    }
}
