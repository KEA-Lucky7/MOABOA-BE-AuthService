package moaboa.auth.token.refresh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class RefreshTokenRepository {

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpirePeriod;
    private RedisTemplate redisTemplate;

    public RefreshTokenRepository(final RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(final String refreshToken, final Long memberId) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        log.info("refresh Token 저장");
        valueOperations.set(refreshToken, String.valueOf(memberId));
        redisTemplate.expire(refreshToken, refreshExpirePeriod, TimeUnit.SECONDS);
    }

    public Optional<String> findMemberIdByToken(final String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return Optional.ofNullable(valueOperations.get(refreshToken));
    }
}
