package cos.peerna.domain.room.repository;

import cos.peerna.domain.history.model.History;
import cos.peerna.domain.room.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByHistory(History history);
}
