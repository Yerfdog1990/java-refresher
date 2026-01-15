package springsecurity.lesson1customauthenticationprovider.registration;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import springsecurity.lesson1customauthenticationprovider.persistance.model.Student;

@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final String appUrl;
    private final Student student;

    public OnRegistrationCompleteEvent(final Student student, final String appUrl) {
        super(student);
        this.student = student;
        this.appUrl = appUrl;
    }
}
