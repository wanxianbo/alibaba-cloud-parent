package com.wanxb.oauth.config;

import com.wanxb.oauth.provider.VerifyCodePasswordTokenGranter;
import com.wanxb.user.service.UcUserService;
import com.wanxb.util.CipherUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
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
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

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

    // ???????????????

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private DataSource dataSource;

    @Autowired
    @Qualifier("userDetailsServiceCustomImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    private UcUserService ucUserService;


//    @Value("${dc}")
//    private String dc;

    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * token ???????????????????????????
     * @return JwtAccessTokenConverter
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        String privateKey = template.boundValueOps("rsa:privateKey").get();
        jwtAccessTokenConverter.setSigningKey(privateKey);
        jwtAccessTokenConverter.setVerifierKey(CipherUtils.generateRSAPublicKeyFromPrivateKeyInPemFormat(privateKey).get());
        DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
        tokenConverter.setUserTokenConverter(new UserAuthenticationConverter());
        jwtAccessTokenConverter.setAccessTokenConverter(tokenConverter);
        return jwtAccessTokenConverter;
    }

    // ?????????????????????????????????
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
        // tokenKeyAccess????????????"/oauth/token_key"???access??????
        // checkTokenAccess ????????????"/oauth/check_token"???access??????
        // allowFormAuthenticationForClients ?????????????????????????????????client_id????????????false
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // tokenServices token????????????????????????????????????redis??????
        // clientDetailsService ????????????????????????????????????????????????inMemory??????jdbc
        // requestFactory ?????????????????????DefaultOAuth2RequestFactory
        // grantType ???????????????????????????
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.POST);
        endpoints.tokenServices(defaultTokenServices());
        // ?????????????????????
        endpoints.authorizationCodeServices(authorizationCodeServices());
        endpoints.requestFactory(requestFactory());
        // token??????????????? ?????????5???
        endpoints.tokenGranter(tokenGranter());
        super.configure(endpoints);
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }

    /**
     * token???????????????????????????????????????????????????grant_type?????????
     * GrantTypes??????
     * spring security???????????????5???grant_type
     * 1.authorization_code ??? ???????????????(??????????????????code,?????????token)
     * 2.password ??? ????????????(????????????,???????????????,????????????token)
     * 3.client_credentials ??? ???????????????(?????????,????????????????????????,???????????????????????????????????????????????????????????????)
     * 4.implicit ??? ????????????(???redirect_uri ???Hash??????token; Auth??????????????????????????????,???JS,Flash)
     * 5.refresh_token ??? ??????access_token
     * ??????????????????????????????grant_type
     * 6.sms_code ??? ???????????????
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
        AuthorizationServerTokenServices tokenServices = defaultTokenServices();
        AuthorizationCodeServices authorizationCodeServices = authorizationCodeServices();
        OAuth2RequestFactory requestFactory = requestFactory();

        List<TokenGranter> tokenGranters = new ArrayList<>();
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
//        //sms_code ??? ???????????????
        tokenGranters.add(new VerifyCodePasswordTokenGranter(authenticationManager, tokenServices, clientDetails,
                requestFactory, userDetailsService, template, ucUserService));

        return tokenGranters;
    }

    @Primary
    @Bean
    public DefaultTokenServices defaultTokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        // jwt????????????
        tokenServices.setTokenStore(jwtTokenStore());
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancers = new ArrayList<>();
//        if (StringUtils.isEmpty(dc)) {
//            dc = "limin";
//        }
        enhancers.add(new UserJwtTokenEnhancer());
        enhancers.add(jwtAccessTokenConverter());
        // ????????????Enhancer??????EnhancerChain???delegates?????????
        enhancerChain.setTokenEnhancers(enhancers);

        tokenServices.setTokenEnhancer(enhancerChain);
        // ??????refresh_code
        tokenServices.setSupportRefreshToken(true);
        //??????refresh_token??????????????????
        //??????/oauth/token?grant_type=refresh_token??????
        //false??????????????????,?????????????????????token???refreshtoken???
        //true????????????????????????????????????token
        tokenServices.setReuseRefreshToken(false);
        tokenServices.setClientDetailsService(clientDetails());
        // token?????????????????????????????????12?????????????????????2??????
        tokenServices.setAccessTokenValiditySeconds(60 * 60 * 2);
        return tokenServices;
    }


}
