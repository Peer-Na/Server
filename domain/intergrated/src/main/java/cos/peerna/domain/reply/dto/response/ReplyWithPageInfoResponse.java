package cos.peerna.domain.reply.dto.response;

import cos.peerna.domain.reply.model.Reply;
import java.util.List;

public record ReplyWithPageInfoResponse(
        List<ReplyResponse> replies,
        int totalPages,
        boolean hasNextPage
) {
    public static ReplyWithPageInfoResponse from(List<ReplyResponse> replies, int totalPages, boolean hasNextPage) {
        return new ReplyWithPageInfoResponse(replies, totalPages, hasNextPage);
    }
}
