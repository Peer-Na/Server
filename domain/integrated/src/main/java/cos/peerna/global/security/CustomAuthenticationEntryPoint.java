package cos.peerna.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * The AuthenticationEntryPoint is an interface
     * provided by Spring Security that is used to determine
     * what should happen when an unauthenticated user attempts to access a protected resource.
     * 인증되지 않은 유저가 보호된 리소스에 접근할 때 Exception 을 handling 하기 위한 부분
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.debug("CustomAuthenticationEntryPoint");
        log.debug("EntryPoint Exception: {}", request.getAttribute(ERROR_EXCEPTION));
        log.debug("EntryPoint authException: {}", authException.getMessage());

        IllegalArgumentException e = new IllegalArgumentException("Authentication failed");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(responseStream, e);
        responseStream.flush();
    }
}
