package cos.peerna.controller.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

public record ResponseDto<T>(@JsonInclude(JsonInclude.Include.NON_NULL) T data) {

    public static <T> ResponseDto<T> of(T data) {
        return new ResponseDto<>(data);
    }
}
