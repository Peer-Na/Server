package cos.peerna.domain.user.dto;

import lombok.Builder;

@Builder
public record UserProfile(
        Long userId,
        String name,
        String imageUrl,
        Integer score
) {}
