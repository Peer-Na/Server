package cos.peerna.domain.room.repository;

import cos.peerna.domain.history.model.History;
import cos.peerna.domain.room.model.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByHistory(History history);
}
