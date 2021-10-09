package com.wanxb.oauth.config;

/**
 * <p>
 *
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/16
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Base64;

/**
 * 解说：
 * WebSecurityConfigurerAdapter默认情况下是springsecurity的http配置
 * 通过会话对用户进行身份验证（如表单登录）
 * AuthorizationServerSecurityConfiguration 为我们默认配置了3个endPoint
 * /oauth/token
 * /oauth/check_token
 * /oauth/token_key
 * 当不存在超过此范围的鉴权url的时候，不需要单独配置WebSecurityConfigurerAdapter
 * 要实现authorization_code模式得配置/oauth/authorize需要鉴权
 * 1.通过httpBasic填写用户名密码或者重定向到登录页先登录
 * 2.自定义/oauth/confirm_access（用户确认授权页面）
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DelegatingMessageSource messageSource;

    @Autowired
    @Qualifier("userDetailsServiceCustomImpl")
    private UserDetailsService userDetailsService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        //新版的security的自动配置包没有提供AuthenticationManager，需要手动调用authenticationManagerBean()这个方法，
        //并且需要增加如下的messageSource配置，以保证正确的提示语
        ReloadableResourceBundleMessageSource messageSource1 = new ReloadableResourceBundleMessageSource();
        messageSource1.addBasenames("classpath:org/springframework/security/messages");
        messageSource.setParentMessageSource(messageSource1);
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .logout();
        http.httpBasic();
        http.authorizeRequests()
                .antMatchers("/oauth/authorize").authenticated()
                .antMatchers("/oauth/confirm_access").authenticated()
                .antMatchers("/rsa/publicKey").authenticated()
                .antMatchers("/logout").authenticated()
                .and()
                .requestMatchers()
                .antMatchers("/oauth/authorize")
                .antMatchers("/logout")
                .antMatchers("/oauth/confirm_access");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        System.out.println(encode);
        System.out.println(passwordEncoder.matches("123456", "$2a$10$nuqUleKc1ryasLyCNhNyLuaXdBfW/PTO96hCcbadqEu0nuPzaM/8m"));
        //
        Base64.Decoder base64Decoder = Base64.getUrlDecoder();
        System.out.println(new String(base64Decoder.decode("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9")));
        System.out.println(new String(base64Decoder.decode("eyJ1c2VyX25hbWUiOiIyNzgzOTc0MTEyODYxMjM0NTYiLCJzY29wZSI6WyIxMDAwMjAiLCIxMDAwMjEiLCIxMDAwMjIiLCIxMDAwMjMiLCIxMDAwMjUiLCIxMDAwMjciXSwiYXRpIjoiMjJjMDcyYjYtNzYwOC00OTMyLWI3NDQtM2Y5NjY0MTBiNjU3IiwibW9iaWxlIjoiMTg2OTAxOTkxNzQiLCJjdXJyZW50VXNlclN0ciI6IntcImN1cnJlbnRVbml0SWRcIjoxMjQ2OTg3NDcwMTI2NTc5NzE0LFwiaWRcIjoxMjU0OTQ1MzcyODg0MTY0NjA5LFwiaWRjYXJkXCI6XCIyNzgzOTc0MTEyODYxMjM0NTZcIixcIm1vYmlsZVwiOlwiMTg2OTAxOTkxNzRcIixcIm5hbWVcIjpcIuatpuWwj-S7jlwiLFwicG9zdElkU2V0XCI6WzEyNDY5ODc0NzI1NTUwODE3MzFdLFwicG9zdFNlcXVlbmNlU2V0XCI6WzEwMDA4M10sXCJ1c2VyTmFtZVwiOlwid3gxXCJ9IiwiZXhwIjoxNjM1MzE1NjI5LCJ1c2VySWQiOjEyNTQ5NDUzNzI4ODQxNjQ2MDksImp0aSI6ImIyNzIyZjFkLWQ2ZTgtNDcxZS04Mjc5LTZlNzc4YjFmMzJjYSIsImNsaWVudF9pZCI6ImRhbmdqaWFuLXdlYiIsImRjIjoibGltaW4ifQ")));
    }


}
