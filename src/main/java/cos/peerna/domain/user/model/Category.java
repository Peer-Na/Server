package cos.peerna.domain.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    DATA_STRUCTURE_ALGORITHM,
    OPERATING_SYSTEM,
    NETWORK,
    DATABASE,
    SECURITY,
    PERSONALITY;

    @JsonCreator
    public static Category from(String s) {
        return Category.valueOf(s.toUpperCase());
    }

}
