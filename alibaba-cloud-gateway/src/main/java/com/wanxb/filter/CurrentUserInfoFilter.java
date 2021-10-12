package com.wanxb.filter;

import com.wanxb.constants.GatewayConstants;
import com.wanxb.jwt.AuthPayload;
import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

/**
 * <p>
 * 上下文设置过滤器
 * </p>
 *
 * @author wanxinabo
 * @date 2021/10/12
 */
@Component
public class CurrentUserInfoFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(CurrentUserInfoFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Object object = exchange.getAttribute(GatewayConstants.OPEN_FLAG);
        // open 接口没有上下文
        if (Objects.nonNull(object)) return chain.filter(exchange);

        Object info = exchange.getAttribute(GatewayConstants.JWT_PAYLOAD);
        if (!(info instanceof AuthPayload)) {
            // 不是上下文对象直接返回未认证
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        AuthPayload authPayload = (AuthPayload) info;
        return chain.filter(addCurrentUserInfo(exchange, authPayload));
    }

    private ServerWebExchange addCurrentUserInfo(ServerWebExchange exchange, AuthPayload authPayload) {
        URI uri = exchange.getRequest().getURI();
        String originalQuery = uri.getRawQuery();
        StringBuilder query = new StringBuilder();

        if (StringUtils.isNotBlank(originalQuery)) {
            query.append(originalQuery);
            if (originalQuery.charAt(originalQuery.length() - 1) != '&') {
                query.append("&");
            }
        }

        query.append("currentUcUserId=").append(authPayload.getUserId().toString());
        query.append("&clientId=").append(authPayload.getClientId());
        if (CollectionUtils.isNotEmpty(authPayload.getScope())) {
            query.append("&scope=").append(String.join(",", authPayload.getScope()));
        }

        try {
            URI newUri = UriComponentsBuilder.fromUri(uri)
                    .replaceQuery(query.toString())
                    .build(true)
                    .toUri();

            // 实现跳转
            ServerHttpRequest request = exchange.getRequest().mutate().uri(newUri).build();
            return exchange.mutate().request(request).build();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid URI query: \"" + query.toString() + "\"");
        }
    }

    @Override
    public int getOrder() {
        return -1000;
    }
}
