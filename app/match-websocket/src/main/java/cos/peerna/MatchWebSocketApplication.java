package cos.peerna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
    PeernaDomainRoot.class,
    MatchWebSocketApplication.class
})
public class MatchWebSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchWebSocketApplication.class, args);
    }
}
