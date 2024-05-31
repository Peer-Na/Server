package cos.peerna.service.response;

public record GetReferenceResponse (
        String ref,
        GitObject object
) {}
