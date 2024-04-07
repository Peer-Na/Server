package cos.peerna.controller.histories;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cos.peerna.controller.BaseControllerTest;
import cos.peerna.controller.dto.ResponseDto;
import cos.peerna.controller.history.HistoryController;
import cos.peerna.controller.history.response.DetailHistoryResponse;
import cos.peerna.controller.history.response.HistoryListResponse;
import cos.peerna.controller.history.response.HistoryResponse;
import cos.peerna.controller.reply.response.ReplyResponse;
import cos.peerna.domain.room.dto.ChatMessageSendDto;
import cos.peerna.domain.user.model.Category;
import groovy.util.logging.Slf4j;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

@Slf4j
@DisplayName("HistoryController 테스트")
@WebMvcTest(HistoryController.class)
public class HistoryControllerTest extends BaseControllerTest {

    @MockBean
    private HistoryController historyController;

    @Test
    @DisplayName("유저 학습이력 조회")
    public void findUserHistory() throws Exception {
        // given
        Long userId = 1L;
        Long cursorId = 0L;
        int size = 10;
        when(historyController.findUserHistory(userId, cursorId, size))
                .thenReturn(ResponseEntity.ok(ResponseDto.of(
                        HistoryListResponse.builder()
                                .histories(List.of(HistoryResponse.builder()
                                        .historyId(1L)
                                        .problemId(1L)
                                        .question("question")
                                        .category(Category.OPERATING_SYSTEM)
                                        .time(LocalDate.now())
                                        .build()))
                                .build()
                )));

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/histories")
                .param("userId", userId.toString())
                .param("cursorId", cursorId.toString())
                .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("data.histories").type(JsonFieldType.ARRAY).description("학습이력 목록"),
                                fieldWithPath("data.histories[].history_id").type(NUMBER).description("학습이력 ID"),
                                fieldWithPath("data.histories[].problem_id").type(NUMBER).description("문제 ID"),
                                fieldWithPath("data.histories[].question").type(STRING).description("문제"),
                                fieldWithPath("data.histories[].category").type(STRING).description("카테고리"),
                                fieldWithPath("data.histories[].time").type(STRING).description("학습 시간")
                        )
                ));
    }

    @Test
    @DisplayName("유저 학습이력 상세 조회")
    public void findDetailHistory() throws Exception {
        // given
        when(historyController.findDetailHistory(any(), any()))
                .thenReturn(ResponseEntity.ok(ResponseDto.of(
                        DetailHistoryResponse.builder()
                                .date(LocalDate.of(2024, 1, 1))
                                .replies(List.of(
                                        ReplyResponse.builder()
                                                .replyId(1L)
                                                .historyId(1L)
                                                .problemId(1L)
                                                .userId(1L)
                                                .userImage("userImage")
                                                .userName("userName")
                                                .question("question")
                                                .answer("answer")
                                                .alreadyLiked(false)
                                                .exampleAnswer("exampleAnswer")
                                                .likeCount(7L)
                                                .build()
                                ))
                                .keywords(List.of(
                                        "keyword1",
                                        "keyword2",
                                        "keyword3"
                                ))
                                .chattings(List.of(
                                        ChatMessageSendDto.builder()
                                                .writerId(1L)
                                                .message("message send by 1 to 2")
                                                .time(LocalTime.now())
                                                .build(),
                                        ChatMessageSendDto.builder()
                                                .writerId(2L)
                                                .message("message send by 2 to 1")
                                                .time(LocalTime.now())
                                                .build()
                                ))
                                .build()
                )));

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/histories/detail")
                .param("historyId", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("data.date").type(STRING).description("학습 시간"),
                                fieldWithPath("data.replies[]").type(JsonFieldType.ARRAY).description("답변 목록"),
                                fieldWithPath("data.keywords[]").type(JsonFieldType.ARRAY).description("키워드 목록"),
                                fieldWithPath("data.chattings[]").type(JsonFieldType.ARRAY).description("채팅 목록"),
                                fieldWithPath("data.replies[].reply_id").type(NUMBER).description("답변 ID"),
                                fieldWithPath("data.replies[].history_id").type(NUMBER).description("학습이력 ID"),
                                fieldWithPath("data.replies[].problem_id").type(NUMBER).description("문제 ID"),
                                fieldWithPath("data.replies[].user_id").type(NUMBER).description("유저 ID"),
                                fieldWithPath("data.replies[].user_image").type(STRING).description("유저 이미지"),
                                fieldWithPath("data.replies[].user_name").type(STRING).description("유저 이름"),
                                fieldWithPath("data.replies[].question").type(STRING).description("질문"),
                                fieldWithPath("data.replies[].answer").type(STRING).description("답변"),
                                fieldWithPath("data.replies[].already_liked").type(JsonFieldType.BOOLEAN).description("이미 좋아요를 눌렀는지 여부"),
                                fieldWithPath("data.replies[].example_answer").type(STRING).description("예시 답변"),
                                fieldWithPath("data.replies[].like_count").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("data.chattings[].writer_id").type(NUMBER).description("작성자 ID"),
                                fieldWithPath("data.chattings[].message").type(STRING).description("메시지"),
                                fieldWithPath("data.chattings[].time").type(STRING).description("시간")
                        )
                ));
    }
}
