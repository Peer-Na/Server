package cos.peerna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
		PeernaDomainRoot.class,
		ChatWebSocketApplication.class
})
public class ChatWebSocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatWebSocketApplication.class, args);
	}
}
