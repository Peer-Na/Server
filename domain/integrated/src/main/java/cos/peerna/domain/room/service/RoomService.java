package cos.peerna.domain.room.service;

import cos.peerna.domain.room.model.Connect;
import cos.peerna.domain.room.model.Room;
import cos.peerna.domain.room.repository.ConnectRepository;
import cos.peerna.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ConnectRepository connectRepository;

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
    TODO: 특정 방을 구독할 때, 해당 방의 유저인지 확인하는 로직 추가
     */
    public boolean isConnectedUser(Integer roomId, Long userId) {
        Connect connect = connectRepository.findById(userId)
            .orElse(null);
        return connect != null && connect.getRoomId().equals(roomId);
    }

}
