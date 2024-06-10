package cos.peerna.chat.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cos.peerna.domain.room.model.Chat;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Data;

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