package cos.peerna.domain.history.dto.result;

import cos.peerna.domain.user.model.Category;
import lombok.Builder;
import java.time.LocalDate;
import java.util.Comparator;

@Builder
public record HistoryResult(
        Long historyId,
        Long problemId,
        String question,
        Category category,
        LocalDate time
) implements Comparator<HistoryResult> {
    @Override
    public int compare(HistoryResult dto1, HistoryResult dto2) {
        return Long.compare(dto1.historyId, dto2.historyId);
    }
}




