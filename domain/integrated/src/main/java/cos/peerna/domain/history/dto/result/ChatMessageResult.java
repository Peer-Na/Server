package cos.peerna.domain.history.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import cos.peerna.domain.room.model.Chat;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageResult {

    @JsonProperty("writer_id")
    private Long writerId;
    private String message;
    private LocalTime time;

    public static ChatMessageResult of(Chat chat) {
        return ChatMessageResult.builder()
                .writerId(chat.getWriterId())
                .message(chat.getContent())
                .time(chat.getTime())
                .build();
    }
}