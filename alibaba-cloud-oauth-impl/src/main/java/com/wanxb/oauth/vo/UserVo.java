package com.wanxb.oauth.vo;

import com.wanxb.user.entity.UcUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.HashSet;

/**
 * <p>
 *
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/16
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserVo extends User {
    private static final long serialVersionUID = -1616279195278053385L;

    private Long userId;

    private String mobile;

    //
    private String idCard;

    //昵称
    private String name;

    /**
     *
     * @param ucUser 用户信息
     * @param username 登录主键
     */
    public UserVo(UcUser ucUser, String username) {
//        boolean enabled = true; // 可用性 :true:可用 false:不可用
//        boolean accountNonExpired = true; // 过期性 :true:没过期 false:过期
//        boolean credentialsNonExpired = true; // 有效性 :true:凭证有效 false:凭证无效
//        boolean accountNonLocked = true; // 锁定性 :true:未锁定 false:已锁定
        this(ucUser.getId(),username,ucUser.getIdcard(),ucUser.getName(),ucUser.getMobile(),ucUser.getPassword());
    }

    /**
     *
     * @param userId 用户id
     * @param username 登录主键
     * @param idCard 身份证号
     * @param name 昵称
     * @param mobile 手机号
     * @param password 密码
     */
    public UserVo(Long userId, String username, String idCard, String name, String mobile, String password) {
//        boolean enabled = true; // 可用性 :true:可用 false:不可用
//        boolean accountNonExpired = true; // 过期性 :true:没过期 false:过期
//        boolean credentialsNonExpired = true; // 有效性 :true:凭证有效 false:凭证无效
//        boolean accountNonLocked = true; // 锁定性 :true:未锁定 false:已锁定
        this(username, password,
                true, true, true, true, new HashSet<GrantedAuthority>());
        this.userId = userId;
        this.mobile = mobile;
        this.idCard = idCard;
        this.name = name;
    }


    public UserVo(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }
}
