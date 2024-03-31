package cos.peerna.domain.room.service;

import cos.peerna.domain.history.model.History;
import cos.peerna.domain.history.repository.HistoryRepository;
import cos.peerna.domain.problem.model.Problem;
import cos.peerna.domain.problem.repository.ProblemRepository;
import cos.peerna.domain.room.dto.response.ChangeProblemResponse;
import cos.peerna.domain.room.event.CreateRoomEvent;
import cos.peerna.domain.room.model.Chat;
import cos.peerna.domain.room.model.Connect;
import cos.peerna.domain.room.model.Room;
import cos.peerna.domain.room.repository.ChatRepository;
import cos.peerna.domain.room.repository.ConnectRepository;
import cos.peerna.domain.room.repository.RoomRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final SimpMessagingTemplate template;
    private final RoomRepository roomRepository;
    private final ConnectRepository connectRepository;
    private final HistoryRepository historyRepository;
    private final ChatRepository chatRepository;
    private final ProblemRepository problemRepository;

    public void createRoom(CreateRoomEvent event) {
        List<Long> connectedUserIds = new ArrayList<>(event.userScore().keySet());
        event.userScore().entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> {
                    Long userId = entry.getKey();
                    connectedUserIds.add(userId);
                });

        History history = historyRepository.save(History.of(LocalDate.now()));
        Room room = roomRepository.save(Room.builder()
                        .category(event.category())
                        .historyId(history.getId())
                        .build());

        for (Long userId : connectedUserIds) {
            /*
            TODO: 더 효율적이고 보안적으로 훌륭한 방법 찾기 (HttpSession 을 시도했으나 실패)
             */
            log.debug("Connect Save: roomId={}, userId={}", room.getId(), userId);
            connectRepository.save(Connect.of(userId, room.getId()));
            template.convertAndSend("/user/" + userId + "/match", room.getId());
        }
    }

    /*
    TODO: 특정 방을 구독할 때, 해당 방의 유저인지 확인하는 로직 추가
     */
    public boolean isConnectedUser(Integer roomId, Long userId) {
        Connect connect = connectRepository.findById(userId)
                .orElse(null);
        return connect != null && connect.getRoomId().equals(roomId);
    }

    public Integer findConnectedRoomId(Long userId) {
        Connect connect = connectRepository.findById(userId)
                .orElse(null);
        return connect == null ? null : connect.getRoomId();
    }

    public Room findById(Integer roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
    }

    /*
    TODO: 학습이 끝날 때까지 채팅 캐시 후 한번에 저장
     */
    public void saveChat(Integer roomId, Long userId, String message) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        History history = historyRepository.findById(room.getLastHistoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "History not found"));

        chatRepository.save(Chat.builder()
                .writerId(userId)
                .content(message)
                .history(history)
                .build());
    }

    public ChangeProblemResponse changeProblem(Integer roomId, String userName, Long problemId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        History history = historyRepository.findById(room.getLastHistoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "History not found"));
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found"));

        history.updateProblem(problem);
        historyRepository.save(history);

        return ChangeProblemResponse.of(problem.getId(), problem.getQuestion(),
                String.format("%s님이 문제를 변경했습니다.", userName));
    }

    public void disconnect(Long userId, String userName) {
        Connect connect = connectRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Connect not found"));
        connectRepository.delete(connect);
        template.convertAndSend("/room/" + connect.getRoomId(),
                String.format("System:\n %s님이 퇴장하셨습니다.", userName));
    }
}
