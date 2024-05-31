package cos.peerna.model;

import cos.peerna.event.CommitReplyEvent;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.reactive.function.client.WebClient;

/*
TODO: Github Class 분리를 하는게 좋을지, 하면 어떻게 하는게 좋을지 의사판단
 */

@Getter
@Setter
@RequiredArgsConstructor
public class Github {
    public static final String BASE_URL = "https://api.github.com/repos/";
    public static final String GET_REF_PATH_PARAM = "{owner}/{repo}/git/ref/{ref}";
    public static final String CREATE_BLOB_PATH_PARAM = "{owner}/{repo}/git/blobs";
    public static final String CREATE_TREE_PATH_PARAM = "{owner}/{repo}/git/trees";
    public static final String CREATE_COMMIT_PATH_PARAM = "{owner}/{repo}/git/commits";
    public static final String UPDATE_HEAD_PATH_PARAM = "{owner}/{repo}/git/refs/heads/main";
    public static final String DEFAULT_BRANCH = "heads/main";
    public static final String MODE_FILE = "100644";
    public static final String TYPE_BLOB = "blob";
    private final String token;
    private final String login;
    private final String repo;
    private final String payload;
    private final String filePath;
    private final String commitMessage;
    private String branch = DEFAULT_BRANCH;
    private String ref;
    private String refSha;
    private String blobSha;
    private String treeSha;
    private String commitSha;

    public static Github from(CommitReplyEvent event) {
        String payload = makePayload(event.question(), event.category(), event.answer());
        String filePath = makeFilePath(event.category(), event.question());
        String commitMessage = makeCommitMessage(event.category(), event.question());
        return new Github(event.githubToken(), event.githubLogin(), event.githubRepo(), payload, filePath, commitMessage);
    }

    public WebClient getGithubClient(WebClient defaultClient) {
        return defaultClient.mutate()
            .baseUrl(BASE_URL)
            .defaultHeader("Authorization", "Bearer " + this.getToken())
            .defaultHeader("Accept", "application/vnd.github+json")
            .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
            .build();
    }

    private static String makePayload(String question, String category, String answer) {
        String content =  String.format("# Q.%s\n### 카테고리: %s\n## A.\n%s", question, category, answer);
        String encodedContent = Base64.getEncoder().encodeToString(content.getBytes(
            StandardCharsets.UTF_8));
        return String.format("{\"content\":\"%s\",\"encoding\":\"base64\"}", encodedContent);
        /*
        TODO: 답변하는데 소요된 시간, 키워드 등 정보추가
         */
    }

    private static String makeFilePath(String category, String question) {
        return category + "/" + question + ".md";
    }

    private static String makeCommitMessage(String category, String question) {
        return String.format("[%s] %s", category, question);
    }
}
