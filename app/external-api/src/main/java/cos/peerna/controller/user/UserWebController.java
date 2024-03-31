package cos.peerna.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserWebController {

    @GetMapping("/login")
    public String login() {
        return "pages/user/login";
    }

    @GetMapping("/signup")
    public String signUp() {
        return "pages/user/signUp";
    }
}
