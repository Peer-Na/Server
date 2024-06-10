package cos.peerna.match.controller;

import cos.peerna.domain.match.model.MatchTicket;
import cos.peerna.domain.user.model.Category;
import cos.peerna.global.security.dto.SessionUser;
import cos.peerna.match.service.MatchService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @MessageMapping("/match/join")
    @SendToUser("/match/join")
    public ResponseEntity<Void> joinQueue(SimpMessageHeaderAccessor messageHeaderAccessor, String category) {
        SessionUser user = (SessionUser) messageHeaderAccessor.getSessionAttributes().get("user");

        MatchTicket matchTicket =  matchService.addTicket(user.getId(), Category.valueOf(category));
        if (matchTicket == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return ResponseEntity.created(URI.create(matchTicket.getId().toString())).build();
    }

    @MessageMapping("/match/cancel")
    @SendToUser("/match/join")
    public ResponseEntity<Void> cancelJoinQueue(SimpMessageHeaderAccessor messageHeaderAccessor) {
        SessionUser user = (SessionUser) messageHeaderAccessor.getSessionAttributes().get("user");

        matchService.cancelTicket(user.getId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}