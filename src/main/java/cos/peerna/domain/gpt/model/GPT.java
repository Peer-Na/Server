package cos.peerna.domain.gpt.model;

public class GPT {

    private static final String MODEL_GPT_3_5_TURBO = "gpt-3.5-turbo";
    private static final String MODEL_GPT_4 = "gpt-4";

    public static String getModel() {
        return MODEL_GPT_3_5_TURBO;
    }

    public static String getConcept(String question) {
        return String.format("너는 소프트웨어 기업의 면접관이고 사용자에게 %s 라는 질문을 했어. "
                + "사용자의 답변을 듣고 나서 답변이 부족하다면 피드백을 주고 괜찮은 수준이라면 꼬리질문을 줘", question);
    }

    public static String getENDMessage() {
        return "\n-----END MESSAGE-----\n";
    }

    public static String getErrorMessage() {
        return "GPT 서비스가 중단되었습니다. 관리자에게 문의하세요.";
    }
}
