package cos.peerna.domain.reply.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

@Validated
public record RegisterWithPersonRequest(
        @NotBlank @Length(max=1000) String answer
) {
    @Override
    public String toString() {
        return "RegisterReplyWithPersonRequest{" +
                "answer='" + answer + '\'' +
                '}';
    }
}
