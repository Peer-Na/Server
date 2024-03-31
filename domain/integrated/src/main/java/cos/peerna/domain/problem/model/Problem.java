package cos.peerna.domain.problem.model;

import cos.peerna.domain.keyword.model.Keyword;
import cos.peerna.domain.user.model.Category;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "problem", indexes = {
    @Index(name = "idx_problem_category", columnList = "category")
})
public class Problem {

    @Id @GeneratedValue
    @Column(name = "problem_id")
    private Long id;

    @Column(length = 1000)
    private String question;

    @Column(length = 1000)
    private String answer;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Keyword> keywords = new ArrayList<>();

    public static Problem createProblem(String question, String answer, Category category) {
        Problem problem = new Problem();
        problem.question = question;
        problem.answer = answer;
        problem.category = category;

        return problem;
    }
}
