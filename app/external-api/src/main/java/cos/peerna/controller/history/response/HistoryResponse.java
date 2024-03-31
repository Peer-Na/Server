package cos.peerna.controller.history.response;

import cos.peerna.controller.user.model.Category;
import java.time.LocalDate;
import java.util.Comparator;
import lombok.Builder;
import lombok.Data;

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



