package cos.peerna.event;

import lombok.Builder;

@Builder
public record CommitReplyEvent(
        String githubLogin,
        String githubToken,
        String githubRepo,
        String question,
        String category,
        String answer
) {}
