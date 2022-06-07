//package com.wanxb.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author wanxianbo
// * @description oauth2 callback
// * @date 创建于 2022/6/6
// */
//@Slf4j
//@RestController
//public class LoginController {
//
//    @RequestMapping(value = "/callback", method = RequestMethod.GET)
//    public void callback(@RequestParam("code") String code, ServletRequest servletRequest,
//                         ServletResponse servletResponse) throws Exception
//    {
//        HttpServletResponse resp = (HttpServletResponse) servletResponse;
//        ResponseEntity<String> response = null;
//        log.info("Authorization Code------" + code);
//        RestTemplate restTemplate = new RestTemplate();
//        StringBuilder tokenUrl = getVerifyUrl(code);
//        response = restTemplate.exchange(tokenUrl.toString(), HttpMethod.POST, null, String.class);
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode node = mapper.readTree(response.getBody());
//        String token = node.path("access_token").asText();
//        String accessTokenUrl = "" + "/?token=" + token;
////        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
////        saveUserInfo(token, principal);
//        resp.sendRedirect(accessTokenUrl);
//    }
//
//    /**
//     *
//     * getVerifyUrl:(拼接验证oauth2验证code的请求路径). <br/>
//     *
//     * @author wenchall
//     * @param code
//     * @return
//     * @since JDK 1.7
//     */
//    private StringBuilder getVerifyUrl(String code)
//    {
//        StringBuilder tokenUrl = new StringBuilder(ConfigConsts.SERVICE_URL);
//        tokenUrl.append(ConfigConsts.OAUTH_TOKEN);
//        tokenUrl.append("?client_id=");
//        tokenUrl.append(ConfigConsts.CLIENT_ID);
//        tokenUrl.append("&client_secret=");
//        tokenUrl.append(ConfigConsts.CLIENT_SECERT);
//        tokenUrl.append("&code=");
//        tokenUrl.append(code);
//        tokenUrl.append("&grant_type=authorization_code");
//        tokenUrl.append("&redirect_uri=");
//        tokenUrl.append(ConfigConsts.REDIRECT_URL);
//        return tokenUrl;
//    }
//}
