package cos.peerna.domain.problem.dto.result;

import cos.peerna.domain.keyword.model.Keyword;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@Validated
public record AnswerAndKeywordResult(
		String answer,
		KeywordResult keywords
) {
	public static AnswerAndKeywordResult of(String answer, List<Keyword> keywords) {
		return new AnswerAndKeywordResult(answer, KeywordResult.of(keywords));
	}
}
