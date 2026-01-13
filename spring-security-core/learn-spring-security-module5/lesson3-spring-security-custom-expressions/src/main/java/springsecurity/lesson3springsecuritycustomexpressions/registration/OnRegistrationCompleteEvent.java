package springsecurity.lesson3springsecuritycustomexpressions.registration;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;

@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final String appUrl;
    private final MyUser myUser;

    public OnRegistrationCompleteEvent(final MyUser myUser, final String appUrl) {
        super(myUser);
        this.myUser = myUser;
        this.appUrl = appUrl;
    }
}
