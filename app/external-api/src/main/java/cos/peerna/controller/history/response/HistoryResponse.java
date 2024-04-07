package cos.peerna.controller.history.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import cos.peerna.domain.history.dto.result.HistoryResult;
import cos.peerna.domain.user.model.Category;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record HistoryResponse (
        @JsonProperty("history_id")
        Long historyId,
        @JsonProperty("problem_id")
        Long problemId,
        String question,
        Category category,
        LocalDate time
) {
        public static HistoryResponse of(HistoryResult result) {
                return HistoryResponse.builder()
                        .historyId(result.historyId())
                        .problemId(result.problemId())
                        .question(result.question())
                        .category(result.category())
                        .time(result.time())
                        .build();
        }
}



