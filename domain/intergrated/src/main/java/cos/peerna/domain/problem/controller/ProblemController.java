package cos.peerna.domain.problem.controller;

import cos.peerna.domain.problem.dto.request.RegisterProblemRequest;
import cos.peerna.domain.problem.dto.response.AnswerAndKeywordResponse;
import cos.peerna.domain.problem.dto.response.KeywordResponse;
import cos.peerna.domain.problem.dto.response.ProblemResponse;
import cos.peerna.domain.problem.service.ProblemService;
import cos.peerna.domain.user.model.Category;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/answer")
    public ResponseEntity<AnswerAndKeywordResponse> getAnswerAndKeyword(@RequestParam Long problemId) {
        return ResponseEntity.ok(problemService.getAnswerAndKeywordByProblemId(problemId));
    }

    @GetMapping("/keyword")
    public ResponseEntity<KeywordResponse> findKeywords(
            @RequestParam Long problemId) {
        return ResponseEntity.ok(problemService.findKeywordsByProblemId(problemId));
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<ProblemResponse>> findProblemsByCategory(
            @PathVariable Category category,
            @RequestParam(required = false, defaultValue = "0") Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(problemService.findProblemsByCategory(category, cursorId, size));
    }
}
