package cos.peerna.domain.keyword.repository;

import cos.peerna.domain.keyword.model.Keyword;
import cos.peerna.domain.problem.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findTop3KeywordsByProblemOrderByCountDesc(Problem problem);
    Optional<Keyword> findKeywordByNameAndProblem(String name, Problem problem);
}
