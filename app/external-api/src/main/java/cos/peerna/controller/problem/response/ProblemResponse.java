package cos.peerna.controller.problem.response;

import cos.peerna.domain.user.model.Category;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

@Builder
@Validated
public record ProblemResponse(
		Long problemId,
		String question,
		String answer,
		Category category
) {}
