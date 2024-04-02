package cos.peerna.domain.problem.dto.result;

import cos.peerna.domain.keyword.model.Keyword;
import java.util.List;
import lombok.Builder;

@Builder
public record KeywordResult(
        List<String> keywords
) {
    public static KeywordResult of(List<Keyword> keywords) {
        return KeywordResult.builder()
                .keywords(keywords.stream().map(Keyword::getName).toList())
                .build();
    }
}
