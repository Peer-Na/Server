package cos.peerna.event;

import cos.peerna.model.Github;
import cos.peerna.service.GithubService;
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
        Github github = Github.from(event);
        githubService.commitReply(github);
    }
}
