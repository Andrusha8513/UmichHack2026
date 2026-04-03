package com.example.support_module.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisJwtService {

    private final RedisTemplate<String , Object> redisTemplate;

    private static final String TOKEN_BLACK_LIST = "blk:token:";

    private static final String USER_BLOCKLIST_PREFIX ="blk:user:";

    public void saveTokenToBlackList(String token , long ttlMillis){
        if(ttlMillis > 0){
            redisTemplate.opsForValue().set(
                    TOKEN_BLACK_LIST + token,
                    "true",
                    ttlMillis,
                    TimeUnit.MILLISECONDS
            );
        }
    }


    public void  blockUserId(Long userId){
        redisTemplate.opsForValue().set(
                USER_BLOCKLIST_PREFIX + userId,
                "banned",
                20,
                TimeUnit.MINUTES
        );
    }



    public boolean isTokenBlacklisted(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_BLACK_LIST + token));
    }

    public boolean isUserBlocked(Long userId){
        return Boolean.TRUE.equals(redisTemplate.hasKey(USER_BLOCKLIST_PREFIX + userId));
    }

    //для супер быстрого разбана крч
    public void unblockUserId(Long userId){
        redisTemplate.delete(USER_BLOCKLIST_PREFIX + userId);
    }


}



