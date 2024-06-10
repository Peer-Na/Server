package cos.peerna.global.websocket;

import cos.peerna.domain.room.repository.ConnectRepository;
import cos.peerna.global.security.dto.SessionUser;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionInterceptor implements ChannelInterceptor {

    private final ApplicationEventPublisher eventPublisher;
    private final ConnectRepository connectRepository;

    @Override
    public void postSend(Message message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        SessionUser user = (SessionUser) accessor.getSessionAttributes().get("user");

        switch (Objects.requireNonNull(accessor.getCommand())) {
            case CONNECT:
                /*
                TODO: 이전 상태 (유저와 학습, 매칭 등)를 확인하고 이어서 진행
                 */
                break;
            case DISCONNECT:
                /*
                TODO: 모듈 분리 후 주석 해제
                eventPublisher.publishEvent(MatchWebSocketDisconnectEvent.of(user.getId()));
                eventPublisher.publishEvent(RoomWebSocketDisconnectEvent.of(user.getId(), user.getName()));
                eventPublisher.publishEvent(GPTWebSocketDisconnectEvent.of(user.getId()));
                 */
                break;
            default:
                break;
        }

    }
}
