package cos.peerna.domain.notification.dto.data;

import cos.peerna.domain.user.dto.UserProfile;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class NotificationData {
	private Long        notificationId;
	private String		type;
	private String      answer;
	private UserProfile sender;
	private String      msg;
	private LocalDate   time;
}
