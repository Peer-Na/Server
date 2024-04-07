package cos.peerna.domain.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cos.peerna.domain.room.model.Chat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class ChatMessageSendDto {

    @JsonProperty("writer_id")
    private Long writerId;
    private String message;
    private LocalTime time;

    public static ChatMessageSendDto of(Chat chat) {
        return ChatMessageSendDto.builder()
                .writerId(chat.getWriterId())
                .message(chat.getContent())
                .time(chat.getTime())
                .build();
    }
}