package cos.peerna.domain.user.model;

import java.util.ArrayList;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum Role {

    UNCERTIFICATED("ROLE_UNCERTIFICATED", "CRUD_CONTENT");

    private final String name;
    private final String authorities;

    public List<GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(getName()));
        Stream.of(getAuthorities().split(","))
                .forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority)));
        return authorities;
    }
}

/*
ANONYMOUS("ROLE_ANONYMOUS") 는 Spring Security 자체가 제공함

TODO: Add more roles
 CERTIFICATED("ROLE_CERTIFICATED", "CRUD_CONTENT,CALL_GPT") for GPT CALL WITHOUT ABUSING
 ADMIN("ROLE_ADMIN,VIEW_CONTENT", "CRUD_CONTENT,CALL_GPT,BAN_IP") for ADMIN
 */
