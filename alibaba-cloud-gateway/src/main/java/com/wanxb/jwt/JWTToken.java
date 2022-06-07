package com.wanxb.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.util.Base64;

import static com.google.common.base.Preconditions.checkState;

/**
 * <p>
 *  自定义 jwt token解析类
 *  没有特殊要求推荐使用官网的 jar 包
 * </p>
 *
 * @author wanxinabo
 * @date 2021/10/8
 */
public class JWTToken {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    /**
     * jwt 的 payload
     */
    private final byte[] verifyContent;

    /**
     * rs256 密钥
     */
    private final byte[] sig;

    private AuthPayload payload;

    public JWTToken(byte[] verifyContent, byte[] sig) {
        this.verifyContent = verifyContent;
        Base64.Decoder decoder = Base64.getUrlDecoder();
        this.sig = decoder.decode(sig);
        checkState(this.sig.length == 256, "only support RS256 alg, sig must be size 256");
        int dotIndex = ArrayUtils.indexOf(verifyContent, (byte) '.');
        try {
            JsonNode jsonNode = JSON_MAPPER.readTree(decoder.decode(ArrayUtils.subarray(verifyContent, 0, dotIndex)));
            checkState("RS256".equals(jsonNode.get("alg").textValue()), "only support RS256 alg");
            JsonNode jsonPayload = JSON_MAPPER.readTree(decoder.decode(ArrayUtils.subarray(verifyContent, dotIndex + 1, verifyContent.length)));
            payload = AuthPayload.fromJsonNode(jsonPayload);
        } catch (IOException e) {
            throw new RuntimeException("parse json error. not a valid jwt token", e);
        }
    }

    public byte[] getVerifyContent() {
        return verifyContent;
    }

    public byte[] getSig() {
        return sig;
    }

    public AuthPayload getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        JWTToken jwtToken = (JWTToken) o;

        return new EqualsBuilder()
                .append(verifyContent, jwtToken.verifyContent)
                .append(sig, jwtToken.sig)
                .append(payload, jwtToken.payload)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(verifyContent)
                .append(sig)
                .append(payload)
                .toHashCode();
    }
}
