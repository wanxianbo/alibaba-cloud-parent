package com.wanxb.oauth.config;

import com.wanxb.oauth.constants.TokenConstants;
import com.wanxb.oauth.vo.UserVo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *  自定义UserAuthentication的转换(提取jwt PAYLOAD的数据)，不从数据库查询认证的用户信息
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/29
 */
public class UserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        if (!map.containsKey(AUTHORITIES)) {
            return Collections.emptyList();
        }
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                    .collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (MapUtils.isNotEmpty(map)) {
            Long userId = null;
            userId  = toLong(map.get(TokenConstants.USER_ID));

            Object mobileObj = map.get(TokenConstants.MOBILE);
            String mobile = Objects.isNull(mobileObj) ? "" : mobileObj.toString();

            Object usernameObj = map.get(UserAuthenticationConverter.USERNAME);
            String username = Objects.isNull(usernameObj) ? "" : usernameObj.toString();

            Object idcardObj = map.get(TokenConstants.ID_CARD);
            String idcard = Objects.isNull(idcardObj) ? null : idcardObj.toString();

            Object nameObj = map.get(TokenConstants.NAME);
            String name = Objects.isNull(nameObj) ? null : nameObj.toString();

            return new UsernamePasswordAuthenticationToken(new UserVo(userId, username, idcard, name, mobile, ""), "N/A", getAuthorities(map));
        }

        return null;
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(String.valueOf(value));
    }
}
