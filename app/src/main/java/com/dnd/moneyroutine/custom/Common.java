package com.dnd.moneyroutine.custom;

public class Common {
    // 공통으로 관리되는 변수
    public static final String SERVER = "http://moneyroutinebeanstalk-env-1.eba-ptkdjtbj.ap-northeast-2.elasticbeanstalk.com/";

    // SharedPreferences 에서 토큰을 저장하는 key
    public static final String tokenKey = "userToken";

    public static final String REFRESH_TOKEN_KEY = "refreshToken";

    public String getTokenKey() {
        return tokenKey;
    }
}
