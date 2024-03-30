package cos.peerna.domain.user.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class Interest implements Serializable {
    @Enumerated(EnumType.STRING)
    private Category priority1;
    @Enumerated(EnumType.STRING)
    private Category priority2;
    @Enumerated(EnumType.STRING)
    private Category priority3;

    protected Interest() {

    }

    public Interest(Category priority1, Category priority2, Category priority3) {
        this.priority1 = priority1;
        this.priority2 = priority2;
        this.priority3 = priority3;
    }
}
