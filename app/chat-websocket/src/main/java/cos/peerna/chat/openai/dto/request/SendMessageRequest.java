package cos.peerna.chat.openai.dto.request;

public record SendMessageRequest(
    String message,
    String lastGPTMessage
) {}
