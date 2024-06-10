package cos.peerna.support.event.chat;


public record GPTWebSocketDisconnectEvent(
        Long userId
) {

    public static GPTWebSocketDisconnectEvent of(Long userId) {
        return new GPTWebSocketDisconnectEvent(userId);
    }
}
