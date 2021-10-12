package com.wanxb.filter;

import com.google.common.collect.Lists;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.wanxb.constants.GatewayConstants.OPEN_FLAG;

/**
 * <p>
 * 开放的 url 不需要进行认证
 * </p>
 *
 * @author wanxinabo
 * @date 2021/10/12
 */
@Component
public class OpenUrlFilter implements GlobalFilter, Ordered {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * 不需要认证的 url
     */
    private static final List<String> PASS_URLS = Lists.newArrayList(
            "/ac/*/open/**", "/ac/*/oauth/**");

    /**
     * 需要认证的 url
     */
    private static final List<String> VALIDATE_URLS = Lists.newArrayList(
            "/ac/*/api/**", "/ac/*/operate/**");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI uri = exchange.getRequest().getURI();
        Optional<String> openOptional = PASS_URLS.stream().filter(a -> ANT_PATH_MATCHER.match(a, uri.getPath())).findFirst();
        if (openOptional.isPresent()) {
            exchange.getAttributes().put(OPEN_FLAG, OPEN_FLAG);
            return chain.filter(exchange);
        }

        Optional<String> validateOptional = VALIDATE_URLS.stream().filter(a -> ANT_PATH_MATCHER.match(a, uri.getPath())).findFirst();
        if (!validateOptional.isPresent()) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
