package cos.peerna.domain.reply.model;

import cos.peerna.domain.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "likey", indexes = {
		@Index(name = "idx_likey_reply", columnList = "reply_id")
})
public class Likey {

	@Id
	@GeneratedValue
	@Column(name = "like_id")
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	@JsonIgnore
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reply_id")
	private Reply reply;

	@Builder
	public Likey(User user, Reply reply) {
		this.user = user;
		this.reply = reply;
	}
}
