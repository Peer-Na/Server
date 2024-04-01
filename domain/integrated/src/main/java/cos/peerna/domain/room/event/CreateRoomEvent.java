package cos.peerna.domain.room.event;


import cos.peerna.domain.user.model.Category;
import java.util.List;

public record CreateRoomEvent(
        List<Long> userIds,
        Category category
) {

    public static CreateRoomEvent of(List<Long> userIds, Category category) {
        return new CreateRoomEvent(userIds, category);
    }
}
