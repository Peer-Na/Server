package cos.peerna.controller.reply.response;

import cos.peerna.domain.reply.dto.result.ReplyWithPageInfoResult;
import java.util.List;
import lombok.Builder;

@Builder
public record ReplyWithPageInfoResponse(
        List<ReplyResponse> replies,
        int totalPages,
        boolean hasNextPage
) {
    public static ReplyWithPageInfoResponse of(ReplyWithPageInfoResult result) {
        return ReplyWithPageInfoResponse.builder()
                .replies(
                        result.replies().stream()
                                .map(ReplyResponse::of)
                                .toList()
                )
                .totalPages(result.totalPages())
                .hasNextPage(result.hasNextPage())
                .build();
    }
}
