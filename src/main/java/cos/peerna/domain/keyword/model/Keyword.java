package cos.peerna.domain.keyword.model;

import cos.peerna.domain.problem.model.Problem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Entity
@Table(name = "keyword", indexes = {
		@Index(name = "idx_keyword_problem", columnList = "problem_id"),
		@Index(name = "idx_keyword_name_problem", columnList = "name, problem_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword {

	@GeneratedValue
	@Id @Column(name = "keyword_id", nullable = false)
	private Long id;

	@Column(name = "name", nullable = false)
    private String name;

    @NotNull @Column(name = "count", nullable = false)
    private Long count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

	@Builder
	public Keyword(String name, Long count, Problem problem) {
		this.name = name;
		this.count = count;
		this.problem = problem;
	}

	public static Keyword createKeyword(String kwd, Problem problem) {
		Keyword keyword = new Keyword();
		keyword.name = kwd;
		keyword.count = 1L;
		keyword.problem = problem;

		return keyword;
	}

	public static Keyword updateKeyword(Keyword keyword) {
		keyword.count += 1L;

		return keyword;
	}
}
