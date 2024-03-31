package cos.peerna.controller.notification.response;

import cos.peerna.domain.user.dto.UserProfile;
import java.time.LocalDate;

public record NotificationResponse(
		Long notificationId,
		String type,
		String answer,
		UserProfile sender,
		String msg,
		LocalDate time
) {}