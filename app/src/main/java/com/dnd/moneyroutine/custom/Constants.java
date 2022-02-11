package com.dnd.moneyroutine.custom;

// 상수 관리 클래스
public class Constants {
    public static final String SERVER = "http://moneyroutinebeanstalk-env-1.eba-ptkdjtbj.ap-northeast-2.elasticbeanstalk.com/";

    public static final String tokenHeader = "X-AUTH-TOKEN"; // 서버 요청 헤더에 들어갈 필드
    public static final String tokenKey = "userToken"; // SharedPreferences에서 토큰을 저장하는 key
}
