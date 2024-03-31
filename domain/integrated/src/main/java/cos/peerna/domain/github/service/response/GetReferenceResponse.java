package cos.peerna.domain.github.service.response;

public record GetReferenceResponse (
        String ref,
        GitObject object
) {}
