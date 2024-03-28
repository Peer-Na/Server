package cos.peerna.domain.keyword.service;

import cos.peerna.domain.keyword.model.Keyword;
import cos.peerna.domain.keyword.repository.KeywordRepository;
import cos.peerna.domain.problem.model.Problem;
import cos.peerna.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import scala.collection.Seq;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KeywordService {

    private final ProblemRepository problemRepository;
    private final KeywordRepository keywordRepository;

    public void make(String name, Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem Not Found"));
        Optional<Keyword> find = keywordRepository.findKeywordByNameAndProblem(name, problem);

        if (find.isEmpty()) {
            Keyword keyword = Keyword.createKeyword(name, problem);
            keywordRepository.save(keyword);
        }
        else {
            Keyword.updateKeyword(find.get());
        }
    }

    public void analyze(String answer, Long problemId) {
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(answer);
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
        List<KoreanTokenJava> koreanTokens = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);

        Map<String, Integer> map = new HashMap<String, Integer>();
        for (KoreanTokenJava token : koreanTokens) {
            if (token.toString().contains("Noun") && token.getText().length() > 1) {
                if (map.containsKey(token.getText())) {
                    map.put(token.getText(), map.get(token.getText()) + 1);
                } else {
                    map.put(token.getText(), 1);
                }
            }
        }

        ArrayList<Keyword> keywords = new ArrayList<>();
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem Not Found"));
        for (String key : map.keySet()) {
            Optional<Keyword> findKeyword = keywordRepository.findKeywordByNameAndProblem(key, problem);
            if (findKeyword.isEmpty()) {
                Keyword keyword = Keyword.builder()
                        .name(key)
                        .problem(problem)
                        .count((long) map.get(key))
                        .build();
                keywords.add(keyword);
            } else {
                Keyword.updateKeyword(findKeyword.get());
            }
        }
        keywordRepository.saveAll(keywords);
    }
}

