package cos.peerna.domain.reply.repository;

import cos.peerna.domain.reply.model.Likey;
import cos.peerna.domain.reply.model.Reply;
import cos.peerna.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeyRepository extends JpaRepository<Likey, Long> {
    Optional<Likey> findLikeyByUserAndReply(User user, Reply reply);
}
