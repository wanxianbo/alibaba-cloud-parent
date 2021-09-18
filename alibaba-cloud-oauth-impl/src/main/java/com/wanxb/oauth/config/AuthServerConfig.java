package com.wanxb.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/16
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    StringRedisTemplate template;

    // 认证管理器

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private DataSource dataSource;


    @Value("${dc}")
    private String dc;

    // 客户端存储，存入数据库
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetails());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // tokenKeyAccess会去设置"/oauth/token_key"的access状态
        // checkTokenAccess 会去设置"/oauth/check_token"的access状态
        // allowFormAuthenticationForClients 设置是否需要鉴权客户端client_id，默认是false
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // tokenServices token的存储位置，我们一般采用redis存储
        // clientDetailsService 客户端配置，就是客户端存储位置，inMemory或者jdbc
        // requestFactory 一般配置默认的DefaultOAuth2RequestFactory
        // grantType 授权类型，自己定义
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.POST);
        endpoints.tokenServices(null);
        // 设置授权码服务
        endpoints.authorizationCodeServices(authorizationCodeServices());
        endpoints.requestFactory(requestFactory());
        // token的授权方式 默认有5种
        endpoints.tokenGranter(tokenGranter());
        super.configure(endpoints);
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }

    /**
     * token的授权方式，重写才能实现添加自定义grant_type的功能
     * GrantTypes说明
     * spring security默认提供了5种grant_type
     * 1.authorization_code — 授权码模式(即先登录获取code,再获取token)
     * 2.password — 密码模式(将用户名,密码传过去,直接获取token)
     * 3.client_credentials — 客户端模式(无用户,用户向客户端注册,然后客户端以自己的名义向’服务端’获取资源)
     * 4.implicit — 简化模式(在redirect_uri 的Hash传递token; Auth客户端运行在浏览器中,如JS,Flash)
     * 5.refresh_token — 刷新access_token
     * 根据需要添加的自定义grant_type
     * 6.sms_code — 验证码登录
     */
    @Bean
    public TokenGranter tokenGranter() {
        TokenGranter tokenGranter = new TokenGranter() {
            private CompositeTokenGranter delegate;

            @Override
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                if (delegate == null) {
                    delegate = new CompositeTokenGranter(getDefaultTokenGranters());
                }
                return delegate.grant(grantType, tokenRequest);
            }
        };
        return tokenGranter;
    }

    @Bean
    public OAuth2RequestFactory requestFactory() {
        return new DefaultOAuth2RequestFactory(clientDetails());
    }

    private List<TokenGranter> getDefaultTokenGranters() {
        ClientDetailsService clientDetails = clientDetails();
        AuthorizationServerTokenServices tokenServices = null;
        AuthorizationCodeServices authorizationCodeServices = authorizationCodeServices();
        OAuth2RequestFactory requestFactory = requestFactory();

        List<TokenGranter> tokenGranters = new ArrayList<TokenGranter>();
        //authorization_code
        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices,
                authorizationCodeServices, clientDetails, requestFactory));
        //refresh_token
        tokenGranters.add(new RefreshTokenGranter(tokenServices, clientDetails, requestFactory));
        ImplicitTokenGranter implicit = new ImplicitTokenGranter(tokenServices, clientDetails,
                requestFactory);
        //implicit
        tokenGranters.add(implicit);
        //client_credentials
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetails, requestFactory));
        //password
        if (authenticationManager != null) {
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager,
                    tokenServices, clientDetails, requestFactory));
        }
//        //sms_code — 验证码登录
//        tokenGranters.add(new VerifyCodePasswordTokenGranter(authenticationManager,tokenServices, clientDetails, requestFactory,userDetailsService,template,iUcUserService));

        return tokenGranters;
    }


}
