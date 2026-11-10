package com.king.ai.api.framework.constant.consts;

/**
 * Consts
 *
 * @author xuxueli 2015-12-19
 */
public class Consts {

    /**
     * login captcha key
     */
    public static final String LOGIN_CAPTCHA_KEY = "king_ai_captcha:";

    /**
     * generate login captcha key
     */
    public static String getLoginCaptchaKey(String uuid) {
        return LOGIN_CAPTCHA_KEY + uuid;
    }

}
