package cos.peerna.domain.match.repository;

import cos.peerna.domain.match.model.Standby;
import cos.peerna.domain.user.model.Category;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface StandbyRepository extends CrudRepository<Standby, Long> {
    List<Standby> findByCategoryOrderByScore(Category category);
}
