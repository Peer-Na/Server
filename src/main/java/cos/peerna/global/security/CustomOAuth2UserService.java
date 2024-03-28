package cos.peerna.global.security;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import cos.peerna.domain.user.model.Role;
import cos.peerna.global.security.dto.OAuthAttributes;
import cos.peerna.global.security.dto.SessionUser;
import cos.peerna.domain.user.model.User;
import cos.peerna.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        String userEmail = null;
        if (registrationId.equals("github"))
            userEmail = getUserEmail(userRequest);

        String token = userRequest.getAccessToken().getTokenValue();
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes(), userEmail, token);
        User user = saveOrUpdate(attributes);
        List<GrantedAuthority> authorities = user.getRole().getGrantedAuthorities();
        httpSession.setAttribute("user", new SessionUser(user, token, authorities, attributes.getLogin()));

        return new DefaultOAuth2User(
                authorities,
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private static String getUserEmail(OAuth2UserRequest userRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + userRequest.getAccessToken().getTokenValue());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET, entity, String.class);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Email>>(){}.getType();
        List<Email> emails = gson.fromJson(response.getBody(), listType);

        if (emails == null || emails.isEmpty()) {
            return null;
        }

        return emails.stream().filter(email -> email.primary).findFirst().orElse(emails.get(0)).email;
    }

    private static class Email {
        private String email;
        private boolean primary;
        private boolean verified;

    }

    protected User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findById(attributes.getId())
                .map(entity -> entity.updateProfile(attributes.getName(), attributes.getEmail(), attributes.getImageUrl(), attributes.getBio()))
                .orElse(User.builder().id(attributes.getId())
                        .name(attributes.getName())
                        .email(attributes.getEmail())
                        .imageUrl(attributes.getImageUrl())
                        .introduce(attributes.getBio())
                        .role(Role.UNCERTIFICATED)
                        .build());
        return userRepository.save(user);
    }
}
