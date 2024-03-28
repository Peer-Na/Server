package cos.peerna.domain.room.dto;

import cos.peerna.domain.user.dto.data.UserProfileData;
import cos.peerna.domain.problem.model.Problem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponseDto {
    private Integer roomId;
    private Long    historyId;
    private Problem problem;
    private UserProfileData peer;

}
