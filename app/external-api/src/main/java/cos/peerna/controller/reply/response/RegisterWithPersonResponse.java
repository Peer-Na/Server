package cos.peerna.controller.reply.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

@Validated
public record RegisterWithPersonResponse(
        @JsonProperty("reply_id")
        Long replyId
) {}
