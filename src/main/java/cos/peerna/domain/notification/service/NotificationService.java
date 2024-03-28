package cos.peerna.domain.notification.service;


import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import cos.peerna.domain.notification.dto.NotificationResponseDto;
import cos.peerna.domain.notification.dto.data.NotificationData;
import cos.peerna.domain.notification.model.Notification;
import cos.peerna.domain.notification.repository.NotificationRepository;
import cos.peerna.domain.user.dto.data.UserProfileData;
import cos.peerna.domain.user.model.User;
import cos.peerna.domain.user.repository.UserRepository;
import cos.peerna.global.security.dto.SessionUser;
import cos.peerna.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final RestTemplate restTemplate = new RestTemplate();

	public NotificationResponseDto getNotifications(SessionUser sessionUser) {
		User user = userRepository.findById(sessionUser.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));
		List<Notification> notificationList = notificationRepository.findAllByUser(user);

		List<NotificationData> list = notificationList.stream().map(notification -> NotificationData.builder()
				.notificationId(notification.getId())
				.type(notification.getType().toString())
				.answer(notification.getReply() == null ? null : notification.getReply().getAnswer())
				.sender(notification.getFollower() == null ? null : new UserProfileData(notification.getFollower()))
				.msg(notification.getMsg())
				.time(notification.getTime())
				.build()
		).collect(Collectors.toList());

		return NotificationResponseDto.builder()
				.notificationList(list)
				.build();
	}

	public void acceptNotification(SessionUser sessionUser, Long notificationId) {
		User user = userRepository.findById(sessionUser.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));
		Notification notification = notificationRepository.findNotificationById(notificationId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification Not Found"));

		if (!notification.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Not Matched");
		}

		Notification.acceptNotification(notification);

		if (Notification.isPRNotification(notification)) {
			String noticeId = notification.getId().toString();
			String question = notification.getReply().getProblem().getQuestion();
			String answer = notification.getReply().getAnswer();

			String url = "https://api.github.com/repos/";
			String repo = "backend-interview-question";

			forkRepository(sessionUser.getToken(), url + "ksundong/backend-interview-question/forks");
			createBranch(sessionUser, url, repo, noticeId);
			getContentAndPush(sessionUser, url + sessionUser.getLogin() + "/" + repo + "/", question, answer, noticeId);
			createPullReq(sessionUser, url + "ksundong/backend-interview-question/pulls",
					"PeerNA 자동 Pull-Request 입니다.", noticeId);
		}
		if (Notification.isFollowNotification(notification)) {
			userService.follow(sessionUser.getId(), notification.getFollower().getId());
		}
	}

	public void forkRepository(String token, String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/vnd.github.v3+json")));
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		if (response.getStatusCode() == HttpStatus.ACCEPTED) {
			log.debug("Fork Success");
		} else {
			log.debug("Fork Failed");
		}
	}

	public String getShaHash(String token, String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/vnd.github.v3+json")));
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		Gson gson = new Gson();
		String body = response.getBody();
		JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

		return jsonObject.get("object").getAsJsonObject().get("sha").getAsString();
	}

	public void createBranch(SessionUser sessionUser, String url, String repo, String noticeId) {
		String token = sessionUser.getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/vnd.github.v3+json");

		String newUrl = "https://api.github.com/repos/"+ sessionUser.getLogin() + "/"
				+ repo;

		String shaHash = getShaHash(token, newUrl + "/git/ref/heads/master");

		JSONObject body = new JSONObject();
		body.put("ref", "refs/heads/peerna" + noticeId);
		body.put("sha", shaHash);

		HttpEntity<String> requestEntity = new HttpEntity<>(body.toString(), headers);
		ResponseEntity<String> response = restTemplate.exchange(newUrl + "/git/refs", HttpMethod.POST, requestEntity, String.class);

		if (response.getStatusCode() == HttpStatus.ACCEPTED) {
			log.debug("Branch Created");
		} else {
			log.debug("Branch Creation Failed");
		}
	}

//	특정 브랜치에 대한 정보
//	https://api.github.com/repos/its-sky/maple_web/git/ref/heads/peerna
//	heads/[브랜치명]

//	readme의 sha값을 가져올 수 있음
//	https://api.github.com/repos/its-sky/maple_web/readme?ref="peerna"

//	https://api.github.com/repos/its-sky/maple_web/contents/README.md
	// -d '{"message":"commit","committer":{"name":"shin","email":"smc9919@naver.com"},"content":"bXkgdXBkYXRlZCBmaWxlIGNvbnRlbnRz",
	// "branch":"peerna","sha":"b23b7d39137334973c8425177041d3988ec82dee"}'

	public void getContentAndPush(SessionUser sessionUser, String url, String question, String answer, String noticeId) {
		String token = sessionUser.getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/vnd.github.v3+json")));
		headers.set("Authorization", "Bearer " + token);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url + "/readme")
				.queryParam("ref", "peerna" + noticeId);
		String newUrl = builder.toUriString();

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
				newUrl, HttpMethod.GET, entity, String.class);
		Gson gson = new Gson();
		String body = response.getBody();
		JsonObject responseJsonObject = gson.fromJson(body, JsonObject.class);

		// sha 추출
		String sha = responseJsonObject.get("sha").getAsString();

		// content 추출 및 Base64 디코딩
		String contentBase64 = responseJsonObject.get("content").getAsString();
		String content = new String(Base64.decodeBase64(contentBase64));

		String[] split = content.split("<details>|</details>");
		String result = "";
		String reserve = "";
		for (String s : split) {
			if (s.contains(question)) {
				result += "<details>\n" + s
						+ "  <p>[PeerNA 좋아요 답변] : " + answer + "</p>\n" + "</details>\n";
				reserve = s;
			} else {
				result += "<details>\n" + s + "\n</details>\n";
			}
		}

		String newContent = Base64.encodeBase64String(result.getBytes(StandardCharsets.UTF_8));

		updateContent(sessionUser, url, newContent, sha, noticeId);
	}

	public void updateContent(SessionUser sessionUser, String url, String content, String sha, String noticeId) {
		String token = sessionUser.getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/vnd.github.v3+json")));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);

		url += "/contents/README.md";

		JSONObject body = new JSONObject();
		body.put("message", "Updated By PeerNA and " + sessionUser.getLogin());
		body.put("content", content);
		body.put("sha", sha);
		JSONObject committer = new JSONObject();
		committer.put("name", sessionUser.getLogin());
		committer.put("email", sessionUser.getEmail());
		body.put("committer", committer);
		body.put("branch", "peerna" + noticeId);

		HttpEntity<String> requestEntity = new HttpEntity<>(body.toString(), headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
		if (response.getStatusCode() == HttpStatus.ACCEPTED) {
			log.debug("Content Updated");
		} else {
			log.debug("Content Update Failed");
		}
	}

	public void createPullReq(SessionUser sessionUser, String url, String title, String noticeId) {
		String token = sessionUser.getToken();
		String baseBranch = "master";
		String headBranch = sessionUser.getLogin() + ":peerna" + noticeId;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/vnd.github.v3+json")));
		headers.set("Authorization", "Bearer " + token);

		Map<String, String> body = new HashMap<>();
		body.put("title", title);
		body.put("body", "PeerNA 서비스의 자동 PR입니다.");
		body.put("head", headBranch);
		body.put("base", baseBranch);

		HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			log.debug("PR Created");
		} else {
			log.debug("PR Creation Failed");
		}
	}

	public void deleteNotification(User user, Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification Not Found"));

		if (!notification.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Not Matched");
		}
		notificationRepository.deleteById(notificationId);
	}

	public void deleteAllNotification(User user) {

		notificationRepository.deleteAllByUser(user);
	}
}
