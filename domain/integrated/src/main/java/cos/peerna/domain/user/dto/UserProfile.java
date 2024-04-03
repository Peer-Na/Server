package cos.peerna.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UserProfile(
        @JsonProperty("user_id")
        Long userId,
        String name,
        @JsonProperty("image_url")
        String imageUrl,
        String email
) {}
