package cos.peerna.controller.history.response;

import cos.peerna.domain.user.model.Category;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record HistoryResponse (
        Long historyId,
        Long problemId,
        String question,
        Category category,
        LocalDate time
) {}



