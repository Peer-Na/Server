package cos.peerna.domain.notification.dto;

import cos.peerna.domain.notification.dto.data.NotificationData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NotificationResponseDto {
	private List<NotificationData> notificationList;
}
