package cos.peerna.controller.reply.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import cos.peerna.domain.reply.dto.result.ReplyResult;
import lombok.Builder;

@Builder
public record ReplyResponse(
        @JsonProperty("history_id")
        Long historyId,
        @JsonProperty("reply_id")
        Long replyId,
        @JsonProperty("problem_id")
        Long problemId,
        @JsonProperty("like_count")
        Long likeCount,
        String question,
        String answer,
        @JsonProperty("example_answer")
        String exampleAnswer,
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("user_name")
        String userName,
        @JsonProperty("user_image")
        String userImage,
        @JsonProperty("already_liked")
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
