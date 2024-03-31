package cos.peerna.domain.history.repository;

import cos.peerna.domain.history.model.History;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("select h from History h join fetch h.problem where h.id = :id")
    Optional<History> findByIdWithProblem(Long id);
}
