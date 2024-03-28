package cos.peerna.domain.gpt.event;


public record GPTWebSocketDisconnectEvent(
        Long userId
) {

    public static GPTWebSocketDisconnectEvent of(Long userId) {
        return new GPTWebSocketDisconnectEvent(userId);
    }
}
