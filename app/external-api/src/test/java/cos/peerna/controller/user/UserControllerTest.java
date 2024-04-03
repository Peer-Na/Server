package cos.peerna.controller.user;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.mockito.BDDMockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import cos.peerna.controller.BaseControllerTest;
import cos.peerna.controller.dto.ResponseDto;
import cos.peerna.controller.user.request.UserRegisterRequest;
import cos.peerna.controller.user.response.UpdateGithubRepoResponse;
import cos.peerna.domain.user.dto.UserProfile;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

@Slf4j
@DisplayName("UserController 테스트")
@WebMvcTest(UserController.class)
public class UserControllerTest extends BaseControllerTest {

    @MockBean
    private UserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("회원가입")
    public void signUp() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(2L, "testUser", "testPassword", "testEmail");

        when(userController.signUp(userRegisterRequest)).thenReturn(ResponseEntity.created(null)
                .body(ResponseDto.of(2L)));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterRequest)))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 명"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일")
                        ),
                        responseFields(
                                fieldWithPath("data").type(NUMBER).description("생성된 회원 ID")
                        )
                ));
    }

    @Test
    @DisplayName("회원탈퇴")
    public void signOut() throws Exception {
        when(userController.signOut(any())).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document());
    }

    @Test
    @DisplayName("회원정보 조회")
    public void me() throws Exception {
        ResponseDto<UserProfile> responseDto = ResponseDto.of(UserProfile.builder()
                        .userId(1L)
                        .name("testUser")
                        .email("testEmail")
                        .imageUrl("testImageUrl")
                        .build());
        when(userController.me(any())).thenReturn(ResponseEntity.ok().body(responseDto));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("data.user_id").type(NUMBER).description("회원 ID"),
                                fieldWithPath("data.name").type(STRING).description("회원 명"),
                                fieldWithPath("data.email").type(STRING).description("회원 이메일"),
                                fieldWithPath("data.image_url").type(STRING).description("회원 이미지 URL")
                        )
                ));
    }

    @Test
    @DisplayName("깃허브 레포 업데이트")
    public void updateGithubRepo() throws Exception {
        when(userController.updateGithubRepo(any(), any())).thenReturn(ResponseEntity.ok()
                .body(ResponseDto.of(new UpdateGithubRepoResponse("testRepoName"))));

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/users/github-repo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"github_repo\":\"testRepoName\"}"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("github_repo").type(JsonFieldType.STRING).description("깃허브 레포")
                        ),
                        responseFields(
                                fieldWithPath("data.github_repo").type(JsonFieldType.STRING).description("업데이트된 레포 이름")
                        )
                ));
    }
}
