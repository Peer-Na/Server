package cos.peerna.domain.room.model;

import cos.peerna.domain.history.model.History;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat", indexes = {
		@Index(name = "idx_chat_history", columnList = "history_id")
})
public class Chat {

	@Id
	@GeneratedValue
	@Column(name = "chat_id")
	private Long id;

	@NotNull
	private Long writerId;

	@Column(length = 500)
	private String content;

	private LocalTime time;

	@NotNull @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "history_id")
	private History history;

	@Builder
	public Chat(Long writerId, String content, History history) {
		this.writerId = writerId;
		this.content = content;
		this.history = history;
		this.time = LocalTime.now();
	}
}
