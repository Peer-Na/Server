package cos.peerna.global.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
    @Target(ElementType.PARAMETER)
    이 어노테이션이 생성될 수 있는 위치를 지정
    PARAMETER 로 지정했으니 메소드의 파라미터로 선언된 객체에서만 사용할 수 있다.

    @Retention(RetentionPolicy.RUNTIME)
    Annotation 의 라이프 사이크를 지정
    SOURCE vs CLASS vs RUNTIME
    소스코드까지 | 바이트코드까지 | 런타임까지(그냥 안 사라진다)
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}
