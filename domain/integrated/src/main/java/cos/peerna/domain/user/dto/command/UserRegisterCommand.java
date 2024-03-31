package cos.peerna.domain.user.dto.command;

public record UserRegisterCommand(
        Long id,
        String name,
        String email,
        String password
) {
}
