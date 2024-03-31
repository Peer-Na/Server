package cos.peerna.controller.reply.response;

import cos.peerna.domain.reply.dto.result.ReplyResult;
import java.util.List;
import lombok.Builder;

@Builder
public record ReplyListResponse(
        List<ReplyResponse> replyList
) {
    public static ReplyListResponse of(List<ReplyResult> replyResults) {
        return ReplyListResponse.builder()
                .replyList(replyResults.stream()
                        .map(ReplyResponse::of)
                        .toList())
                .build();
    }
}
