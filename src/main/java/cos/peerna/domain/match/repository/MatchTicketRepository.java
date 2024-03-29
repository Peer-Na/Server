package cos.peerna.domain.match.repository;

import cos.peerna.domain.match.model.MatchTicket;
import cos.peerna.domain.user.model.Category;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface MatchTicketRepository extends CrudRepository<MatchTicket, Long> {
    List<MatchTicket> findByCategoryOrderByScore(Category category);
}
