package cos.peerna.global.config;

import cos.peerna.PeernaDomainRoot;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackageClasses = {PeernaDomainRoot.class})
@EnableJpaRepositories(basePackageClasses = {PeernaDomainRoot.class})
@EnableJpaAuditing
public class JpaAuditingConfig {
}