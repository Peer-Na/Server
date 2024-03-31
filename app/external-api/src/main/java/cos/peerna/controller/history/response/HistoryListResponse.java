package cos.peerna.controller.history.response;

import cos.peerna.domain.history.dto.result.HistoryResult;
import java.util.List;
import lombok.Builder;

@Builder
public record HistoryListResponse (
        List<HistoryResult> historyList
) {}



