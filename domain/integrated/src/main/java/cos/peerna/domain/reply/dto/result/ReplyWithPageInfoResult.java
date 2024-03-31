package cos.peerna.domain.reply.dto.result;

import java.util.List;
import lombok.Builder;

@Builder
public record ReplyWithPageInfoResult(
        List<ReplyResult> replies,
        int totalPages,
        boolean hasNextPage
) {}
