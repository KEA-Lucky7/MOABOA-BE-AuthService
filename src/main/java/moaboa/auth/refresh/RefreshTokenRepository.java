package moaboa.auth.refresh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class RefreshTokenRepository {

    @Value("${jwt.secret.expiration}")
    private Long refreshExpirePeriod;
    private RedisTemplate redisTemplate;

    public RefreshTokenRepository(final RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String refreshToken, Long memberId) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken, memberId);
        redisTemplate.expire(refreshToken, refreshExpirePeriod, TimeUnit.SECONDS);
        log.info("refresh Token 저장");

    }

    public Optional<String> findByToken(final String refreshToken) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        Long memberId = valueOperations.get(refreshToken);

        if (Objects.isNull(memberId)) {
            return Optional.empty();
        }

        return Optional.of(refreshToken);
    }

    public Optional<String> findById(Long userId) {
        return Optional.ofNullable("");
    }
}
