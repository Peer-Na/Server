package cos.peerna.domain.notification.model;

import cos.peerna.domain.reply.model.Reply;
import cos.peerna.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

	@Id @GeneratedValue
	private Long id;

	private String msg;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reply_id", nullable = true)
	private Reply reply;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = true)
	private User follower;

	@Enumerated(EnumType.STRING)
	private NotificationType type;

	private LocalDate time;

	@Builder
	public Notification(String msg, User user, Reply reply, User follower, NotificationType type) {
		this.msg = msg;
		this.user = user;
		this.reply = reply;
		this.follower = follower;
		this.type = type;
		this.time = LocalDate.now();
	}

	public static void acceptNotification(Notification notification) {
		if (notification.type.equals(NotificationType.PULL_REQ)) {
			notification.type = NotificationType.PULL_REQ_ACC;
			notification.msg = "Pull-Request가 수락되었습니다.";
		}
		else if (notification.type.equals(NotificationType.FOLLOW)) {
			notification.type = NotificationType.FOLLOW_EACH;
		}
		notification.time = LocalDate.now();
	}

	public static boolean isPRNotification(Notification notification) {
		return notification.type.equals(NotificationType.PULL_REQ_ACC);
	}

	public static boolean isFollowNotification(Notification notification) {
		return notification.type.equals(NotificationType.FOLLOW);
	}
}
