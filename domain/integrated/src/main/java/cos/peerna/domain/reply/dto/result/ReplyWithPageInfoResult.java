package cos.peerna.domain.reply.dto.result;

import java.util.List;

public record ReplyWithPageInfoResult(
        List<ReplyResult> replies,
        int totalPages,
        boolean hasNextPage
) {
    public static ReplyWithPageInfoResult of(List<ReplyResult> replies, int totalPages, boolean hasNextPage) {
        return new ReplyWithPageInfoResult(replies, totalPages, hasNextPage);
    }
}
