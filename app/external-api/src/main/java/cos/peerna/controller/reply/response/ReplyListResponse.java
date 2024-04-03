package cos.peerna.controller.reply.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import cos.peerna.domain.reply.dto.result.ReplyResult;
import java.util.List;
import lombok.Builder;

@Builder
public record ReplyListResponse(

        List<ReplyResponse> replies
) {
    public static ReplyListResponse of(List<ReplyResult> replyResults) {
        return ReplyListResponse.builder()
                .replies(replyResults.stream()
                        .map(ReplyResponse::of)
                        .toList())
                .build();
    }
}
