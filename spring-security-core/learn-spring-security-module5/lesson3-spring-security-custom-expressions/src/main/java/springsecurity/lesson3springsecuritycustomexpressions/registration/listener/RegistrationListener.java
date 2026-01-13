package springsecurity.lesson3springsecuritycustomexpressions.registration.listener;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.service.IMyUserService;
import springsecurity.lesson3springsecuritycustomexpressions.registration.OnRegistrationCompleteEvent;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final JavaMailSender mailSender;
    private final IMyUserService service;
    private final Environment env;

    @Autowired
    public RegistrationListener(JavaMailSender mailSender, IMyUserService service, Environment env) {
        this.mailSender = mailSender;
        this.service = service;
        this.env = env;
    }

    @Override
    public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
        System.out.println("[DEBUG_LOG] RegistrationListener.onApplicationEvent called for student: " + event.getMyUser().getEmail());
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final OnRegistrationCompleteEvent event) {
        System.out.println("[DEBUG_LOG] confirmRegistration started");
        final MyUser myUser = event.getMyUser();
        final String token = UUID.randomUUID().toString();
        service.createVerificationTokenForUser(myUser, token);

        try {
            final SimpleMailMessage email = constructEmailMessage(event, myUser, token);
            System.out.println("[DEBUG_LOG] sending email to: " + email.getTo()[0]);
            mailSender.send(email);
            System.out.println("[DEBUG_LOG] email sent successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] FAILED to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //

    private SimpleMailMessage constructEmailMessage(final OnRegistrationCompleteEvent event, final MyUser myUser, final String token) {
        final String recipientAddress = myUser.getEmail();
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
