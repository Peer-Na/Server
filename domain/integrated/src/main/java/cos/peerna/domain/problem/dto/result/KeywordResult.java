package cos.peerna.domain.problem.dto.result;

import java.util.List;
import lombok.Builder;

@Builder
public record KeywordResult(
        List<String> keywords
) {
}
