package cos.peerna.controller.history;

import cos.peerna.controller.history.response.DetailHistoryResponse;
import cos.peerna.controller.history.response.HistoryListResponse;
import cos.peerna.domain.history.dto.result.DetailHistoryResult;
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
    public ResponseEntity<HistoryListResponse> findUserHistory(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "0") Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(HistoryListResponse.builder()
                .historyList(historyService.findUserHistory(userId, cursorId, size))
                .build());
    }

    @GetMapping("/detail")
    public ResponseEntity<DetailHistoryResponse> findDetailHistory(
            @Nullable @LoginUser SessionUser user,
            @RequestParam Long historyId) {

        DetailHistoryResult result = historyService.findDetailHistory(historyId, user == null ? null : user.getId());
        return ResponseEntity.ok(DetailHistoryResponse.builder()
                .question(result.getQuestion())
                .exampleAnswer(result.getExampleAnswer())
                .time(result.getTime())
                .replies(result.getReplies())
                .keywords(result.getKeywords())
                .chattings(result.getChattings())
                .build());
    }
}
