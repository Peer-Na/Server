package cos.peerna.domain.user.controller;

import cos.peerna.domain.user.dto.UserRegisterRequestDto;
import cos.peerna.domain.user.dto.request.UpdateGithubRepoRequest;
import cos.peerna.domain.user.service.UserService;
import cos.peerna.global.security.LoginUser;
import cos.peerna.global.security.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Long> signUp(@RequestBody UserRegisterRequestDto dto) {
        return ResponseEntity.ok(userService.signUp(dto));
    }

    @DeleteMapping
    public ResponseEntity<String> signOut(@LoginUser SessionUser user) {
        userService.delete(user);

        return ResponseEntity.ok()
                .body("success");
    }

    @GetMapping("/info")
    public ResponseEntity<SessionUser> userStatus(@LoginUser SessionUser user) {
        return ResponseEntity.ok()
                .body(user);
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
    public ResponseEntity<String> updateGithubRepo(@LoginUser SessionUser user,
                                                   @RequestBody UpdateGithubRepoRequest request) {
        userService.updateGithubRepo(user.getId(), request.githubRepo());
        return ResponseEntity.ok()
                .body("success");
    }
}
