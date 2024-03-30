package cos.peerna.domain.notification.dto.data;

import cos.peerna.domain.user.dto.data.UserProfileData;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class NotificationData {
	private Long        notificationId;
	private String		type;
	private String      answer;
	private UserProfileData sender;
	private String      msg;
	private LocalDate   time;
}
