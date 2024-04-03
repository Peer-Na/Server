package cos.peerna.controller.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateGithubRepoRequest(
        @JsonProperty("github_repo")
        String githubRepo
) {
}
