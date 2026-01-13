package springsecurity.lesson3springsecuritycustomexpressions.persistance.service;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;

import java.util.Collection;

@Getter
public class MyUserPrincipal implements UserDetails {

    private final MyUser myUser;

    public MyUserPrincipal(MyUser myUser) {
        this.myUser = myUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return myUser.getPrivileges()
                .stream()
                .map(p -> new SimpleGrantedAuthority(p.getName()))
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return myUser.getPassword();
    }

    @Override
    public String getUsername() {
        return myUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}

