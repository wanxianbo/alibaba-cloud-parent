package com.wanxb.oauth.provider;

import com.wanxb.constants.OauthConstants;
import com.wanxb.user.service.UcUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/27
 */
public class VerifyCodePasswordTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "password_verify_code";

    protected UserDetailsService userDetailsService;

    protected StringRedisTemplate template;

    protected UcUserService iUcUserService;

    protected AuthenticationManager authenticationManager;

    public VerifyCodePasswordTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                                          OAuth2RequestFactory requestFactory,
                                          UserDetailsService userDetailsService,StringRedisTemplate template,UcUserService iUcUserService) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
        this.template = template;
        this.iUcUserService = iUcUserService;
        this.userDetailsService = userDetailsService;
    }

    public VerifyCodePasswordTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                                          OAuth2RequestFactory requestFactory, String grantType,
                                          UserDetailsService userDetailsService, StringRedisTemplate template, UcUserService iUcUserService) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.userDetailsService = userDetailsService;
        this.template = template;
        this.iUcUserService = iUcUserService;
    }


    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        // ???????????????????????????
        String username = parameters.get("username");
        // ???????????????????????????
        String verifyCode = parameters.get("verifyCode");
        // ????????????key
        String key = parameters.get("key");
        // ????????????????????????
        String password = parameters.get("password");

        parameters.remove("username");
        parameters.remove("verifyCode");
        parameters.remove("key");
        parameters.remove("password");

        //???????????????
        checkSecurityCode(key,verifyCode);

        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);

        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new InvalidGrantException(ase.getMessage());
        } catch (BadCredentialsException e) {
            // If the username/password are wrong the spec says we should send 400/invalid grant
            throw new InvalidGrantException(e.getMessage());
        }

        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }

    protected void checkSecurityCode(String key, String verifyCode) {
        if (StringUtils.isBlank(verifyCode)) {
            throw new InvalidGrantException("??????????????????");
        }
        String redisKey = String.format(OauthConstants.VERIFY_CODE_KEY, key);
        String value = template.opsForValue().get(redisKey);
        // ???????????????????????????
        if (!StringUtils.equals(verifyCode, value)) {
            throw new InvalidGrantException("???????????????");
        }
        // remove redis???key
        template.delete(key);
    }
}
