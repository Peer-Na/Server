package cos.peerna.domain.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Career {
    OVER_3, OVER_1, UNDER_1;

    @JsonCreator
    public static Career from(String s) {
        return Career.valueOf(s.toUpperCase());
    }

}
