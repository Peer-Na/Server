package cos.peerna.controller.problem.response;

import cos.peerna.domain.problem.dto.result.AnswerAndKeywordResult;
import cos.peerna.domain.problem.dto.result.KeywordResult;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

@Builder
@Validated
public record AnswerAndKeywordResponse(
		String answer,
		KeywordResult keywords
) {

	public static AnswerAndKeywordResponse of(AnswerAndKeywordResult result) {
		return AnswerAndKeywordResponse.builder()
				.answer(result.answer())
				.keywords(result.keywords())
				.build();
	}
}
