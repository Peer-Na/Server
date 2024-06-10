package cos.peerna.chat.openai.controller;

import cos.peerna.chat.openai.dto.request.SendMessageRequest;
import cos.peerna.chat.openai.service.GPTService;
import cos.peerna.global.security.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GPTController {

    private final GPTService gptService;

    @MessageMapping("/gpt/message")
    @SendToUser("/gpt")
    public void sendMessageToGPT(SimpMessageHeaderAccessor messageHeaderAccessor, SendMessageRequest request) {
        SessionUser user = (SessionUser) messageHeaderAccessor.getSessionAttributes().get("user");

        gptService.sendMessage(user, request);
    }
}
