package cos.peerna.chat.openai.config;

import cos.peerna.support.event.chat.RegisterReplyEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class ReplyKafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, RegisterReplyEvent> registerReplyEventConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                new JsonDeserializer<>(RegisterReplyEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RegisterReplyEvent>
    registerReplyEventKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, RegisterReplyEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(registerReplyEventConsumerFactory());
        return factory;
    }
}
