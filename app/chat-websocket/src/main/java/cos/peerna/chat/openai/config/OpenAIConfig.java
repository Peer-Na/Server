package cos.peerna.chat.openai.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Value("${openai.token}")
    private String token;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(token);
    }
}
