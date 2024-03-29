package cos.peerna.domain.match.controller;

import cos.peerna.domain.match.model.Standby;
import cos.peerna.domain.match.service.MatchService;
import cos.peerna.domain.user.model.Category;
import cos.peerna.global.security.dto.SessionUser;
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

        Standby standby =  matchService.addStandby(user.getId(), Category.valueOf(category));
        if (standby == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return ResponseEntity.created(URI.create(standby.getId().toString())).build();
    }

    @MessageMapping("/match/cancel")
    @SendToUser("/match/join")
    public ResponseEntity<Void> cancelJoinQueue(SimpMessageHeaderAccessor messageHeaderAccessor) {
        SessionUser user = (SessionUser) messageHeaderAccessor.getSessionAttributes().get("user");

        matchService.cancelStandby(user.getId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}