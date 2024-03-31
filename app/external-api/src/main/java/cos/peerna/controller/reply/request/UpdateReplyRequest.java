package cos.peerna.controller.reply.request;

import cos.peerna.domain.reply.dto.command.UpdateReplyCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

@Validated
public record UpdateReplyRequest(
        @NotNull Long problemId,
        @NotBlank @Length(max=1000) String answer
) {
    public UpdateReplyCommand toServiceDto() {
        return UpdateReplyCommand.builder()
                .problemId(problemId)
                .answer(answer)
                .build();
    }
}
