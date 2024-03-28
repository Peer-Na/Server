package cos.peerna.service;

import static org.junit.Assert.assertThrows;

import cos.peerna.domain.problem.dto.request.RegisterProblemRequest;
import cos.peerna.domain.problem.service.ProblemService;
import cos.peerna.domain.user.model.Category;
import cos.peerna.domain.problem.model.Problem;
import cos.peerna.domain.problem.repository.ProblemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@Transactional
class ProblemServiceTest {

	@Autowired
	ProblemService problemService;
	@Autowired ProblemRepository problemRepository;

	@Test
	@DisplayName("중복 문제 검증")
	void make() {
		// 문제 등록
		RegisterProblemRequest request = RegisterProblemRequest.of(
				"hello", "안녕", Category.OPERATING_SYSTEM);
		Problem problem = Problem.createProblem(
				request.question(), request.answer(), request.category()
		);
		problemRepository.save(problem);

		// 동일한 내용의 문제 등록 시도
		RegisterProblemRequest duplicateRequest = RegisterProblemRequest.of(
				"hello", "안녕", Category.OPERATING_SYSTEM);
		Problem duplicateProblem = Problem.createProblem(
				duplicateRequest.question(), duplicateRequest.answer(), duplicateRequest.category()
		);

		// 동일한 내용의 문제를 등록하려고 시도하면 예외가 발생해야 함
		assertThrows(ResponseStatusException.class, () -> problemRepository.save(duplicateProblem));
	}
}