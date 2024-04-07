package cos.peerna.controller.problem;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import cos.peerna.controller.BaseControllerTest;
import cos.peerna.controller.dto.ResponseDto;
import cos.peerna.controller.problem.response.AnswerAndKeywordResponse;
import cos.peerna.controller.problem.response.ProblemListResponse;
import cos.peerna.domain.problem.dto.result.ProblemResult;
import cos.peerna.domain.user.model.Category;
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
@DisplayName("ProblemController 테스트")
@WebMvcTest(ProblemController.class)
public class ProblemControllerTest extends BaseControllerTest {

    @MockBean
    private ProblemController problemController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("예시 답변 및 키워드 조회")
    public void getAnswerAndKeyword() throws Exception {
        // given
        Long problemId = 1L;
        when(problemController.getAnswerAndKeyword(problemId))
                .thenReturn(ResponseEntity.ok(ResponseDto.of(AnswerAndKeywordResponse.builder()
                        .answer("answer")
                        .keywords(List.of("keyword1", "keyword2"))
                        .build())));

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/problems/answer")
                .param("problemId", problemId.toString()))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("data.answer").type(STRING).description("답변"),
                                fieldWithPath("data.keywords[]").type(JsonFieldType.ARRAY).description("키워드 목록")
                        )
                ));
    }

    @Test
    @DisplayName("카테고리별 문제 조회")
    public void findProblemsByCategory() throws Exception {
        // given
        when(problemController.findProblemsByCategory(any(), any(), any()))
                .thenReturn(ResponseEntity.ok(ResponseDto.of(ProblemListResponse.builder()
                        .problems(List.of(ProblemResult.builder()
                                .problemId(1L)
                                .question("question")
                                .answer("answer")
                                .category(Category.OPERATING_SYSTEM)
                                .build()))
                        .build())));

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/problems/OPERATING_SYSTEM")
                .param("cursorId", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("data.problems[]").type(JsonFieldType.ARRAY).description("문제 목록"),
                                fieldWithPath("data.problems[].problemId").type(NUMBER).description("문제 ID"),
                                fieldWithPath("data.problems[].question").type(STRING).description("질문"),
                                fieldWithPath("data.problems[].answer").type(STRING).description("답변"),
                                fieldWithPath("data.problems[].category").type(STRING).description("카테고리")
                        )
                ));
    }
}
