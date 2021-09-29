package com.wanxb.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * <p>
 *  资源服务器（需要用token换取的都是资源）
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/16
 */

/**
 * ResourceServerConfigurerAdapter默认情况下是spring security oauth2的http配置
 * 使用一个特殊的过滤器(OAuth2AuthenticationProcessingFilter)来检查请求中的承载令牌，以便通过OAuth2对请求进行认证
 * <p>
 * 解说：
 * 资源服务器中配置的url访问时会过：OAuth2AuthenticationProcessingFilter
 * 可以获取请求头（Authorization：格式："Bearer ${token}"）（ps：当请求头不存在时，获取请求参数（access_token））中的token值
 * 校验token并读取与该token相关的用户信息存入上下文，以便后续的资源url使用
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private ResourceServerTokenServices resourceServerTokenServices;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 使用token认证，关闭 CSRF保护
        http.csrf().disable();
        http.httpBasic();

        http.authorizeRequests()
                .antMatchers("/ac/*/api/**").authenticated()
                .antMatchers("/oauth/removeToken").authenticated()
                .and()
                .requestMatchers()
                .antMatchers("/ac/*/api/**")
                .antMatchers("/oauth/removeToken");
    }
}
