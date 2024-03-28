package cos.peerna.domain.history.model;

import cos.peerna.domain.problem.model.Problem;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class History {
	@Id @GeneratedValue
	@Column(name = "history_id")
	private Long id;

	private LocalDate time;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "problem_id")
	private Problem problem;

	public static History of(Problem problem) {
		History history = new History();
		history.time = LocalDate.now();
		history.problem = problem;
		return history;
	}

	public static History of(LocalDate time) {
		History history = new History();
		history.time = time;
		return history;
	}

	public void updateProblem(Problem problem) {
		this.problem = problem;
	}
}
