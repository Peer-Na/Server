package cos.peerna.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 10800) // 3시간 (해당 어노테이션 사용시 timeout application.yml 로 설정 불가능)
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("SESSION");// JSESSIONID 가 기본
        serializer.setCookiePath("/");
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$"); // 호출한 도메인으로 설정됨
        serializer.setCookieMaxAge(86400); // -1 = 브라우저 닫힐때까지
        serializer.setUseSecureCookie(true); // https 만 허용
        serializer.setUseHttpOnlyCookie(true); // js 에서 쿠키 접근 가능
        serializer.setSameSite("None");
        return serializer;
    }

}
