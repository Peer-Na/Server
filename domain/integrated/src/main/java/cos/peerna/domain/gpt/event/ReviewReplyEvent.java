package cos.peerna.domain.gpt.event;

public record ReviewReplyEvent(
        Long historyId,
        Long userId,
        String question,
        String answer
) {
    @Override
    public String toString() {
        return "ReviewReplyEvent{" +
                "historyId=" + historyId +
                "userId=" + userId +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }

    public static ReviewReplyEvent of(Long historyId, Long userId, String question, String answer) {
        return new ReviewReplyEvent(historyId, userId, question, answer);
    }
}
