package cos.peerna.domain.problem.dto.result;

import cos.peerna.domain.user.model.Category;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

@Builder
@Validated
public record ProblemResult(
		Long problemId,
		String question,
		String answer,
		Category category
) {}
