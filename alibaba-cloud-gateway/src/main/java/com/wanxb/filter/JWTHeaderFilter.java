package com.wanxb.filter;

import com.google.common.base.Splitter;
import com.wanxb.config.DcCache;
import com.wanxb.jwt.JWTToken;
import com.wanxb.jwt.JWTVerifier;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  bearer 认证头的拦截器
 * </p>
 *
 * @author wanxinabo
 * @date 2021/10/12
 */
@Component
public class JWTHeaderFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JWTHeaderFilter.class);

    public static final String JWT_PAYLOAD = ".JWT_PAYLOAD";

    private static final String OPEN_FLAG = ".OPEN_FLAG";

    private final DcCache dcCache;

    public JWTHeaderFilter(DcCache dcCache) {
        this.dcCache = dcCache;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Object attribute = exchange.getAttribute(OPEN_FLAG);
        if (Objects.nonNull(attribute)) {
            // 开放的路径
            return chain.filter(exchange);
        }

        // 判断是否是 open 的请求
        if (isOpenRequest(exchange)) {
            return chain.filter(exchange);
        }

        String bearerToken = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (!validateToken(bearerToken) && !isInWhitelist(exchange.getRequest())) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        assert bearerToken != null;
        JWTToken jwtToken = getJwtToken(bearerToken);
        boolean verified;
        try {
            verified = verifySignature(jwtToken);
        } catch (Exception e) {
            log.error("signature not verified. cause: ", e);
            verified = false;
        }

        if (!verified) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().setComplete();
        }

        assert jwtToken != null;
        // 设置上下文信息
        exchange.getAttributes().put(JWT_PAYLOAD, jwtToken.getPayload());
        return chain.filter(exchange);
    }

    private boolean verifySignature(JWTToken jwtToken) {
        return new JWTVerifier(jwtToken, dcCache).verify();
    }

    private JWTToken getJwtToken(String bearerToken) {
        String tokenStr = bearerToken.substring(6).trim();
        int lastIndexOfDot = tokenStr.lastIndexOf('.');
        String content = tokenStr.substring(0, lastIndexOfDot);
        String sig = tokenStr.substring(lastIndexOfDot + 1);
        try {
            return new JWTToken(content.getBytes(), sig.getBytes());
        } catch (Exception e) {
            log.error("can not build token info. ", e);
            return null;
        }
    }

    private static final String BEARER = "bearer";

    private boolean validateToken(String bearerToken) {
        if (StringUtils.isEmpty(bearerToken)) return false;
        return BEARER.equalsIgnoreCase(bearerToken.substring(0, 6))
                && dotOccurrence(bearerToken) == 2;
    }

    protected boolean isInWhitelist(ServerHttpRequest request) {
        return false;
    }

    /**
     * 判断是否JWT格式
     * @param bearToken token
     * @return int
     */
    private int dotOccurrence(String bearToken) {
        //46 means '.'
        return bearToken.chars().reduce(0, (p, c) -> c == 46 ? p + 1 : p);
    }

    private static final Splitter PATH_SPLITTER = Splitter.on('/').trimResults();

    private boolean isOpenRequest(ServerWebExchange exchange) {
        URI uri = exchange.getRequest().getURI();
        List<String> pathSegments = PATH_SPLITTER.splitToList(uri.getPath());
        pathSegments = pathSegments.stream().filter(s -> !StringUtils.isNotBlank(s)).collect(Collectors.toList());
        if (pathSegments.size() > 2) {
            return "open".equalsIgnoreCase(pathSegments.get(2));
        }

        return false;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
