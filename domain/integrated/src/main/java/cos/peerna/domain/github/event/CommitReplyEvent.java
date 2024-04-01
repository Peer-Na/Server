package cos.peerna.domain.github.event;

import cos.peerna.domain.problem.model.Problem;
import lombok.Builder;

@Builder
public record CommitReplyEvent(
        String githubLogin,
        String githubToken,
        String githubRepo,
        Problem problem,
        String answer
) {}
