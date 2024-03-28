package cos.peerna.domain.gpt.event;

import cos.peerna.domain.gpt.service.GPTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GPTEventListener {

    private final GPTService gptService;

    @EventListener
    public void transactionalEventListenerAfterCommit(ReviewReplyEvent event) {
        log.info("Review Reply Event Published: {}", event);
        gptService.reviewReply(event);
    }

    @EventListener
    public void websocketDisconnectEventHandler(GPTWebSocketDisconnectEvent event) {
        log.info("GPTWebSocketDisconnectEvent Event Published: {}", event);
        gptService.deleteConversation(event.userId());
    }
}
