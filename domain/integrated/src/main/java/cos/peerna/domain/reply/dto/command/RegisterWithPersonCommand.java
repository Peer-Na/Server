package cos.peerna.domain.reply.dto.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

@Builder
@Validated
public record RegisterWithPersonCommand(
        @NotBlank @Length(max=1000) String answer
) {}
