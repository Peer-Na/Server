package cos.peerna.controller.reply;

import cos.peerna.controller.dto.ResponseDto;
import cos.peerna.controller.reply.request.RegisterWithGPTRequest;
import cos.peerna.controller.reply.request.RegisterWithPersonRequest;
import cos.peerna.controller.reply.request.UpdateReplyRequest;
import cos.peerna.controller.reply.response.RegisterWithGPTResponse;
import cos.peerna.controller.reply.response.RegisterWithPersonResponse;
import cos.peerna.controller.reply.response.ReplyListResponse;
import cos.peerna.controller.reply.response.ReplyWithPageInfoResponse;
import cos.peerna.domain.reply.service.ReplyService;
import cos.peerna.global.security.LoginUser;
import cos.peerna.global.security.dto.SessionUser;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/replies")
public class ReplyController {

    private final ReplyService replyService;

    @GetMapping
    public ResponseEntity<ResponseDto<ReplyListResponse>> findReplies(
            @RequestParam(required = false, defaultValue = "0") Long cursorId,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ResponseDto.of(
                ReplyListResponse.of(replyService.findReplies(cursorId, size))));
    }

    @GetMapping("/problem")
    public ResponseEntity<ResponseDto<ReplyWithPageInfoResponse>> getRepliesByProblem(
            @RequestParam Long problemId,
            @RequestParam(required = false, defaultValue = "0") Integer page) {
        return ResponseEntity.ok(ResponseDto.of(
                ReplyWithPageInfoResponse.of(replyService.findRepliesByProblem(problemId, page))));
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseDto<ReplyListResponse>> findUserReplies(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "0") Long cursorId,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ResponseDto.of(
                ReplyListResponse.of(replyService.findUserReplies(userId, cursorId, size))));
    }

    @PostMapping("/gpt")
    public ResponseEntity<ResponseDto<RegisterWithGPTResponse>> registerReplyWithGPT(@LoginUser SessionUser user,
                                                                                     @RequestBody RegisterWithGPTRequest request) {
        Long replyId = replyService.registerWithGPT(request.toServiceDto(), user);
        return ResponseEntity.created(
                URI.create(String.valueOf(replyId))).body(ResponseDto.of(new RegisterWithGPTResponse(replyId)));
    }

    @PostMapping("/person")
    public ResponseEntity<ResponseDto<RegisterWithPersonResponse>> registerReplyWithPerson(@LoginUser SessionUser user,
                                                                                           @RequestBody RegisterWithPersonRequest request) {
        Long replyId = replyService.registerWithPerson(request.toServiceDto(), user);
        return ResponseEntity.created(
                URI.create(String.valueOf(replyId))).body(ResponseDto.of(new RegisterWithPersonResponse(replyId)));
    }

    @PatchMapping
    public ResponseEntity<Void> modifyReply(@LoginUser SessionUser user, @RequestBody UpdateReplyRequest request) {
        replyService.modify(request.toServiceDto(), user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/likey")
    public ResponseEntity<Void> addLikey(@LoginUser SessionUser user, @RequestParam Long replyId) {
        String likeyId = replyService.addLikey(user, replyId);
        return ResponseEntity.created(URI.create(likeyId)).build();
    }

    @DeleteMapping("/likey")
    public ResponseEntity<Void> deleteLikey(@LoginUser SessionUser user, @RequestParam Long replyId) {
        replyService.deleteLikey(user, replyId);
        return ResponseEntity.noContent().build();
    }
}