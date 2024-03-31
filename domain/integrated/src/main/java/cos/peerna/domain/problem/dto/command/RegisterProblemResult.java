package cos.peerna.domain.problem.dto.command;


import cos.peerna.domain.user.model.Category;
import lombok.Builder;

@Builder
public record RegisterProblemResult(
        String question,
        String answer,
        Category category
) {}
