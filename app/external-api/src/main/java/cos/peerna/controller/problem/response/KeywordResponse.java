package cos.peerna.controller.problem.response;

import cos.peerna.domain.problem.dto.result.KeywordResult;
import java.util.List;
import lombok.Builder;

@Builder
public record KeywordResponse(
        List<String> keywords
) {
    public static KeywordResponse of(KeywordResult keywordResult) {
        return KeywordResponse.builder()
                .keywords(keywordResult.keywords())
                .build();
    }
}
