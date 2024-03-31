package cos.peerna.domain.reply.dto.result;

import lombok.Builder;

@Builder
public record ReplyResult(
        Long historyId,
        Long replyId,
        Long problemId,
        Long likeCount,
        String question,
        String answer,
        String exampleAnswer,
        Long userId,
        String userName,
        String userImage,
        boolean alreadyLiked
) {
}
