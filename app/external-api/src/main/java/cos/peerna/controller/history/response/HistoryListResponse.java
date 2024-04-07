package cos.peerna.controller.history.response;

import java.util.List;
import lombok.Builder;

@Builder
public record HistoryListResponse (
        List<HistoryResponse> histories
) {}



