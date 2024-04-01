package cos.peerna.domain.room.event;

import cos.peerna.domain.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomEventListener {

    private final RoomService roomService;

    @KafkaListener(
            topics = "peerna:matched",
            containerFactory = "greetingKafkaListenerContainerFactory")
    public void greetingListener(CreateRoomEvent event) {
        log.debug("RoomCreateEvent!! ---> {}", event);
        roomService.createRoom(event);
    }

    @EventListener
    public void websocketDisconnectEventHandler(RoomWebSocketDisconnectEvent event) {
        log.debug("RoomWebSocketDisconnectEvent ---> {}", event);
        roomService.disconnect(event.userId(), event.name());
    }
}
