package cos.peerna.domain.match.model;

import cos.peerna.domain.user.model.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash("peerna:standby")
public final class Standby {
    @Id
    private final Long id;

    @Indexed
    @Enumerated(EnumType.STRING)
    private final Category category;

    private final Integer score;

    private final LocalDateTime createdAt;


    @Builder
    public Standby(Long id, Category category, Integer score, LocalDateTime createdAt) {
        this.id = id;
        this.category = category;
        this.score = score;
        this.createdAt = createdAt;
    }

    public boolean isMatchable(Standby standby) {
        long waitingTime = ChronoUnit.SECONDS.between(this.createdAt, LocalDateTime.now());
        int scoreGap = Math.abs(this.score - standby.score);
        return scoreGap <= 50 + Math.log(1 + waitingTime) / Math.log(2);
    }

    @Override
    public String toString() {
        return "Standby[" +
                "id=" + id + ", " +
                "score=" + score + ", " +
                "createdAt=" + createdAt + ']';
    }
}
