package cos.peerna.domain.problem.service;

import cos.peerna.domain.keyword.model.Keyword;
import cos.peerna.domain.keyword.repository.KeywordRepository;
import cos.peerna.domain.problem.dto.result.AnswerAndKeywordResult;
import cos.peerna.domain.problem.dto.result.KeywordResult;
import cos.peerna.domain.problem.dto.result.ProblemResult;
import cos.peerna.domain.problem.model.Problem;
import cos.peerna.domain.problem.repository.ProblemRepository;
import cos.peerna.domain.user.model.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final KeywordRepository keywordRepository;

    @Transactional
    public Long make(String question, String answer, Category category) {
        validateProblem(question);
        Problem newProblem = problemRepository.save(Problem.createProblem(question, answer, category));
        return newProblem.getId();
    }

    public KeywordResult findKeywordsByProblemId(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem Not Found"));
        List<Keyword> keywords = keywordRepository.findTop3KeywordsByProblemOrderByCountDesc(problem);
        List<String> keywordStrings = new ArrayList<>();
        for (Keyword keyword : keywords) {
            keywordStrings.add(keyword.getName());
        }
        return KeywordResult.builder()
                .keywords(keywordStrings)
                .build();
    }

    public AnswerAndKeywordResult getAnswerAndKeywordByProblemId(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem Not Found"));
        List<Keyword> top3Keywords = keywordRepository.findTop3KeywordsByProblemOrderByCountDesc(problem);

        return AnswerAndKeywordResult.of(problem.getAnswer(), top3Keywords);
    }

    public List<ProblemResult> findProblemsByCategory(Category category, Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("id").ascending());

        List<Problem> problems = problemRepository.findByCategoryAndIdGreaterThanOrderByIdAsc(category, cursorId, pageable);
        List<ProblemResult> problemResults = new ArrayList<>();
        for (Problem problem : problems) {
            problemResults.add(ProblemResult.builder()
                    .problemId(problem.getId())
                    .question(problem.getQuestion())
                    .answer(problem.getAnswer())
                    .category(problem.getCategory())
                    .build());
        }
        return problemResults;
    }

    private void validateProblem(String question) {
        problemRepository.findProblemByQuestion(question).ifPresent(p -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already Exist Problem");
        });
    }
}

