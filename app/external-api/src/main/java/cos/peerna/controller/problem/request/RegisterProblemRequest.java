package cos.peerna.controller.problem.request;


import cos.peerna.domain.user.model.Category;

public record RegisterProblemRequest(
        String question,
        String answer,
        Category category
) {
    @Override
    public String toString() {
        return "RegisterProblemRequest{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", category=" + category +
                '}';
    }

    public static RegisterProblemRequest of(String question, String answer, Category category) {
        return new RegisterProblemRequest(question, answer, category);
    }
}
