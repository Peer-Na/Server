package cos.peerna.domain.reply.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

@Builder
@Validated
public record RegisterWithGPTCommand(
        @NotBlank @Length(max=1000) String answer,
        @NotNull Long problemId
) {
}
