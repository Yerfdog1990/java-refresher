package springsecurity.lesson3springsecuritycustomexpressions.persistance.testdatainitialization;

import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.Organization;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.Privilege;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.MyUserRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.OrganizationRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.PrivilegeRepository;

import java.util.Set;

@Component
public class SetupData {

    private final MyUserRepository userRepository;
    private final PrivilegeRepository privilegeRepository;
    private final OrganizationRepository organizationRepository;

    public SetupData(
            MyUserRepository userRepository,
            PrivilegeRepository privilegeRepository,
            OrganizationRepository organizationRepository
    ) {
        this.userRepository = userRepository;
        this.privilegeRepository = privilegeRepository;
        this.organizationRepository = organizationRepository;
    }

    @PostConstruct
    public void init() {
        initPrivileges();
        initOrganizations();
        initUsers();
    }

    private void initPrivileges() {
        privilegeRepository.save(new Privilege("FOO_READ_PRIVILEGE"));
        privilegeRepository.save(new Privilege("FOO_WRITE_PRIVILEGE"));
    }

    private void initOrganizations() {
        organizationRepository.save(new Organization("FirstOrg"));
        organizationRepository.save(new Organization("SecondOrg"));
    }

    private void initUsers() {
        Privilege read = privilegeRepository.findByName("FOO_READ_PRIVILEGE");
        Privilege write = privilegeRepository.findByName("FOO_WRITE_PRIVILEGE");

        MyUser john = new MyUser();
        john.setUsername("john");
        john.setPassword("{noop}123");
        john.setPrivileges(Set.of(read));
        john.setOrganization(organizationRepository.findByName("FirstOrg"));
        userRepository.save(john);

        MyUser tom = new MyUser();
        tom.setUsername("tom");
        tom.setPassword("{noop}111");
        tom.setPrivileges(Set.of(read, write));
        tom.setOrganization(organizationRepository.findByName("SecondOrg"));
        userRepository.save(tom);
    }
}
