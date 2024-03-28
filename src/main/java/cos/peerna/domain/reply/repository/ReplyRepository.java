package cos.peerna.domain.reply.repository;

import cos.peerna.domain.history.model.History;
import cos.peerna.domain.problem.model.Problem;
import cos.peerna.domain.reply.model.Reply;
import cos.peerna.domain.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("select r from Reply r join fetch r.problem where r.id = :id")
    Optional<Reply> findWithUserAndProblemById(Long id);
    @Query("select r from Reply r left join fetch r.likes where r.id = :id")
    Optional<Reply> findByIdWithUserAndLike(Long id);
    @Query("select r from Reply r join fetch r.history join fetch r.problem "
            + "where r.id > :cursorId and r.user.id = :userId order by r.id asc")
    List<Reply> findRepliesByUserIdOrderByIdAsc(Long userId, Long cursorId, Pageable pageable);
    Page<Reply> findRepliesByProblemOrderByLikeCountDesc(Problem problem, Pageable pageable);
    Optional<Reply> findFirstByUserAndProblemOrderByIdDesc(User user, Problem problem);
    List<Reply> findRepliesWithUserByHistoryOrderByHistoryIdDesc(History history);

    List<Reply> findRepliesByOrderByIdDesc(Pageable pageable);
    List<Reply> findRepliesByIdLessThanOrderByIdDesc(Long cursorId, Pageable pageable);

    @Query("select r from Reply r join fetch r.history where r.user.id = :userId order by r.id desc limit 1")
    Optional<Reply> findFirstByUserIdOrderByIdDesc(Long userId);
}
