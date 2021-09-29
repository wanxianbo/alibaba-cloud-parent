package com.wanxb.constants;

/**
 * <p>
 * oauth2 的常量类
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/29
 */
public interface OauthConstants {

    String VERIFY_CODE_KEY = "oauth:code:%s";

    class Client {
        /**
         * alibaba cloud 的web client_id
         */
        public static final String ALIBABA_CLOUD_WEB = "alibaba-cloud-web";
        /**
         * alibaba cloud 的app client_id
         */
        public static final String ALIBABA_CLOUD_APP = "alibaba-cloud-app";

    }
}
