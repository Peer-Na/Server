package cos.peerna.domain.room.dto.response;

public record ChangeProblemResponse(
        Long problemId,
        String question,
        String message
) {
    public static ChangeProblemResponse of(Long problemId, String question, String message) {
        return new ChangeProblemResponse(problemId, question, message);
    }

    @Override
    public String toString() {
        return "ChangeProblemResponse{" +
                "problemId=" + problemId +
                ", question='" + question + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
