package cos.peerna.domain.notification.model;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum NotificationType {
	PULL_REQ, PULL_REQ_ACC, FOLLOW, FOLLOW_EACH, NORMAL;

	@JsonCreator
	public static NotificationType from(String s) {
		return NotificationType.valueOf(s.toUpperCase());
	}
}
