package cos.peerna.controller.reply.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import cos.peerna.domain.reply.dto.command.RegisterWithGPTCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

@Validated
public record RegisterWithGPTRequest(
        @NotBlank @Length(max=1000) String answer,
        @JsonProperty("problem_id")
        @NotNull Long problemId
) {
    public RegisterWithGPTCommand toServiceDto() {
        return RegisterWithGPTCommand.builder()
                .answer(answer)
                .problemId(problemId)
                .build();
    }
}
