package cos.peerna.global.common.controller;

import cos.peerna.domain.history.dto.response.DetailHistoryResponse;
import cos.peerna.domain.history.service.HistoryService;
import cos.peerna.domain.match.model.MatchTicket;
import cos.peerna.domain.match.service.MatchService;
import cos.peerna.domain.room.service.RoomService;
import cos.peerna.domain.user.model.Category;
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
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HistoryService historyService;
    private final RoomService roomService;
    private final MatchService matchService;
    private final UserService userService;
    private static final String DEFAULT_USER_IMAGE = "https://avatars.githubusercontent.com/u/0?v=4";

    @GetMapping("/")
    public String index(@Nullable @LoginUser SessionUser user, Model model) {
        if (user != null && roomService.findConnectedRoomId(user.getId()) != null) {
            return "redirect:/reply/multi";
        }
        MatchTicket matchTicket = user == null ? null : matchService.findTicketById(user.getId());
        setUserProfile(user, model);
        setPageTitle(model, "피어나");
        setStandbyInfo(model, matchTicket);
        return "pages/index";
    }

    @GetMapping("/reply/solo")
    public String soloStudy(@Nullable @LoginUser SessionUser user, Model model) {
        if (user == null) {
            return "redirect:/";
        }
        setUserProfile(user, model);
        setPageTitle(model, "Study - Solo");
        return "pages/reply/solo";
    }

    @GetMapping("/reply/multi")
    public String multiStudy(@Nullable @LoginUser SessionUser user, Model model) {
        if (user == null) {
            return "redirect:/";
        }
        Integer roomId = roomService.findConnectedRoomId(user.getId());
        if (roomId == null) {
            return "redirect:/";
        }
        Category category = roomService.findById(roomId).getCategory();
        setUserProfile(user, model);
        setPageTitle(model, "Study - Multi");
        setRoomInfo(model, category, roomId);
        return "pages/reply/multi";
    }

    @GetMapping("/reply/latest")
    public String latestReplies(@Nullable @LoginUser SessionUser user, Model model) {
        setUserProfile(user, model);
        setPageTitle(model, "최신 사람 답변 모음 - 피어나");
        return "pages/reply/latest";
    }

    @GetMapping("/reply/others")
    public String othersReplies(@Nullable @LoginUser SessionUser user, Model model) {
        setUserProfile(user, model);
        setPageTitle(model, "다른 사람 답변 모음 - 피어나");
        return "pages/reply/others";
    }

    @GetMapping("/mypage")
    public String myPage(@Nullable @LoginUser SessionUser sessionUser, Model model) {
        if (sessionUser == null) {
            return "redirect:/";
        }
        User userinfo = userService.findById(sessionUser.getId());
        model.addAttribute("repository", userinfo.getGithubRepo());
        setUserProfile(sessionUser, model);
        setPageTitle(model, "My Page - 피어나");

        return "pages/user/mypage";
    }

    @GetMapping("/reply/{id}")
    public String reply(@Nullable @LoginUser SessionUser user, Model model,
                        @Nullable @PathVariable("id") Long historyId) {
        DetailHistoryResponse detailHistory =
                historyService.findDetailHistory(historyId, user == null ? null : user.getId());
        setUserProfile(user, model);
        setPageTitle(model, "Reply - 피어나");
        setHistory(model, detailHistory);
        return "pages/reply/view";
    }

    private void setUserProfile(@Nullable SessionUser user, Model model) {
        Long userId = user != null ? user.getId() : null;
        String userName = user != null ? user.getName() : "Guest";
        String userImage = user != null ? user.getImageUrl() : DEFAULT_USER_IMAGE;
        String userEmail = user != null ? user.getEmail() : null;
        model.addAttribute("userId", userId);
        model.addAttribute("userName", userName);
        model.addAttribute("userImage", userImage);
        model.addAttribute("userEmail", userEmail);
    }

    private void setPageTitle(Model model, String title) {
        model.addAttribute("pageTitle", title);
    }

    private void setRoomInfo(Model model, Category category, Integer roomId) {
        model.addAttribute("category", category);
        model.addAttribute("roomId", roomId);
    }

    private void setHistory(Model model, DetailHistoryResponse history) {
        model.addAttribute("history", history);
    }

    private void setStandbyInfo(Model model, MatchTicket matchTicket) {
        model.addAttribute("category", matchTicket == null ? null : matchTicket.getCategory());
    }

    /*
     NOTICE: 아래 페이지들은 테스트를 위해 존재. 실제 서비스에서는 제거
     */
    @GetMapping("/login")
    public String login() {
        return "pages/user/login";
    }

    @GetMapping("/signup")
    public String signUp() {
        return "pages/user/signUp";
    }
}
