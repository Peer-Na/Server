package cos.peerna.domain.room.model;

import cos.peerna.domain.user.model.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("Peerna:Room")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {
    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Category category;
    private List<Long> historyIdList;

    @Builder
    public Room(Long historyId, Category category) {
        this.historyIdList = new ArrayList<>();
        this.historyIdList.add(historyId);
        this.category = category;
    }

    public Long getLastHistoryId() {
        return historyIdList.get(historyIdList.size() - 1);
    }
}
