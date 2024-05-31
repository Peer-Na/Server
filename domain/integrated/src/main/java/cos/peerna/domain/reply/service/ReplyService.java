package cos.peerna.domain.reply.service;

import cos.peerna.event.CommitReplyEvent;
import cos.peerna.domain.gpt.event.RegisterReplyEvent;
import cos.peerna.domain.history.model.History;
import cos.peerna.domain.history.repository.HistoryRepository;
import cos.peerna.domain.keyword.service.KeywordService;
import cos.peerna.domain.problem.model.Problem;
import cos.peerna.domain.problem.repository.ProblemRepository;
import cos.peerna.domain.reply.dto.command.RegisterWithGPTCommand;
import cos.peerna.domain.reply.dto.command.RegisterWithPersonCommand;
import cos.peerna.domain.reply.dto.command.UpdateReplyCommand;
import cos.peerna.domain.reply.dto.result.ReplyResult;
import cos.peerna.domain.reply.dto.result.ReplyWithPageInfoResult;
import cos.peerna.domain.reply.model.Likey;
import cos.peerna.domain.reply.model.Reply;
import cos.peerna.domain.reply.repository.LikeyRepository;
import cos.peerna.domain.reply.repository.ReplyRepository;
import cos.peerna.domain.room.model.Connect;
import cos.peerna.domain.room.model.Room;
import cos.peerna.domain.room.repository.ConnectRepository;
import cos.peerna.domain.room.repository.RoomRepository;
import cos.peerna.domain.user.model.User;
import cos.peerna.domain.user.repository.UserRepository;
import cos.peerna.global.security.dto.SessionUser;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private static final int PAGE_SIZE = 10;

    private final KafkaTemplate<String, RegisterReplyEvent> registerReplyEventKafkaTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final LikeyRepository likeyRepository;
    private final HistoryRepository historyRepository;
    private final KeywordService keywordService;
    private final RoomRepository roomRepository;
    private final ConnectRepository connectRepository;

    @Transactional
    public Long registerWithGPT(RegisterWithGPTCommand command, SessionUser sessionUser) {
        User user = userRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("No User Data"));
        Problem problem = problemRepository.findById(command.problemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem Not Found"));
        History history = historyRepository.save(History.of(problem));
        Reply reply = replyRepository.save(Reply.builderForRegister()
                .answer(command.answer())
                .history(history)
                .problem(problem)
                .user(user)
                .build());

        keywordService.analyze(command.answer(), command.problemId());

        if (user.getGithubRepo() != null) {
            applicationEventPublisher.publishEvent(CommitReplyEvent.builder()
                    .githubLogin(sessionUser.getLogin())
                    .githubToken(sessionUser.getToken())
                    .githubRepo(user.getGithubRepo())
                    .category(problem.getCategory().name())
                    .question(problem.getQuestion())
                    .answer(command.answer())
                    .build());
        }
        /*
        TODO: user.getGithubRepo() == null 일 때, 유저에게 GithubRepo를 등록하라는 메시지 전달
         */

        registerReplyEventKafkaTemplate.send("peerna-openai-register_reply", RegisterReplyEvent.builder()
                .historyId(history.getId())
                .userId(user.getId())
                .question(problem.getQuestion())
                .answer(command.answer())
                .build());
        /*
        TODO: User의 Authority에 따라 ReviewReplyEvent 발행 여부 결정
         */

        return reply.getId();
    }

    @Transactional
    public Long registerWithPerson(RegisterWithPersonCommand command, SessionUser sessionUser) {
        User user = userRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("No User Data"));
        Connect connect = connectRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Connect Not Found"));
        Room room = roomRepository.findById(connect.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room Not Found"));
        History history = historyRepository.findByIdWithProblem(room.getLastHistoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "History or Problem Not Found"));
        Problem problem = history.getProblem();
        Reply reply = replyRepository.save(Reply.builderForRegister()
                .answer(command.answer())
                .history(history)
                .problem(problem)
                .user(user)
                .build());

        /*
        TODO: 서비스가 한가로운 시간에 Batch로 처리 하는 시스템으로 변경
         */
        keywordService.analyze(command.answer(), problem.getId());

        if (user.getGithubRepo() != null) {
            applicationEventPublisher.publishEvent(CommitReplyEvent.builder()
                    .githubLogin(sessionUser.getLogin())
                    .githubToken(sessionUser.getToken())
                    .githubRepo(user.getGithubRepo())
                    .question(problem.getQuestion())
                    .category(problem.getCategory().name())
                    .answer(command.answer())
                    .build());
        }
        /*
        TODO: user.getGithubRepo() == null 일 때, 유저에게 GithubRepo를 등록하라는 메시지 전달
         */

        registerReplyEventKafkaTemplate.send("peerna-room-register_reply", RegisterReplyEvent.builder()
                .historyId(reply.getHistory().getId())
                .replyId(reply.getId())
                .likeCount(reply.getLikeCount())
                .problemId(reply.getProblem().getId())
                .question(reply.getProblem().getQuestion())
                .exampleAnswer(reply.getProblem().getAnswer())
                .answer(reply.getAnswer())
                .userId(reply.getUser().getId())
                .userName(reply.getUser().getName())
                .userImage(reply.getUser().getImageUrl())
                .build());

        return reply.getId();
    }

    @Transactional
    public void modify(UpdateReplyCommand command, SessionUser sessionUser) {
        User user = userRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("No User Data"));
        Problem problem = problemRepository.findById(command.problemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem Not Found"));
        Reply reply = replyRepository.findFirstByUserAndProblemOrderByIdDesc(user, problem)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reply Not Found"));

        reply.modifyAnswer(command.answer());
        keywordService.analyze(command.answer(), command.problemId());

        if (user.getGithubRepo() != null) {
            applicationEventPublisher.publishEvent(CommitReplyEvent.builder()
                    .githubLogin(sessionUser.getLogin())
                    .githubToken(sessionUser.getToken())
                    .githubRepo(user.getGithubRepo())
                    .question(problem.getQuestion())
                    .category(problem.getCategory().name())
                    .answer(command.answer())
                    .build());
        }
        /*
        TODO: user.getGithubRepo() == null 일 때, 유저에게 GithubRepo를 등록하라는 메시지 전달
         */
    }

    /*
    TODO: 쿼리 개선(?)
     */
    public List<ReplyResult> findReplies(Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("id").descending());

        List<Reply> replies;
        if (cursorId == 0) {
            replies = replyRepository.findRepliesByOrderByIdDesc(pageable);
        } else {
            replies = replyRepository.findRepliesByIdLessThanOrderByIdDesc(cursorId, pageable);
        }
        return replies.stream()
                .map(r -> ReplyResult.builder()
                        .historyId(r.getHistory().getId())
                        .replyId(r.getId())
                        .likeCount(r.getLikeCount())
                        .problemId(r.getProblem().getId())
                        .question(r.getProblem().getQuestion())
                        .answer(r.getAnswer())
                        .userId(r.getUser().getId())
                        .userName(r.getUser().getName())
                        .userImage(r.getUser().getImageUrl())
                        .build()
                ).collect(Collectors.toList());
    }

    public ReplyWithPageInfoResult findRepliesByProblem(Long problemId, int page) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem Not Found"));

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("likeCount").descending());
        Page<Reply> repliesPage = replyRepository.findRepliesByProblemOrderByLikeCountDesc(problem, pageable);

        List<Reply> replies = repliesPage.getContent();
        List<ReplyResult> replyResults = replies.stream()
                .map(r -> ReplyResult.builder()
                        .replyId(r.getId())
                        .likeCount(r.getLikeCount())
                        .problemId(r.getProblem().getId())
                        .question(r.getProblem().getQuestion())
                        .answer(r.getAnswer())
                        .userId(r.getUser().getId())
                        .userName(r.getUser().getName())
                        .userImage(r.getUser().getImageUrl())
                        .build()
                ).toList();
        return ReplyWithPageInfoResult.builder()
                .replies(replyResults)
                .totalPages(repliesPage.getTotalPages())
                .hasNextPage(repliesPage.hasNext())
                .build();
    }

    /*
    TODO: 오름차순으로 변경, (동적 쿼리 사용)
    */
    public List<ReplyResult> findUserReplies(Long userId, Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("id").ascending());
        List<Reply> replies = replyRepository.findRepliesByUserIdOrderByIdAsc(userId, cursorId, pageable);
        List<ReplyResult> replyResults = new ArrayList<>();
        for (Reply reply : replies) {
            replyResults.add(ReplyResult.builder()
                    .historyId(reply.getHistory().getId())
                    .replyId(reply.getId())
                    .likeCount(reply.getLikeCount())
                    .problemId(reply.getProblem().getId())
                    .question(reply.getProblem().getQuestion())
                    .answer(reply.getAnswer())
                    .userId(reply.getUser().getId())
                    .userName(reply.getUser().getName())
                    .userImage(reply.getUser().getImageUrl())
                    .alreadyLiked(reply.isLikedBy(userId))
                    .build());
        }
        return replyResults;
    }

    @Transactional
    public String addLikey(SessionUser sessionUser, Long replyId) {
        Reply reply = replyRepository.findByIdWithUserAndLike(replyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reply Not Found"));

        if (reply.isLikedBy(sessionUser.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already Recommended Reply");
        }

        User user = userRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));
        Likey newLikey = likeyRepository.save(Likey.builder()
                .user(user)
                .reply(reply)
                .build());

        reply.addLikey(newLikey);

        return String.valueOf(newLikey.getId());
    }

    @Transactional
    public void deleteLikey(SessionUser sessionUser, Long replyId) {
        Reply reply = replyRepository.findByIdWithUserAndLike(replyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reply Not Found"));

        if (!reply.isLikedBy(sessionUser.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Likey Not Found");
        }

        reply.dislikeReply(sessionUser.getId());
        User user = userRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));

        likeyRepository.findLikeyByUserAndReply(user, reply).ifPresent(likeyRepository::delete);
    }
}
