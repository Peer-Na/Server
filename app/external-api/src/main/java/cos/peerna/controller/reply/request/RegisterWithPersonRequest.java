package cos.peerna.controller.reply.request;

import cos.peerna.domain.reply.dto.command.RegisterWithPersonCommand;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

@Validated
public record RegisterWithPersonRequest(
        @NotBlank @Length(max=1000) String answer
) {
    public RegisterWithPersonCommand toServiceDto() {
        return RegisterWithPersonCommand.builder()
                .answer(answer)
                .build();
    }
}
