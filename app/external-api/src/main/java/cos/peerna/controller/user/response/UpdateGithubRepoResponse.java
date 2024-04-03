package cos.peerna.controller.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UpdateGithubRepoResponse(
        @JsonProperty("github_repo")
        String githubRepo
) {
}
