package cos.peerna.controller.history.response;

import cos.peerna.domain.reply.dto.result.ReplyResult;
import cos.peerna.domain.room.dto.ChatMessageSendDto;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetailHistoryResponse {
    private String question;
    private String exampleAnswer;
    private LocalDate time;
    private List<ReplyResult> replies;
    private List<String> keywords;
    private List<ChatMessageSendDto> chattings;
}