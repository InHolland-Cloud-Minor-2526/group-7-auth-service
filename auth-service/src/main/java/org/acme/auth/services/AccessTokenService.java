package org.acme.auth.services;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.redis.client.RedisClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccessTokenService {

    @Inject
    RedisClient redis;
    
    public void storeAccessToken(Long userId, String accessToken, long ttlSeconds) {
        String key = "userId : " + userId;
        String hashedAccessToken = BcryptUtil.bcryptHash(accessToken);
        redis.setex(key, String.valueOf(ttlSeconds), hashedAccessToken);

    }

    // public String getAccessTokenHash(Long userId) {
    //     String key = ACCESS_TOKEN_PREFIX + userId;
    //     return redis.get(key).toString();
    // }
    // public void deleteAccessToken(Long userId) {
    //     redis.del(ACCESS_TOKEN_PREFIX + userId);
    // }
}
