package com.wanxb.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wanxb.utils.CipherUtils;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

/**
 * <p>
 *
 * </p>
 *
 * @author wanxinabo
 * @date 2021/10/9
 */
@Component
public class DcCache implements InitializingBean {

    private final StringRedisTemplate stringRedisTemplate;

    public DcCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private final LoadingCache PUBLIC_KEY_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(30))
            .build(CacheLoader.from(this::obtainPublicKey));

    private RSAKeyParameters obtainPublicKey(String dc) {
        String pubKey = stringRedisTemplate.opsForValue().get("rsa:privateKey");
        try {
            return CipherUtils.readRSAPublicKeyInPemFormat(pubKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RSAKeyParameters getPubKey0() {
        try {
            return (RSAKeyParameters) CipherUtils.readRsaAsymmetricKeyPairInPemFormat(stringRedisTemplate.opsForValue().get("rsa:privateKey")).getPublic();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
