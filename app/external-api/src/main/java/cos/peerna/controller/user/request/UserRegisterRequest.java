package cos.peerna.controller.user.request;

import cos.peerna.domain.user.dto.command.UserRegisterCommand;

public record UserRegisterRequest(
        Long id,
        String name,
        String email,
        String password
) {

    public UserRegisterCommand toServiceDto() {
        return new UserRegisterCommand(
                id,
                name,
                email,
                password
        );
    }
}



