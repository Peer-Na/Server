package cos.peerna.controller.reply;

import cos.peerna.domain.history.dto.result.DetailHistoryResult;
import cos.peerna.domain.history.service.HistoryService;
import cos.peerna.domain.room.service.RoomService;
import cos.peerna.domain.user.model.Category;
import cos.peerna.global.security.LoginUser;
import cos.peerna.global.security.dto.SessionUser;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/reply")
@RequiredArgsConstructor
public class ReplyWebController {

    private final HistoryService historyService;
    private final RoomService roomService;

    @GetMapping("/solo")
    public String soloStudy(@Nullable @LoginUser SessionUser user, Model model) {
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("pageTitle", "Study - Solo");
        return "pages/reply/solo";
    }

    @GetMapping("/multi")
    public String multiStudy(@Nullable @LoginUser SessionUser user, Model model) {
        if (user == null) {
            return "redirect:/";
        }
        Integer roomId = roomService.findConnectedRoomId(user.getId());
        if (roomId == null) {
            return "redirect:/";
        }
        Category category = roomService.findById(roomId).getCategory();
        model.addAttribute("pageTitle", "Study - Multi");
        model.addAttribute("category", category);
        model.addAttribute("roomId", roomId);
        return "pages/reply/multi";
    }

    @GetMapping("/latest")
    public String latestReplies(@Nullable @LoginUser SessionUser user, Model model) {
        model.addAttribute("pageTitle", "최신 사람 답변 모음 - 피어나");
        return "pages/reply/latest";
    }

    @GetMapping("/others")
    public String othersReplies(@Nullable @LoginUser SessionUser user, Model model) {
        model.addAttribute("pageTitle", "다른 사람 답변 모음 - 피어나");
        return "pages/reply/others";
    }

    @GetMapping("/{id}")
    public String reply(@Nullable @LoginUser SessionUser user, Model model,
                        @Nullable @PathVariable("id") Long historyId) {
        DetailHistoryResult detailHistory =
                historyService.findDetailHistory(historyId, user == null ? null : user.getId());
        model.addAttribute("pageTitle", "Reply - 피어나");
        model.addAttribute("history", detailHistory);
        return "pages/reply/view";
    }
}
