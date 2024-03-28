package cos.peerna.global.security;

import cos.peerna.global.security.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;

    /**
     * @LoginUser 를 사용하기 위한 환경 구성
     * LoginUserArgumentResolver 가 스프링에서 인식될 수 있도록
     * WebMvcConfigurer의 resolvers에 추가함
     *
     * supportsParameter()
     * 컨트롤러 메서드의 특정 파라미터를 지원하는지 판단합니다.
     * 파라미터에 @LoginUser 어노테이션이 붙어 있고,
     * 파라미터 클래스 타입이 SessionUser.class 인 경우 true를 반환

     * resolveParameter()
     * 파라미터에 전달할 객체를 생성합니다.
     * 세션에서 객체를 가져옵니다.
     */

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());

        return isLoginUserAnnotation && isUserClass;
//        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        return httpSession.getAttribute("user");
    }
}
