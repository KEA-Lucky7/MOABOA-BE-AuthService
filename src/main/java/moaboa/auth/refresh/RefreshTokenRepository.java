package moaboa.auth.refresh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRepository {

    private RedisTemplate redisTemplate;
    @Value("${jwt.secret.expiration}")
    private Long refreshExpirePeriod;

    public RefreshTokenRepository(final RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(Long memberId) {
        String key = UUID.randomUUID().toString();
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, memberId);
        redisTemplate.expire(key, refreshExpirePeriod, TimeUnit.SECONDS);
    }

    public Optional<String> findById(final String refreshToken) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        Long memberId = valueOperations.get(refreshToken);

        if (Objects.isNull(memberId)) {
            return Optional.empty();
        }

        return Optional.of(refreshToken);
    }
}
