package cos.peerna.domain.github.service;

import cos.peerna.domain.github.domain.Github;
import cos.peerna.domain.github.service.response.CreateBlobResponse;
import cos.peerna.domain.github.service.response.CreateCommitResponse;
import cos.peerna.domain.github.service.response.CreateTreeResponse;
import cos.peerna.domain.github.service.response.GetReferenceResponse;
import cos.peerna.domain.github.service.response.GitObject;
import cos.peerna.domain.problem.model.Problem;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubService {

    private final WebClient defaultClient;

    @Async
    public void commitReply(Github github, Problem problem, String answer) {
        WebClient webClient = github.getGithubClient(defaultClient);

        getRefAndSha(webClient, github)
                .flatMap(success -> createBlob(webClient, github, problem, answer))
                .flatMap(success -> createTree(webClient, github, problem))
                .flatMap(success -> createCommit(webClient, github, problem))
                .flatMap(success -> updateHead(webClient, github))
                .subscribe(
                        success -> log.info("Commit was successful"),
                        error -> log.error("Error during commit: " + error.getMessage())
                        /*
                        TODO: 성공 및 실패 이벤트 발생으로 클라이언트에게 알람 전송
                         */
                );
    }

    public Mono<String> getRefAndSha(WebClient webClient, Github github) {
        Mono<GetReferenceResponse> mono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(Github.GET_REF_PATH_PARAM)
                        .build(github.getLogin(), github.getRepo(), Github.DEFAULT_BRANCH)
                )
                .retrieve()
                .bodyToMono(GetReferenceResponse.class);
        return mono.flatMap(response -> {
            github.setRef(response.ref());
            github.setRefSha(response.object().sha());
            log.debug("getRefAndSha() Response: {}", response);
            return Mono.just("success");
        });
    }

    public Mono<String> createBlob(WebClient webClient, Github github, Problem problem, String answer) {
        String content = Github.makeContentWithEncode(problem, answer);
        String encodedContent = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        String payload = String.format("{\"content\":\"%s\",\"encoding\":\"base64\"}", encodedContent);

        Mono<CreateBlobResponse> mono = webClient.post()
                .uri(uriBuilder -> uriBuilder.path(Github.CREATE_BLOB_PATH_PARAM)
                        .build(github.getLogin(), github.getRepo()))
                .body(Mono.just(payload), String.class)
                .retrieve()
                .bodyToMono(CreateBlobResponse.class);
        return mono.flatMap(response -> {
            github.setBlobSha(response.sha());
            log.debug("createBlob() Response: {}", response);
            return Mono.just("success");
        });
    }

    public Mono<String> createTree(WebClient webClient, Github github,
                                   Problem problem) {
        String filePath = Github.makeFilePath(problem);

        String treeElement = String.format("{\"path\":\"%s\",\"mode\":\"%s\",\"type\":\"%s\",\"sha\":\"%s\"}",
                filePath, Github.MODE_FILE, Github.TYPE_BLOB, github.getBlobSha());

        String payload = "{\"base_tree\":\"" + github.getRefSha() + "\",\"tree\":[" + treeElement + "]}";
        log.debug("Payload: {}", payload);

        Mono<CreateTreeResponse> mono = webClient.post()
                .uri(uriBuilder -> uriBuilder.path(Github.CREATE_TREE_PATH_PARAM)
                        .build(github.getLogin(), github.getRepo()))
                .body(Mono.just(payload), String.class)
                .retrieve()
                .bodyToMono(CreateTreeResponse.class);

        return mono.flatMap(response -> {
            log.debug("createTree() Response: {}", response);
            github.setTreeSha(response.sha());
            return Mono.just("success");
        });
    }

    public Mono<String> createCommit(WebClient webClient, Github github, Problem problem) {
        String message = Github.makeCommitMessage(problem);
        String payload = String.format("{\"message\":\"%s\",\"tree\":\"%s\",\"parents\":[\"%s\"]}",
                message, github.getTreeSha(), github.getRefSha());

        Mono<CreateCommitResponse> mono = webClient.post()
                .uri(uriBuilder -> uriBuilder.path(Github.CREATE_COMMIT_PATH_PARAM)
                        .build(github.getLogin(), github.getRepo()))
                .body(Mono.just(payload), String.class)
                .retrieve()
                .bodyToMono(CreateCommitResponse.class);
        return mono.flatMap(response -> {
            github.setCommitSha(response.sha());
            log.debug("createCommit() Response: {}", response);
            return Mono.just("success");
        });
    }

    public Mono<String> updateHead(WebClient webClient, Github github) {

        String payload = String.format("{\"sha\":\"%s\",\"force\":true}", github.getCommitSha());

        Mono<GitObject> mono = webClient.patch()
                .uri(uriBuilder -> uriBuilder.path(Github.UPDATE_HEAD_PATH_PARAM)
                        .build(github.getLogin(), github.getRepo()))
                .body(Mono.just(payload), String.class)
                .retrieve()
                .bodyToMono(GitObject.class);
        return mono.flatMap(response -> {
            log.debug("updateHead() Response: {}", response);
            return Mono.just("success");
        });
    }
}
