package cos.peerna.controller.history;

import cos.peerna.controller.dto.ResponseDto;
import cos.peerna.controller.history.response.DetailHistoryResponse;
import cos.peerna.controller.history.response.HistoryListResponse;
import cos.peerna.controller.history.response.HistoryResponse;
import cos.peerna.controller.reply.response.ReplyResponse;
import cos.peerna.domain.history.dto.result.DetailHistoryResult;
import cos.peerna.domain.history.dto.result.HistoryResult;
import cos.peerna.domain.history.service.HistoryService;
import cos.peerna.domain.user.model.Category;
import cos.peerna.global.security.LoginUser;
import cos.peerna.global.security.dto.SessionUser;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/histories")
public class HistoryController {
    private final HistoryService historyService;
    @GetMapping
    public ResponseEntity<ResponseDto<HistoryListResponse>> findUserHistory(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "0") Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size) {

        List<HistoryResult> historyList = historyService.findUserHistory(userId, cursorId, size);
        return ResponseEntity.ok(ResponseDto.of(
                        HistoryListResponse.builder()
                                .histories(historyList.stream()
                                        .map(HistoryResponse::of).toList()).build()));
    }

    @GetMapping("/detail")
    public ResponseEntity<ResponseDto<DetailHistoryResponse>> findDetailHistory(
            @Nullable @LoginUser SessionUser user,
            @RequestParam Long historyId) {

        DetailHistoryResult result = historyService.findDetailHistory(historyId, user == null ? null : user.getId());
        return ResponseEntity.ok(ResponseDto.of(DetailHistoryResponse.builder()
                .date(result.getTime())
                .replies(result.getReplies().stream()
                        .map(ReplyResponse::of).toList())
                .keywords(result.getKeywords())
                .chattings(result.getChattings())
                .build()));
    }
}
