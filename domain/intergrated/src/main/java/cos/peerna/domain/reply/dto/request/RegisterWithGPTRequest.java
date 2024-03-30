package cos.peerna.domain.reply.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

@Validated
public record RegisterWithGPTRequest(
        @NotBlank @Length(max=1000) String answer,
        @NotNull Long problemId
) {
    @Override
    public String toString() {
        return "RegisterReplyWithGPTRequest{" +
                "answer='" + answer + '\'' +
                ", problemId=" + problemId +
                '}';
    }
}
