package cos.peerna.domain.history.controller;

import cos.peerna.domain.history.dto.response.DetailHistoryResponse;
import cos.peerna.domain.history.service.HistoryService;
import cos.peerna.domain.history.dto.response.HistoryResponse;
import cos.peerna.global.security.LoginUser;
import cos.peerna.global.security.dto.SessionUser;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
