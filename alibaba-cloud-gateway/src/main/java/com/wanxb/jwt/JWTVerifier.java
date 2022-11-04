package com.wanxb.jwt;

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wanxb.config.DcCache;
import com.wanxb.utils.CipherUtils;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;

import java.time.Duration;
import java.util.Base64;

/**
 * <p>
 *  jwt 验证
 * </p>
 *
 * @author wanxinabo
 * @date 2021/10/8
 */
public class JWTVerifier {
    private final JWTToken token;

    private final DcCache dcCache;

    private static final Cache<JWTToken, byte[]> VERIFIED_TOKEN_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(30))
            .maximumSize(100_000)
            .build();

    public static final byte[] MEMORIZED_TOKEN = new byte[0];

    public JWTVerifier(JWTToken token, DcCache dcCache) {
        this.token = token;
        this.dcCache = dcCache;
    }

    public boolean verify() {
        if (token == null) return false;
        if (token.getPayload() == null) return false;
        // 独立部署的标识 暂时无
        // if (Strings.isNullOrEmpty(token.getPayload().getDc())) return false;
        if (token.getPayload().isExpired()) {
            clearMemo();
            return false;
        }
        if (verifiedToken()) return true;
        if (rsaVerify()) {
            memoToken();
            return true;
        }
        return false;
    }

    private void clearMemo() {
        if (VERIFIED_TOKEN_CACHE.getIfPresent(token) == null) return;
        VERIFIED_TOKEN_CACHE.invalidate(token);
    }

    private boolean verifiedToken() {
        return VERIFIED_TOKEN_CACHE.getIfPresent(token) != null;
    }

    private void memoToken() {
        VERIFIED_TOKEN_CACHE.put(token, MEMORIZED_TOKEN);
    }

    private boolean rsaVerify() {
        RSADigestSigner signer = new RSADigestSigner(new SHA256Digest());
        signer.init(false, dcCache.getPubKey0());
        signer.update(token.getVerifyContent(), 0 , token.getVerifyContent().length);
        return signer.verifySignature(token.getSig());
    }
}
