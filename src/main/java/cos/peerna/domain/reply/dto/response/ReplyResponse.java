package cos.peerna.domain.reply.dto.response;

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
    public static ReplyResponse of(Long historyId, Long replyId, Long problemId, Long likeCount, String question, String answer, String exampleAnswer,
                                   Long userId, String userName, String userImage, boolean alreadyLiked) {
        return new ReplyResponse(historyId, replyId, problemId, likeCount, question, answer, exampleAnswer, userId, userName, userImage, alreadyLiked);
    }

    @Override
    public String toString() {
        return "ReplyResponse{" +
                "historyId=" + historyId
                + "replyId=" + replyId
                + ", problemId=" + problemId
                + ", likeCount=" + likeCount
                + ", question='" + question + '\''
                + ", answer='" + answer + '\''
                + ", exampleAnswer='" + exampleAnswer + '\''
                + ", userId=" + userId
                + ", userName='" + userName + '\''
                + ", userImage='" + userImage + '\''
                + ", alreadyLiked=" + alreadyLiked
                + '}';
    }
}
