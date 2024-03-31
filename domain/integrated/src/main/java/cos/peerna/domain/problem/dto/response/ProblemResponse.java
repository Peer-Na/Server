package cos.peerna.domain.problem.dto.response;

import cos.peerna.domain.user.model.Category;
import org.springframework.validation.annotation.Validated;

@Validated
public record ProblemResponse(
		Long problemId,
		String question,
		String answer,
		Category category
) {
	@Override
	public String toString() {
		return "ProblemResponse{" +
				"problemId=" + problemId +
				", question='" + question + '\'' +
				", answer='" + answer + '\'' +
				", category='" + category + '\'' +
				'}';
	}

	public static ProblemResponse of(Long problemId, String question, String answer, Category category) {
		return new ProblemResponse(problemId, question, answer, category);
	}

}
