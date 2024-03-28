package cos.peerna.domain.room.event;


public record RoomWebSocketDisconnectEvent(
        Long userId,
        String name
) {

    public static RoomWebSocketDisconnectEvent of(Long userId, String name) {
        return new RoomWebSocketDisconnectEvent(userId, name);
    }
}
