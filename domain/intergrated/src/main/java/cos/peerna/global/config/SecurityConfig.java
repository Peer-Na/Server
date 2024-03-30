package cos.peerna.global.config;

import cos.peerna.global.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, "/api/reply", "/api/reply/problem").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers("/api/**").hasAnyAuthority("CRUD_CONTENT")
                .anyRequest().permitAll();
        http
                .csrf()
                .disable();
        http
                .oauth2Login()
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

        http
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/api/login")
                .usernameParameter("email")
                .passwordParameter("password");
        http
                .logout()
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("SESSION");

        http
                .authenticationProvider(customAuthenticationProvider)
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint);



//        http
//                .sessionManagement()
//                .maximumSessions(1)
//                .maxSessionsPreventsLogin(true);

        return http.build();
    }
}

/*
 * .maxSessionsPreventsLogin();
 * true -> 동일한 계정으로 먼저 접근한 회원이 있다면 두 번째로 접근한 회원은 접근 불가능
 * false -> 먼저 로그인한 계정을 로그아웃 시키고 접근

 * .userInfoEndpoint()
 * OAuth 2 로그인 성공 이후 사용자 정보를 가져올 떄의 설정들을 담당한다.

 * .userService(customOAuth2UserService);
 * 소셜 로그인 성공시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록한다.
 * 리소스 서버(소셜 서비스들)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자하는 기능을 명시할 수 있다.
 **/