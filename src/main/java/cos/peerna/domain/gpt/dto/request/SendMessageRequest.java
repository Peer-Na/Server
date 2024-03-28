package cos.peerna.domain.gpt.dto.request;

public record SendMessageRequest(
    String message,
    String lastGPTMessage
) {
    @Override
    public String toString() {
        return "SendMessageRequest{" +
                "message='" + message + '\'' +
                ", lastGPTMessage='" + lastGPTMessage + '\'' +
                '}';
    }
}
