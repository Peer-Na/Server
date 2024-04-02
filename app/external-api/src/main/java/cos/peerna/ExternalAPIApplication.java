package cos.peerna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cos.peerna.PeernaDomainRoot;

@SpringBootApplication(scanBasePackageClasses = {
		PeernaDomainRoot.class,
		ExternalAPIApplication.class
})
public class ExternalAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExternalAPIApplication.class, args);
	}
}
