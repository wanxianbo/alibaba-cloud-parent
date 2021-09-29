package com.wanxb.oauth.config;

import com.wanxb.oauth.constants.TokenConstants;
import com.wanxb.oauth.vo.UserVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *  jwt内容相关信息增强类
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/27
 */
public class UserJwtTokenEnhancer implements TokenEnhancer {

    private static final String REQUEST_CACHE_CURRENT_USER_INFO = "currentUserInfo";

//    private String dc;
//
//    public UserJwtTokenEnhancer(String dc) {
//        this.dc = dc;
//    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        if (authentication.getUserAuthentication() instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication.getUserAuthentication();
            Map<String, Object> info = new LinkedHashMap<>(accessToken.getAdditionalInformation());
            if (authenticationToken.getPrincipal() instanceof UserVo) {
                UserVo userVo = (UserVo) authenticationToken.getPrincipal();
                if (Objects.nonNull(userVo)) {
                    //当前用户id
                    info.put(TokenConstants.USER_ID,userVo.getUserId());
                    //当前用户手机号
                    info.put(TokenConstants.MOBILE,userVo.getMobile());
                    //身份证号
                    info.put(TokenConstants.ID_CARD,userVo.getIdCard());
                    if(StringUtils.isNotBlank(userVo.getName())){
                        //昵称
                        info.put(TokenConstants.NAME,userVo.getName());
                    }
                    //独立部署标志
//                    info.put(TokenConstants.DC,dc);
                }
                RequestContextHolder.getRequestAttributes().setAttribute(REQUEST_CACHE_CURRENT_USER_INFO, userVo, RequestAttributes.SCOPE_REQUEST);
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        }
        return accessToken;
    }
}
