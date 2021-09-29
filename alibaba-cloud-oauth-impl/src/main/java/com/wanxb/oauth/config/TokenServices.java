package com.wanxb.oauth.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  每次重新登录的时候我们需要设置以前的token失效，保证以前登录的账号需要重新登录
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/29
 */
public class TokenServices extends DefaultTokenServices {
    /**
     * 创建新token的时候，使以前的token失效
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
//    @Transactional
//    @Override
//    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
//        //每次登录获取存在的token,如果不为空，就让他失效，然后重新创建token
//        OAuth2AccessToken existAccessToken = getAccessToken(authentication);
//        if (existAccessToken != null) {
//            revokeToken(existAccessToken.getValue());
//        }
//        return super.createAccessToken(authentication);
//    }
}
