package cos.peerna.domain.reply.model;

import cos.peerna.domain.user.model.User;
import cos.peerna.domain.history.model.History;
import cos.peerna.domain.problem.model.Problem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reply", indexes = {
        @Index(name = "idx_reply_history", columnList = "history_id"),
        @Index(name = "idx_reply_problem", columnList = "problem_id"),
        @Index(name = "idx_reply_user", columnList = "user_id")
})
public class Reply {

    @Id
    @GeneratedValue
    @Column(name = "reply_id")
    private Long id;

    @NotNull
    @Column(length = 500)
    private String answer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id")
    private History history;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "reply", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likey> likes;

    private Long likeCount;

    @Builder
    private Reply(String answer, History history, Problem problem, User user) {
        this.answer = answer;
        this.history = history;
        this.problem = problem;
        this.user = user;
        this.likeCount = 0L;
        this.likes = new ArrayList<>();
    }

    @Builder(builderMethodName = "builderForRegister")
    public static Reply of(String answer, History history, Problem problem, User user) {
        return builder()
                .answer(answer)
                .history(history)
                .problem(problem)
                .user(user)
                .build();
    }

    public void modifyAnswer(String answer) {
        this.answer = answer;
    }

    public void addLikey(Likey likey) {
        this.likes.add(likey);
        ++this.likeCount;
        this.user.addScore(10);
    }

    public void dislikeReply(Long userId) {
        Likey likey = this.likes.stream()
                .filter(like -> like.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Likey Not Found"));
        this.likes.remove(likey);
        --this.likeCount;
        this.user.addScore(-10);
    }

    public boolean isLikedBy(Long userId) {
        if (userId == null) {
            return false;
        }
        return this.likes.stream()
                .anyMatch(likey -> likey.getUser().getId().equals(userId));
    }
}
