package cos.peerna.controller.reply;

import cos.peerna.controller.reply.request.RegisterWithGPTRequest;
import cos.peerna.controller.reply.request.RegisterWithPersonRequest;
import cos.peerna.controller.reply.request.UpdateReplyRequest;
import cos.peerna.controller.reply.response.ReplyListResponse;
import cos.peerna.controller.reply.response.ReplyWithPageInfoResponse;
import cos.peerna.domain.reply.service.ReplyService;
import cos.peerna.global.security.LoginUser;
import cos.peerna.global.security.dto.SessionUser;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reply")
public class ReplyController {

	private final ReplyService replyService;

	@GetMapping
	public ResponseEntity<ReplyListResponse> findReplies(
			@RequestParam(required = false, defaultValue = "0") Long cursorId,
			@RequestParam(required = false, defaultValue = "10") int size) {
		return ResponseEntity.ok(ReplyListResponse.of(replyService.findReplies(cursorId, size)));
	}

	@GetMapping("/problem")
	public ResponseEntity<ReplyWithPageInfoResponse> getRepliesByProblem(
			@RequestParam Long problemId,
			@RequestParam @Nullable int page) {
		return ResponseEntity.ok(ReplyWithPageInfoResponse.of(replyService.findRepliesByProblem(problemId, page)));
	}

	@GetMapping("/user")
    public ResponseEntity<ReplyListResponse> findUserReplies(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "0") Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(ReplyListResponse.of(replyService.findUserReplies(userId, cursorId, size)));
    }

	@PostMapping("/gpt")
	public ResponseEntity<Void> registerReplyWithGPT(@LoginUser SessionUser user,
											  @RequestBody RegisterWithGPTRequest request) {
		String replyId = replyService.registerWithGPT(request.toServiceDto(), user);
		return ResponseEntity.created(URI.create(replyId)).build();
	}

	@PostMapping("/person")
	public ResponseEntity<Void> registerReplyWithPerson(@LoginUser SessionUser user,
														@RequestBody RegisterWithPersonRequest request) {
		String replyId = replyService.registerWithPerson(request.toServiceDto(), user);
		return ResponseEntity.created(URI.create(replyId)).build();
	}

	@PatchMapping
	public ResponseEntity<Void> modifyReply(@LoginUser SessionUser user, @RequestBody UpdateReplyRequest request) {
		replyService.modify(request.toServiceDto(), user);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/likey")
	public ResponseEntity<String> addLikey(@LoginUser SessionUser user, @RequestParam Long replyId) {
		String likeyId = replyService.addLikey(user, replyId);
		return ResponseEntity.created(URI.create(likeyId)).build();
	}

	@DeleteMapping("/likey")
	public ResponseEntity<Void> deleteLikey(@LoginUser SessionUser user, @RequestParam Long replyId) {
		replyService.deleteLikey(user, replyId);
		return ResponseEntity.noContent().build();
	}
}