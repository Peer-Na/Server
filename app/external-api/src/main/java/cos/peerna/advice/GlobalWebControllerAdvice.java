package cos.peerna.advice;

import cos.peerna.global.security.LoginUser;
import cos.peerna.global.security.dto.SessionUser;
import jakarta.annotation.Nullable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalWebControllerAdvice {

    private static final String DEFAULT_USER_IMAGE = "https://avatars.githubusercontent.com/u/0?v=4";

    @ModelAttribute
    public void setUserProfile(@Nullable @LoginUser SessionUser user, Model model) {
        if (user == null) {
            model.addAttribute("userName", "Guest");
            model.addAttribute("userImage", DEFAULT_USER_IMAGE);
            return;
        }
        model.addAttribute("userId", user.getId());
        model.addAttribute("userName", user.getName());
        model.addAttribute("userImage", user.getImageUrl() != null ? user.getImageUrl() : DEFAULT_USER_IMAGE);
        model.addAttribute("userEmail", user.getEmail());
    }
}
