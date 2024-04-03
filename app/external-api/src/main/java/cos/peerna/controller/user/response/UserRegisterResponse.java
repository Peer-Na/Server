package cos.peerna.controller.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserRegisterResponse(
        @JsonProperty("user_id")
        Long userId
) {}



