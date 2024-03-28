package cos.peerna.domain.reply.controller;

import cos.peerna.domain.reply.dto.request.RegisterWithGPTRequest;
import cos.peerna.domain.reply.dto.request.RegisterWithPersonRequest;
import cos.peerna.domain.reply.dto.request.UpdateReplyRequest;
import cos.peerna.domain.reply.dto.response.ReplyResponse;
import cos.peerna.domain.reply.dto.response.ReplyWithPageInfoResponse;
import cos.peerna.domain.reply.service.ReplyService;
import cos.peerna.global.security.LoginUser;
import cos.peerna.global.security.dto.SessionUser;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reply")
public class ReplyController {

	private final ReplyService replyService;

	@GetMapping
	public ResponseEntity<List<ReplyResponse>> findReplies(
			@RequestParam(required = false, defaultValue = "0") Long cursorId,
			@RequestParam(required = false, defaultValue = "10") int size) {
		return ResponseEntity.ok(replyService.findReplies(cursorId, size));
	}

	@GetMapping("/problem")
	public ResponseEntity<ReplyWithPageInfoResponse> getRepliesByProblem(
			@RequestParam Long problemId,
			@RequestParam @Nullable int page) {
		return ResponseEntity.ok(replyService.findRepliesByProblem(problemId, page));
	}

	@GetMapping("/user")
    public ResponseEntity<List<ReplyResponse>> findUserReplies(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "0") Long cursorId,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(replyService.findUserReplies(userId, cursorId, size));
    }

	@PostMapping("/gpt")
	public ResponseEntity<Void> registerReplyWithGPT(@LoginUser SessionUser user,
											  @RequestBody RegisterWithGPTRequest request) {
		String replyId = replyService.registerWithGPT(request, user);
		return ResponseEntity.created(URI.create(replyId)).build();
	}

	@PostMapping("/person")
	public ResponseEntity<Void> registerReplyWithPerson(@LoginUser SessionUser user,
														@RequestBody RegisterWithPersonRequest request) {
		String replyId = replyService.registerWithPerson(request, user);
		return ResponseEntity.created(URI.create(replyId)).build();
	}

	@PatchMapping
	public ResponseEntity<Void> modifyReply(@LoginUser SessionUser user, @RequestBody UpdateReplyRequest request) {
		replyService.modify(request, user);
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