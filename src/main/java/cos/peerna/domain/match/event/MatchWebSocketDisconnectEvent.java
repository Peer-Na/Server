package cos.peerna.domain.match.event;


public record MatchWebSocketDisconnectEvent(
        Long userId
) {

    public static MatchWebSocketDisconnectEvent of(Long userId) {
        return new MatchWebSocketDisconnectEvent(userId);
    }
}
