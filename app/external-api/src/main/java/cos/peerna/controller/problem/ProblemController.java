package cos.peerna.controller.problem;

import cos.peerna.controller.problem.request.RegisterProblemRequest;
import cos.peerna.controller.problem.response.AnswerAndKeywordResponse;
import cos.peerna.controller.problem.response.KeywordResponse;
import cos.peerna.controller.problem.response.ProblemListResponse;
import cos.peerna.domain.problem.service.ProblemService;
import cos.peerna.domain.user.model.Category;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problem")
public class ProblemController {

    private static final String PROBLEM_PREFIX = "/api/problem/";

    private final ProblemService problemService;

    @PostMapping
    public ResponseEntity<String> registerProblem(@RequestBody RegisterProblemRequest request) {
        Long problemId = problemService.make(request.question(), request.answer(), request.category());
        URI location = URI.create(PROBLEM_PREFIX + problemId);
        return ResponseEntity.created(location).build();
    }

    /*
    TODO: /answer, /keyword 2개를 하나로 줄이기
     */
    @GetMapping("/answer")
    public ResponseEntity<AnswerAndKeywordResponse> getAnswerAndKeyword(@RequestParam Long problemId) {
        return ResponseEntity.ok(AnswerAndKeywordResponse.of(problemService.getAnswerAndKeywordByProblemId(problemId)));
    }

    @GetMapping("/keyword")
    public ResponseEntity<KeywordResponse> findKeywords(
            @RequestParam Long problemId) {
        return ResponseEntity.ok(KeywordResponse.of(problemService.findKeywordsByProblemId(problemId)));
    }

    @GetMapping("/{category}")
    public ResponseEntity<ProblemListResponse> findProblemsByCategory(
            @PathVariable Category category,
            @RequestParam(required = false, defaultValue = "0") Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(ProblemListResponse.builder()
                .problems(problemService.findProblemsByCategory(category, cursorId, size))
                .build());
    }
}
