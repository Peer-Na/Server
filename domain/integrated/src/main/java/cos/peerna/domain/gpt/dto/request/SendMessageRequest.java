package cos.peerna.domain.gpt.dto.request;

public record SendMessageRequest(
    String message,
    String lastGPTMessage
) {}
