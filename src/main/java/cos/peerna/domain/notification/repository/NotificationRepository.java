package cos.peerna.domain.notification.repository;

import cos.peerna.domain.notification.model.Notification;
import cos.peerna.domain.user.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("SELECT n FROM Notification n JOIN FETCH n.reply r JOIN FETCH r.problem p WHERE n.user = :user")
	List<Notification> findByUser(User user);

	List<Notification> findAllByUser(User user);

	@Query("SELECT n FROM Notification n JOIN FETCH n.reply r JOIN FETCH r.problem p WHERE n.id = :id")
	Optional<Notification> findNotificationById(Long id);

	void deleteAllByUser(User user);

}
