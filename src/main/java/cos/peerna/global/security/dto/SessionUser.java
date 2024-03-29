package cos.peerna.global.security.dto;

import cos.peerna.domain.user.model.User;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class SessionUser implements UserDetails {
    private final Long id;
    private final String login;
    private final String name;
    private final String email;
    private final String password;
    private final String imageUrl;
    private final List<GrantedAuthority> grantedAuthorities;
    private String token;

    public SessionUser(User user, String token, List<GrantedAuthority> authorities, String login) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.imageUrl = user.getImageUrl();
        this.token = token;
        this.login = login;
        this.grantedAuthorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}