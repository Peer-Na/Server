package cos.peerna.support.event.room;


public record RoomWebSocketDisconnectEvent(
        Long userId,
        String name
) {

    public static RoomWebSocketDisconnectEvent of(Long userId, String name) {
        return new RoomWebSocketDisconnectEvent(userId, name);
    }
}
