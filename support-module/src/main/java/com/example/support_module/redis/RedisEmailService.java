package com.example.support_module.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisEmailService {
    private final RedisTemplate<String , Object> redisTemplate;

    private static final String CONFIRMATION_CODE_PREFIX="blk:email_code:";

    private static final String RESET_PASSWORD_CODE = "blk:res_pass_code:";

    public long incrementEmailCount(String email) {
        String key = "email_limit:" + email;
        long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, 15, TimeUnit.MINUTES);
        }
        return count;
    }

    public void saveEmailConfirmation(String code){
        redisTemplate.opsForValue().set(
                CONFIRMATION_CODE_PREFIX + code,
                "active",
                15,
                TimeUnit.MINUTES
        );
    }

    public boolean isCodeAlive(String code){
        return Boolean.TRUE.equals(redisTemplate.hasKey(CONFIRMATION_CODE_PREFIX + code));
    }

    public void deleteConfirmationCode(String code){
        redisTemplate.delete(CONFIRMATION_CODE_PREFIX + code);
    }

    public void saveResetCode(String code){
        redisTemplate.opsForValue().set(
                RESET_PASSWORD_CODE + code,
                "active",
                15,
                TimeUnit.MINUTES
        );
    }

    public boolean isResetCodeAlive(String code){
        return Boolean.TRUE.equals(redisTemplate.hasKey(RESET_PASSWORD_CODE + code));
    }

    public void deleteResetCode(String code){
        redisTemplate.delete(RESET_PASSWORD_CODE + code);
    }
}
