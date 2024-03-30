package cos.peerna.domain.github.domain;

import cos.peerna.domain.problem.model.Problem;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.reactive.function.client.WebClient;

/*
TODO: Github Class 분리를 하는게 좋을지, 하면 어떻게 하는게 좋을지 의사판단
 */

@Getter
@Setter
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
    private final String branch;
    private String ref;
    private String refSha;
    private String blobSha;
    private String treeSha;
    private String commitSha;

    private Github(String token, String login, String repo) {
        this.token = token;
        this.login = login;
        this.repo = repo;
        this.branch = DEFAULT_BRANCH;
    }

    public static Github of(String token, String login, String repo) {
        return new Github(token, login, repo);
    }

    public static String makeContentWithEncode(Problem problem, String answer) {
        return String.format("# Q.%s\n### 카테고리: %s\n## A.\n%s", problem.getQuestion(), problem.getCategory(), answer);
        /*
        TODO: 답변하는데 소요된 시간, 키워드 등 정보추가
         */
    }

    public static String makeFilePath(Problem problem) {
        return problem.getCategory() + "/" + problem.getQuestion() + ".md";
    }

    public static String makeCommitMessage(Problem problem) {
        return String.format("[%s] %s", problem.getCategory(), problem.getQuestion());
    }

    public WebClient getGithubClient(WebClient defaultClient) {
        return defaultClient.mutate()
                .baseUrl(BASE_URL)
                .defaultHeader("Authorization", "Bearer " + this.getToken())
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
    }
}
