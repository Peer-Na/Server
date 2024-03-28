package cos.peerna.domain.room.controller;

import cos.peerna.domain.room.dto.response.ChangeProblemResponse;
import cos.peerna.domain.room.service.RoomService;
import cos.peerna.global.security.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @MessageMapping("/room/{roomId}/enter")
    @SendTo("/room/{roomId}")
    public String enter(SimpMessageHeaderAccessor messageHeaderAccessor,
                        @DestinationVariable("roomId") Long roomId) {
        SessionUser user = (SessionUser) messageHeaderAccessor.getSessionAttributes().get("user");

        return String.format("System:\n %s님이 입장하셨습니다.", user.getName());
    }

    @MessageMapping("/room/{roomId}")
    @SendTo("/room/{roomId}")
    public String chat(SimpMessageHeaderAccessor messageHeaderAccessor,
                       @DestinationVariable("roomId") Integer roomId,
                       @Payload String message) {
        SessionUser user = (SessionUser) messageHeaderAccessor.getSessionAttributes().get("user");

        roomService.saveChat(roomId, user.getId(), message);

        return String.format("%s:\n %s", user.getName(), message);
    }

    @MessageMapping("/room/{roomId}/problem")
    @SendTo("/room/{roomId}/problem")
    public ChangeProblemResponse changeProblem(SimpMessageHeaderAccessor messageHeaderAccessor,
                                               @DestinationVariable("roomId") Integer roomId,
                                               @Payload Long problemId) {
        SessionUser user = (SessionUser) messageHeaderAccessor.getSessionAttributes().get("user");

        return roomService.changeProblem(roomId, user.getName(), problemId);
    }
}
