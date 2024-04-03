package cos.peerna.controller.reply;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import cos.peerna.controller.BaseControllerTest;
import cos.peerna.controller.dto.ResponseDto;
import cos.peerna.controller.reply.request.RegisterWithGPTRequest;
import cos.peerna.controller.reply.request.RegisterWithPersonRequest;
import cos.peerna.controller.reply.response.RegisterWithGPTResponse;
import cos.peerna.controller.reply.response.RegisterWithPersonResponse;
import cos.peerna.controller.reply.response.ReplyListResponse;
import cos.peerna.controller.reply.response.ReplyResponse;
import cos.peerna.controller.reply.response.ReplyWithPageInfoResponse;
import cos.peerna.domain.reply.dto.result.ReplyResult;
import groovy.util.logging.Slf4j;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

@Slf4j
@DisplayName("ReplyController 테스트")
@WebMvcTest(ReplyController.class)
public class ReplyControllerTest extends BaseControllerTest {

    @MockBean
    private ReplyController replyController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("최신 답변 조회")
    public void findReplies() throws Exception {
        // given
        when(replyController.findReplies(any(), any()))
                .thenReturn(ResponseEntity.ok().body(ResponseDto.of(ReplyListResponse.of(
                        List.of(ReplyResult.builder()
                                .replyId(1L)
                                .historyId(1L)
                                .problemId(1L)
                                .likeCount(0L)
                                .question("question")
                                .answer("answer")
                                .exampleAnswer("exampleAnswer")
                                .userId(1L)
                                .userName("userName")
                                .userImage("userImage")
                                .alreadyLiked(false)
                                .build())))));

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/replies")
                .param("cursorId", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("data.replies[]").type(JsonFieldType.ARRAY).description("답변 리스트"),
                                fieldWithPath("data.replies[].history_id").type(NUMBER).description("히스토리 아이디"),
                                fieldWithPath("data.replies[].reply_id").type(NUMBER).description("답변 아이디"),
                                fieldWithPath("data.replies[].problem_id").type(NUMBER).description("문제 아이디"),
                                fieldWithPath("data.replies[].like_count").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("data.replies[].question").type(STRING).description("질문"),
                                fieldWithPath("data.replies[].answer").type(STRING).description("답변"),
                                fieldWithPath("data.replies[].example_answer").type(STRING).description("예시 답변"),
                                fieldWithPath("data.replies[].user_id").type(NUMBER).description("유저 아이디"),
                                fieldWithPath("data.replies[].user_name").type(STRING).description("유저 이름"),
                                fieldWithPath("data.replies[].user_image").type(STRING).description("유저 이미지"),
                                fieldWithPath("data.replies[].already_liked").type(BOOLEAN).description("이미 좋아요 했는지 여부")
                        )
                ));
    }

    @Test
    @DisplayName("문제별 답변 조회")
    public void findRepliesByProblem() throws Exception {
        // given
        when(replyController.getRepliesByProblem(any(), any()))
                .thenReturn(ResponseEntity.ok().body(ResponseDto.of(ReplyWithPageInfoResponse.builder()
                        .replies(List.of(ReplyResponse.builder()
                                .replyId(1L)
                                .historyId(1L)
                                .problemId(1L)
                                .likeCount(0L)
                                .question("question")
                                .answer("answer")
                                .exampleAnswer("exampleAnswer")
                                .userId(1L)
                                .userName("userName")
                                .userImage("userImage")
                                .alreadyLiked(false)
                                .build()))
                        .totalPages(1)
                        .hasNextPage(false)
                        .build())));

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/replies/problem")
                .param("problemId", "1")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("data.total_pages").type(NUMBER).description("총 페이지 수"),
                                fieldWithPath("data.has_next_page").type(BOOLEAN).description("다음 페이지 존재 여부"),
                                fieldWithPath("data.replies[]").type(JsonFieldType.ARRAY).description("답변 리스트"),
                                fieldWithPath("data.replies[].history_id").type(NUMBER).description("히스토리 아이디"),
                                fieldWithPath("data.replies[].reply_id").type(NUMBER).description("답변 아이디"),
                                fieldWithPath("data.replies[].problem_id").type(NUMBER).description("문제 아이디"),
                                fieldWithPath("data.replies[].like_count").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("data.replies[].question").type(STRING).description("질문"),
                                fieldWithPath("data.replies[].answer").type(STRING).description("답변"),
                                fieldWithPath("data.replies[].example_answer").type(STRING).description("예시 답변"),
                                fieldWithPath("data.replies[].user_id").type(NUMBER).description("유저 아이디"),
                                fieldWithPath("data.replies[].user_name").type(STRING).description("유저 이름"),
                                fieldWithPath("data.replies[].user_image").type(STRING).description("유저 이미지"),
                                fieldWithPath("data.replies[].already_liked").type(BOOLEAN).description("이미 좋아요 했는지 여부")
                        )
                ));
    }

    @Test
    @DisplayName("유저별 답변 조회")
    public void findRepliesByUser() throws Exception {
        // given
        when(replyController.findUserReplies(any(), any(), any()))
                .thenReturn(ResponseEntity.ok().body(ResponseDto.of(ReplyListResponse.of(
                        List.of(ReplyResult.builder()
                                .replyId(1L)
                                .historyId(1L)
                                .problemId(1L)
                                .likeCount(0L)
                                .question("question")
                                .answer("answer")
                                .exampleAnswer("exampleAnswer")
                                .userId(1L)
                                .userName("userName")
                                .userImage("userImage")
                                .alreadyLiked(false)
                                .build())))));

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/replies/user")
                        .param("userId", "1")
                        .param("cursorId", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("data.replies[]").type(JsonFieldType.ARRAY).description("답변 리스트"),
                                fieldWithPath("data.replies[].history_id").type(NUMBER).description("히스토리 아이디"),
                                fieldWithPath("data.replies[].reply_id").type(NUMBER).description("답변 아이디"),
                                fieldWithPath("data.replies[].problem_id").type(NUMBER).description("문제 아이디"),
                                fieldWithPath("data.replies[].like_count").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("data.replies[].question").type(STRING).description("질문"),
                                fieldWithPath("data.replies[].answer").type(STRING).description("답변"),
                                fieldWithPath("data.replies[].example_answer").type(STRING).description("예시 답변"),
                                fieldWithPath("data.replies[].user_id").type(NUMBER).description("유저 아이디"),
                                fieldWithPath("data.replies[].user_name").type(STRING).description("유저 이름"),
                                fieldWithPath("data.replies[].user_image").type(STRING).description("유저 이미지"),
                                fieldWithPath("data.replies[].already_liked").type(BOOLEAN).description("이미 좋아요 했는지 여부")
                        )
                ));
    }

    @Test
    @DisplayName("답변 등록-GPT와 함께")
    public void registerReplyWithGPT() throws Exception {
        // given
        RegisterWithGPTRequest request = new RegisterWithGPTRequest("testAnswer", 1L);
        when(replyController.registerReplyWithGPT(any(), any()))
                .thenReturn(ResponseEntity.created(null).body(
                        ResponseDto.of(new RegisterWithGPTResponse(1L))));

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/replies/gpt")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("problem_id").type(NUMBER).description("문제 아이디"),
                                fieldWithPath("answer").type(STRING).description("답변 내용")
                        ),
                        responseFields(
                                fieldWithPath("data.reply_id").type(NUMBER).description("생성된 답변 아이디")
                        )
                ));
    }

    @Test
    @DisplayName("답변 등록-사람과 함께")
    public void registerReplyWithHuman() throws Exception {
        // given
        RegisterWithPersonRequest request = new RegisterWithPersonRequest("testAnswer");
        when(replyController.registerReplyWithPerson(any(), any()))
                .thenReturn(ResponseEntity.created(null).body(
                        ResponseDto.of(new RegisterWithPersonResponse(1L))));

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/replies/person")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("answer").type(STRING).description("답변 내용")
                        ),
                        responseFields(
                                fieldWithPath("data.reply_id").type(NUMBER).description("생성된 답변 아이디")
                        )
                ));
    }
}
