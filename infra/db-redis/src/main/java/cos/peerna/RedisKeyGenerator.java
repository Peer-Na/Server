package cos.peerna;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyGenerator {
    private final static String KEY_PREFIX = "peerna";
    public String generate(Class<?> target, Number id) {
        return String.format("%s:%s:%s", KEY_PREFIX, target.getSimpleName().toLowerCase(), id.toString());
    }
}
