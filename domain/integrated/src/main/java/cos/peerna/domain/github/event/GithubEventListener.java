package cos.peerna.domain.github.event;

import cos.peerna.domain.github.domain.Github;
import cos.peerna.domain.github.service.GithubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubEventListener {

    private final GithubService githubService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void transactionalEventListenerAfterCommit(CommitReplyEvent event) {
        log.debug("TransactionPhase.AFTER_COMMIT ---> {}", event);
        Github github = Github.of(event.githubToken(), event.githubLogin(), event.githubRepo());
        githubService.commitReply(github, event.problem(), event.answer());
    }
}
