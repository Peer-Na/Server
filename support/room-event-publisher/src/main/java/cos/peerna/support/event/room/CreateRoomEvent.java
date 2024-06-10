package cos.peerna.support.event.room;


import java.util.List;

public record CreateRoomEvent(
        List<Long> userIds,
        String category
) {

    public static CreateRoomEvent of(List<Long> userIds, String category) {
        return new CreateRoomEvent(userIds, category);
    }
}
