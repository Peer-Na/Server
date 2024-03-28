package cos.peerna.domain.gpt.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import cos.peerna.domain.gpt.dto.request.SendMessageRequest;
import cos.peerna.domain.gpt.event.ReviewReplyEvent;
import cos.peerna.domain.gpt.model.GPT;
import cos.peerna.domain.history.model.History;
import cos.peerna.domain.history.repository.HistoryRepository;
import cos.peerna.domain.reply.model.Reply;
import cos.peerna.domain.reply.repository.ReplyRepository;
import cos.peerna.domain.room.model.Chat;
import cos.peerna.domain.room.repository.ChatRepository;
import cos.peerna.global.common.service.RedisKeyGenerator;
import cos.peerna.global.security.dto.SessionUser;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GPTService {

    private final RedisKeyGenerator keyGenerator;
    private final ReplyRepository replyRepository;
    private final SimpMessagingTemplate template;
    private final RedisTemplate<String, ChatMessage> redisTemplate;
    private final ObjectMapper objectMapper;
    private final OpenAiService openAIService;
    private final ChatRepository chatRepository;
    private final HistoryRepository historyRepository;

    public void deleteConversation(Long userId) {
        redisTemplate.delete(keyGenerator.generate(ChatMessage.class, userId));
    }

    /*
    TODO: Async 로 변경
     */
    public void reviewReply(ReviewReplyEvent event) {
        /*
        TODO: 사용자의 권한에 따른 gpt 모델 선택
         */
        ChatMessage systemMessage = new ChatMessage("system", GPT.getConcept(event.question()));
        ChatMessage userMessage = new ChatMessage("user", event.answer());

        StringBuilder assistantMessageBuilder = new StringBuilder();
        openAIService.streamChatCompletion(ChatCompletionRequest.builder()
                        .model(GPT.getModel())
                        .messages(List.of(
                                systemMessage,
                                userMessage
                        ))
                        .build())
                .doOnError(throwable -> sendErrorMessage(event.userId()))
                .blockingForEach(chunk -> sendChatMessages(chunk, event.userId(), assistantMessageBuilder));
        ChatMessage assistantMessage = new ChatMessage("assistant", assistantMessageBuilder.toString());

        pushChatMessages(event.userId(), systemMessage, userMessage, assistantMessage);

        History history = historyRepository.findById(event.historyId())
                .orElseThrow(() -> new NotFoundException("history not found"));
        chatRepository.save(Chat.builder()
                .writerId(0L)
                .content(assistantMessageBuilder.toString())
                .history(history)
                .build());
    }

    /*
    TODO: Async 로 변경
    TODO: lastReply 를 찾는 방식에서 유저마다 gpt와의 대화를 저장하는 hashes생성
     lastReply 를 찾는 방식은 GPT와 학습과 사람과 학습을 병행하고 있을 때 버그가 날 수 있음
     chat 과 history가 연관관계가 있기에 chat 저장소를 MongoDB로 변경하고 수정
     */
    public void sendMessage(SessionUser user, SendMessageRequest request) {
        Reply lastReply = replyRepository.findFirstByUserIdOrderByIdDesc(user.getId())
                .orElseThrow(() -> new NotFoundException("reply not found"));

        List<ChatMessage> messages = getChatMessages(user.getId());
        ChatMessage userMessage = new ChatMessage("user", request.message());
        messages.add(userMessage);

        StringBuilder assistantMessageBuilder = new StringBuilder();
        openAIService.streamChatCompletion(ChatCompletionRequest.builder()
                        .model(GPT.getModel())
                        .messages(messages)
                        .build())
                .doOnError(throwable -> sendErrorMessage(user.getId()))
                .blockingForEach(chunk -> sendChatMessages(chunk, user.getId(), assistantMessageBuilder));
        ChatMessage assistantMessage = new ChatMessage("assistant", assistantMessageBuilder.toString());

        pushChatMessages(user.getId(), userMessage, assistantMessage);

        chatRepository.save(Chat.builder()
                .writerId(user.getId())
                .content(request.message())
                .history(lastReply.getHistory())
                .build());
        chatRepository.save(Chat.builder()
                .writerId(0L)
                .content(assistantMessageBuilder.toString())
                .history(lastReply.getHistory())
                .build());
    }

    private List<ChatMessage> getChatMessages(Long userId) {
        String key = keyGenerator.generate(ChatMessage.class, userId);
        List<ChatMessage> messageObjects = redisTemplate.opsForList().range(key, 0, -1);
        List<ChatMessage> messages = new ArrayList<>();
        if (messageObjects == null) {
            throw new NotFoundException("messageObjects is null");
        }
        for (Object messageObject : messageObjects) {
            ChatMessage chatMessage = objectMapper.convertValue(messageObject, ChatMessage.class);
            messages.add(chatMessage);
        }
        return messages;
    }

    private void pushChatMessages(Long userId, ChatMessage... message) {
        String key = keyGenerator.generate(ChatMessage.class, userId);
        for (ChatMessage chatMessage : message) {
            redisTemplate.opsForList().rightPush(key, chatMessage);
        }
    }

    private void sendChatMessages(ChatCompletionChunk chunk, Long userId, StringBuilder assistantMessageBuilder) {
        /*
        TODO: stream 이 끝나면, gpt 답변 전체를 저장
        TODO: gpt에게서 오는 chunk의 순서가 보장되지 않음
         */
        String message = chunk.getChoices().get(0).getMessage().getContent();
        if (message == null) {
            template.convertAndSend("/user/" + userId + "/gpt", GPT.getENDMessage());
            return;
        }
        template.convertAndSend("/user/" + userId + "/gpt", message);
        assistantMessageBuilder.append(message);
    }

    private void sendErrorMessage(Long userId) {
        template.convertAndSend("/user/" + userId + "/gpt", GPT.getErrorMessage());
    }
}
