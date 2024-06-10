package cos.peerna.chat.room.event;

import cos.peerna.chat.room.facade.RoomFacade;
import cos.peerna.domain.room.repository.ConnectRepository;
import cos.peerna.support.event.chat.RegisterReplyEvent;
import cos.peerna.support.event.room.CreateRoomEvent;
import cos.peerna.support.event.room.RoomWebSocketDisconnectEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomEventListener {

    private final ConnectRepository connectRepository;
    private final RoomFacade roomService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @KafkaListener(
            topics = "peerna-matched",
            containerFactory = "createRoomEventKafkaListenerContainerFactory")
    public void createRoomEventListener(CreateRoomEvent event) {
        log.debug("RoomCreateEvent!! ---> {}", event);
        roomService.createRoom(event);
    }

    @KafkaListener(
            topics = "peerna-room-register_reply",
            containerFactory = "registerReplyEventKafkaListenerContainerFactory")
    public void registerReplyEventListener(RegisterReplyEvent event) {
        log.debug("RegisterReplyEvent!! ---> {}", event);
        connectRepository.findById(event.userId()).ifPresent(connect -> {
            simpMessagingTemplate.convertAndSend("/room/" + connect.getRoomId() + "/answer", event);
        });
    }

    @EventListener
    public void websocketDisconnectEventHandler(RoomWebSocketDisconnectEvent event) {
        log.debug("RoomWebSocketDisconnectEvent ---> {}", event);
        roomService.disconnect(event.userId(), event.name());
    }
}
