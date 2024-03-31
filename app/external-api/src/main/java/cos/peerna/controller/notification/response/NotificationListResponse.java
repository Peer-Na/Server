package cos.peerna.controller.notification.response;

import java.util.List;

public record NotificationListResponse(
		List<NotificationResponse> notificationList
) {}