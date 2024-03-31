package cos.peerna.controller.notification.response;

import cos.peerna.domain.user.dto.data.UserProfileData;
import java.time.LocalDate;

public record NotificationResponse(
		Long notificationId,
		String type,
		String answer,
		UserProfileData sender,
		String msg,
		LocalDate time
) {}