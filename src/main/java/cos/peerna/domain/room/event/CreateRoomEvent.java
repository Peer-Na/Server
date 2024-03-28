package cos.peerna.domain.room.event;


import cos.peerna.domain.user.model.Category;
import java.util.Map;

public record CreateRoomEvent(
        Map<Long, Integer> userScore,
        Category category
) {

    public static CreateRoomEvent of(Map<Long, Integer> userScore, Category category) {
        return new CreateRoomEvent(userScore, category);
    }
}
