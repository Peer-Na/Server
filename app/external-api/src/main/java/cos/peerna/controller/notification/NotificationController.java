package cos.peerna.controller.notification;//package cos.peerna.domain.notification.controller;
//
//import cos.peerna.domain.notification.dto.NotificationResponseDto;
//import cos.peerna.domain.notification.service.NotificationService;
//import cos.peerna.domain.user.model.User;
//import cos.peerna.domain.user.repository.UserRepository;
//import cos.peerna.global.security.LoginUser;
//import cos.peerna.global.security.dto.SessionUser;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/api/notification")
//public class NotificationController {
//
//    private final NotificationService notificationService;
//	private final UserRepository userRepository;
//
//    @GetMapping
//    public ResponseEntity<NotificationResponseDto> getNotifications(@LoginUser SessionUser user) {
//        return ResponseEntity.ok(notificationService.getNotifications(user));
//    }
//
//    @DeleteMapping
//    public ResponseEntity<String> deleteNotification(@LoginUser SessionUser sessionUser, @RequestParam Long notificationId) {
//		User user = userRepository.findById(sessionUser.getId())
//				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저가 없습니다."));
//        if (notificationId == -1)
//            notificationService.deleteAllNotification(user);
//        else
//            notificationService.deleteNotification(user, notificationId);
//
//        return ResponseEntity.noContent().build();
//    }
//}
