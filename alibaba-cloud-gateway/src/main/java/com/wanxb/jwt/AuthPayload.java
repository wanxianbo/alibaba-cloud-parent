package com.wanxb.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * <p>
 * 自定义的jwt的PAYLOAD
 * 也可以使用官网推荐封装好的包https://jwt.io
 * </p>
 *
 * @author wanxinabo
 * @date 2021/10/8
 */
public final class AuthPayload {
    private String userName;

    private String jti;

    private String clientId;

    private String mobile;

    private Long userId;

    private List<String> scope;

    private String dc;

    private boolean expired;

    public static AuthPayload fromJsonNode(JsonNode jsonNode) {
        long now = System.currentTimeMillis() / 1000;
        AuthPayload payload = new AuthPayload();
        payload.setJti(requireNonNull(jsonNode.get("jti"), "jti field cannot be null").textValue());
        payload.setExpired(now >= jsonNode.get("exp").longValue());
        payload.setClientId(jsonNode.get("client_id").textValue());
        payload.setUserName(jsonNode.get("user_name").textValue());
        payload.setMobile(ofNullable(jsonNode.get("mobile")).map(JsonNode::textValue).orElse(""));
        payload.setDc(ofNullable(jsonNode.get("dc")).map(JsonNode::textValue).orElse(""));
        payload.setUserId(jsonNode.get("userId").longValue());
        JsonNode scopeNode = jsonNode.get("scope");
        List<String> scopeList = IntStream.range(0, scopeNode.size())
                .mapToObj(i -> scopeNode.get(i).textValue()).collect(Collectors.toList());
        payload.setScope(scopeList);
        return payload;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getScope() {
        return scope;
    }

    public void setScope(List<String> scope) {
        this.scope = scope;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AuthPayload that = (AuthPayload) o;

        return new EqualsBuilder()
                .append(expired, that.expired)
                .append(userName, that.userName)
                .append(jti, that.jti)
                .append(clientId, that.clientId)
                .append(mobile, that.mobile)
                .append(userId, that.userId)
                .append(scope, that.scope)
                .append(dc, that.dc)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userName)
                .append(jti)
                .append(clientId)
                .append(mobile)
                .append(userId)
                .append(scope)
                .append(dc)
                .append(expired)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "AuthPayload{" +
                "userName='" + userName + '\'' +
                ", jti='" + jti + '\'' +
                ", clientId='" + clientId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", userId=" + userId +
                ", scope=" + scope +
                ", dc='" + dc + '\'' +
                ", expired=" + expired +
                '}';
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis() / 1000);
        System.out.println(new String(Base64.getUrlDecoder().decode("eyJ1c2VyX25hbWUiOiIzNjA0MjUxOTg5MTExMjQ5NTAiLCJzY29wZSI6WyIxMDAwMjAiLCIxMDAwMjEiLCIxMDAwMjIiLCIxMDAwMjMiLCIxMDAwMjUiLCIxMDAwMjciXSwibW9iaWxlIjoiMTUxNzkxOTQ5MjkiLCJjdXJyZW50VXNlclN0ciI6IntcImN1cnJlbnRVbml0SWRcIjoxMTU3MzA4Nzc2MjkxMTAyNzIyLFwiaWRcIjoxMTU4MjYxMDE1MTQyODU0NjU4LFwiaWRjYXJkXCI6XCIzNjA0MjUxOTg5MTExMjQ5NTBcIixcIm1vYmlsZVwiOlwiMTUxNzkxOTQ5MjlcIixcIm5hbWVcIjpcIueGiuWwj-atplwiLFwicG9zdElkU2V0XCI6WzExNTczMDg3NzY5NjIxOTEzNjVdLFwicG9zdFNlcXVlbmNlU2V0XCI6WzEwMDA4M119IiwiZXhwIjoxNjI4MTYzMDcxLCJ1c2VySWQiOjExNTgyNjEwMTUxNDI4NTQ2NTgsImp0aSI6ImYwZTU2OTY3LTdmZjYtNDE2Ni1iNDMxLTgyYmI1MjI4ZjMxYSIsImNsaWVudF9pZCI6ImRhbmdqaWFuLXdlYiIsImRjIjoibGltaW4ifQ")));
    }
}
