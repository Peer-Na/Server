package cos.peerna.controller;

import cos.peerna.domain.user.model.User;
import cos.peerna.domain.user.service.UserService;
import cos.peerna.global.security.LoginUser;
import cos.peerna.global.security.dto.SessionUser;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping("/")
    public String index(@Nullable @LoginUser SessionUser user, Model model) {
        model.addAttribute("pageTitle", "피어나");
        return "pages/index";
    }

    @GetMapping("/mypage")
    public String myPage(@Nullable @LoginUser SessionUser sessionUser, Model model) {
        if (sessionUser == null) {
            return "redirect:/";
        }
        User userinfo = userService.findById(sessionUser.getId());
        model.addAttribute("repository", userinfo.getGithubRepo());
        model.addAttribute("pageTitle", "My Page - 피어나");
        return "pages/user/mypage";
    }
}
