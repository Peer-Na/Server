package cos.peerna.chat.room.config;

import cos.peerna.support.event.room.CreateRoomEvent;
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
public class RoomKafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, CreateRoomEvent> createRoomEventConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                new JsonDeserializer<>(CreateRoomEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CreateRoomEvent>
    createRoomEventKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, CreateRoomEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createRoomEventConsumerFactory());
        return factory;
    }
}
