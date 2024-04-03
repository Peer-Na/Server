package cos.peerna.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UserProfile(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("user_name")
        String userName,
        @JsonProperty("user_image")
        String userImage,
        @JsonProperty("user_email")
        String userEmail
) {}
