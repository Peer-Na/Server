package cos.peerna.controller.reply.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterWithGPTResponse(
        @JsonProperty("reply_id")
        Long reply_id
) {}
