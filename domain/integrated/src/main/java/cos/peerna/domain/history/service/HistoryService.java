package cos.peerna.domain.history.service;

import cos.peerna.domain.history.dto.result.DetailHistoryResult;
import cos.peerna.domain.history.dto.result.HistoryResult;
import cos.peerna.domain.history.model.History;
import cos.peerna.domain.history.repository.HistoryRepository;
import cos.peerna.domain.keyword.model.Keyword;
import cos.peerna.domain.keyword.repository.KeywordRepository;
import cos.peerna.domain.problem.model.Problem;
import cos.peerna.domain.reply.dto.result.ReplyResult;
import cos.peerna.domain.room.dto.ChatMessageSendDto;
import cos.peerna.domain.reply.model.Reply;
import cos.peerna.domain.reply.repository.ReplyRepository;
import cos.peerna.domain.room.model.Chat;
import cos.peerna.domain.room.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HistoryService {
    private final ReplyRepository replyRepository;
    private final HistoryRepository historyRepository;
    private final KeywordRepository keywordRepository;
    private final ChatRepository chatRepository;

    public List<HistoryResult> findUserHistory(Long userId, Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Reply> replyList = replyRepository.findRepliesByUserIdOrderByIdAsc(userId, cursorId, pageable);

        return replyList.stream().map(reply -> {
            History history = reply.getHistory();
            Problem problem = reply.getProblem();
            return HistoryResult.builder()
                    .historyId(history.getId())
                    .problemId(problem.getId())
                    .question(problem.getQuestion())
                    .category(problem.getCategory())
                    .time(history.getTime())
                    .build();
        }).collect(Collectors.toList());
    }

    public DetailHistoryResult findDetailHistory(Long historyId, Long userId) {
        History history = historyRepository.findByIdWithProblem(historyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "History Not Found"));
        Problem problem = history.getProblem();
        List<Reply> replyList = replyRepository.findRepliesWithUserByHistoryOrderByHistoryIdDesc(history);
        List<Keyword> keywordList = keywordRepository.findTop3KeywordsByProblemOrderByCountDesc(problem);
        List<ReplyResult> replyResultList = new ArrayList<>();
        for (Reply reply : replyList) {
            replyResultList.add(ReplyResult.builder()
                    .replyId(reply.getId())
                    .likeCount((long) reply.getLikes().size())
                    .answer(reply.getAnswer())
                    .userId(reply.getUser().getId())
                    .userName(reply.getUser().getName())
                    .userImage(reply.getUser().getImageUrl())
                    .alreadyLiked(reply.isLikedBy(userId))
                    .build());
        }

        List<Chat> chat = chatRepository.findAllByHistory(history);
        return DetailHistoryResult.builder()
                .question(problem.getQuestion())
                .exampleAnswer(problem.getAnswer())
                .time(history.getTime())
                .replies(replyResultList)
                .keywords(keywordList.stream().map(Keyword::getName).collect(Collectors.toList()))
                .chattings(chat.stream().map(ChatMessageSendDto::new).collect(Collectors.toList()))
                .build();
    }
}
