package cos.peerna.domain.match.model;

import cos.peerna.domain.user.model.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash("peerna:match:ticket")
public final class MatchTicket {
    @Id
    private final Long id;

    @Indexed
    @Enumerated(EnumType.STRING)
    private Category interest1;
    
    @Indexed
    @Enumerated(EnumType.STRING)
    private Category interest2;

    private final Integer score;

    @Transient
    private Long waitingTime;

    private final LocalDateTime createdAt;


    @Builder
    public MatchTicket(Long id, Category interest1, Category interest2, Integer score, LocalDateTime createdAt) {
        this.id = id;
        this.interest1 = interest1;
        this.interest2 = interest2;
        this.score = score;
        this.createdAt = createdAt;
    }

    public boolean isMatchable(MatchTicket matchTicket) {
        if (waitingTime == null) {
            waitingTime = ChronoUnit.SECONDS.between(this.createdAt, LocalDateTime.now());
        }
        int scoreGap = Math.abs(this.score - matchTicket.score);
        return scoreGap <= 50 + Math.log(1 + waitingTime) / Math.log(2);
    }

    public boolean isLongTermWaiting() {
        if (waitingTime == null) {
            waitingTime = ChronoUnit.SECONDS.between(this.createdAt, LocalDateTime.now());
        }
        return waitingTime > 300;
    }

    public void rotateInterests() {
        Category temp = interest1;
        interest1 = interest2;
        interest2 = temp;
    }

    @Override
    public String toString() {
        return "MatchTicket[" +
                "id=" + id + ", " +
                "score=" + score + ", " +
                "createdAt=" + createdAt + ']';
    }
}
