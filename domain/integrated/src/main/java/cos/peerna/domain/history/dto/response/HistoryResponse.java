package cos.peerna.domain.history.dto.response;

import cos.peerna.domain.user.model.Category;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Comparator;

@Data
@Builder
public class HistoryResponse implements Comparator<HistoryResponse> {
    private Long     historyId;
    private Long     problemId;
    private String   question;
    private Category category;
    private LocalDate time;

    @Override
    public int compare(HistoryResponse dto1, HistoryResponse dto2) {
        if (dto1.historyId > dto2.historyId) {
            return 1;
        } else if (dto1.historyId < dto2.historyId) {
            return -1;
        }
        return 0;
    }
}



