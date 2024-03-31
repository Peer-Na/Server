package cos.peerna.domain.problem.dto.response;

import cos.peerna.domain.keyword.model.Keyword;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@Validated
public record AnswerAndKeywordResponse(
		String answer,
		List<Keyword> keywords
) {
	@Override
	public String toString() {
		return "AnswerAndKeywordResponse{" +
				"answer='" + answer + '\'' +
				", keywords=" + keywords +
				'}';
	}

	public static AnswerAndKeywordResponse of(String answer, List<Keyword> keywords) {
		return new AnswerAndKeywordResponse(answer, keywords);
	}
}
