package cos.peerna.domain.problem.dto.response;

import java.util.List;

public record KeywordResponse(
        List<String> keywords
) {
    public static KeywordResponse of(List<String> keywords) {
        return new KeywordResponse(keywords);
    }

    @Override
    public String toString() {
        return "KeywordResponse{" +
                "keywords=" + keywords
                + '}';
    }
}
