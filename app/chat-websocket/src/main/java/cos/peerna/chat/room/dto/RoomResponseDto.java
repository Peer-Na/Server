package cos.peerna.chat.room.dto;

import cos.peerna.domain.problem.model.Problem;
import cos.peerna.domain.user.dto.UserProfile;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponseDto {
    private Integer roomId;
    private Long    historyId;
    private Problem problem;
    private UserProfile peer;
}
