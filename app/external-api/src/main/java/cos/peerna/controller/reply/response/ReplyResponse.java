package cos.peerna.controller.reply.response;

import cos.peerna.domain.reply.dto.result.ReplyResult;
import lombok.Builder;

@Builder
public record ReplyResponse(
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
    public static ReplyResponse of(ReplyResult replyResult) {
        return ReplyResponse.builder()
                .historyId(replyResult.historyId())
                .replyId(replyResult.replyId())
                .problemId(replyResult.problemId())
                .likeCount(replyResult.likeCount())
                .question(replyResult.question())
                .answer(replyResult.answer())
                .exampleAnswer(replyResult.exampleAnswer())
                .userId(replyResult.userId())
                .userName(replyResult.userName())
                .userImage(replyResult.userImage())
                .alreadyLiked(replyResult.alreadyLiked())
                .build();
    }
}
