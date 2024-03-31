package cos.peerna.controller.problem.response;

import cos.peerna.domain.problem.dto.result.ProblemResult;
import java.util.List;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

@Builder
@Validated
public record ProblemListResponse(
		List<ProblemResult> problems
) {
}
