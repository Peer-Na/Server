package cos.peerna.controller.user;

import cos.peerna.controller.dto.ResponseDto;
import cos.peerna.controller.user.request.UpdateGithubRepoRequest;
import cos.peerna.controller.user.request.UserRegisterRequest;
import cos.peerna.controller.user.response.UserRegisterResponse;
import cos.peerna.domain.user.dto.UserProfile;
import cos.peerna.domain.user.service.UserService;
import cos.peerna.global.security.LoginUser;
import cos.peerna.global.security.dto.SessionUser;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseDto<UserRegisterResponse>> signUp(@RequestBody UserRegisterRequest request) {
        Long userId = userService.signUp(request.toServiceDto());
        return ResponseEntity.created(URI.create(userId.toString())).body(
                ResponseDto.of(new UserRegisterResponse(userId)));
    }

    @DeleteMapping
    public ResponseEntity<Void> signOut(@LoginUser SessionUser user) {
        userService.delete(user.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseDto<UserProfile>> me(@LoginUser SessionUser user) {
        return ResponseEntity.ok()
                .body(ResponseDto.of(UserProfile.builder()
                        .userId(user.getId())
                        .userName(user.getName())
                        .userImage(user.getImageUrl())
                        .userEmail(user.getEmail())
                        .build()));
    }

    @PostMapping("/follow")
    public ResponseEntity<String> follow(@LoginUser SessionUser user, @RequestParam Long followeeId) {
        userService.follow(user.getId(), followeeId);
        return ResponseEntity.ok()
                .body("success");
    }

    @DeleteMapping("/follow")
    public ResponseEntity<String> unfollow(@LoginUser SessionUser user, @RequestParam Long followeeId) {
        userService.unfollow(user.getId(), followeeId);
        return ResponseEntity.ok()
                .body("success");
    }

    @PatchMapping("/github-repo")
    public ResponseEntity<Void> updateGithubRepo(@LoginUser SessionUser user,
                                                 @RequestBody UpdateGithubRepoRequest request) {
        userService.updateGithubRepo(user.getId(), request.githubRepo());
        return ResponseEntity.noContent().build();
    }
}
