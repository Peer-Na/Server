package cos.peerna.controller.history;

import cos.peerna.controller.history.response.DetailHistoryResponse;
import cos.peerna.controller.history.response.HistoryResponse;
import cos.peerna.domain.history.service.HistoryService;
import cos.peerna.global.security.LoginUser;
import cos.peerna.global.security.dto.SessionUser;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private final HistoryService historyService;
    @GetMapping
    public ResponseEntity<List<HistoryResponse>> findUserHistory(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "0") Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(historyService.findUserHistory(userId, cursorId, size));
    }

    @GetMapping("/detail")
    public ResponseEntity<DetailHistoryResponse> findDetailHistory(
            @Nullable @LoginUser SessionUser user,
            @RequestParam Long historyId) {
        return ResponseEntity.ok(historyService.findDetailHistory(historyId, user == null ? null : user.getId()));
    }
}
