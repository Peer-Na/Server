package cos.peerna;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableAsync
@SpringBootApplication
@EnableJpaRepositories(basePackages = {"cos.peerna"})
@EnableJpaAuditing
public class PeernaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeernaApplication.class, args);
	}

	@Bean
	Hibernate5JakartaModule hibernate5Module() {
		return new Hibernate5JakartaModule();
	}

	@Bean
	public BCryptPasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
