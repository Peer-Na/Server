package cos.peerna.domain.user.dto;

import lombok.Data;

@Data
public class UserRegisterRequestDto {
    private Long id;
    private String name;
    private String email;
    private String password;
}



