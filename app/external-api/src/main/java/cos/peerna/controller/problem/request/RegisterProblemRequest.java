package cos.peerna.controller.problem.request;


import cos.peerna.domain.user.model.Category;
import lombok.Builder;

@Builder
public record RegisterProblemRequest(
        String question,
        String answer,
        Category category
) {}
