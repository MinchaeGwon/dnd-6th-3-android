package com.dnd.moneyroutine.custom;

import android.content.Context;
import android.content.SharedPreferences;

import com.dnd.moneyroutine.service.JWTUtils;

/*
 * PreferenceManager:
 * 로그인이 유지될 수 있도록 SharedPreference에 토큰 값을 저장하는 것을 돕는 클래스
 * */
public class PreferenceManager {

    /*
    데이터를 저장 할 때는 SharedPreferences의 Editor를 이용하여, 데이터 타입에 따라 put을 해주시면 됩니다. 마지막에 commit을 해야 적용되니 주의하시기 바랍니다.
    데이터를 로드 할 때는 저장할 때 사용했던 key 값을 이용하시면 됩니다. 로드 할 때, 해당 key에 해당되는 데이터가 없다면 default로 설정한 값이 호출됩니다.
     */
    public static final String PREFERENCES_NAME = "prefs";

    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * String 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * boolean 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();

    }

    /**
     * int 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setInt(Context context, String key, int value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();

    }

    /**
     * long 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setLong(Context context, String key, long value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();

    }

    /**
     * float 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setFloat(Context context, String key, float value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();

    }

    public static String getToken(Context context, String key) {
        String token = getString(context, key);
        if(token == null || JWTUtils.validateToken(token) <= 0) {
            removeKey(context, key);
            return null;
        }
        return token;
    }

    /**
     * String 값 로드
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        String value = prefs.getString(key, DEFAULT_VALUE_STRING);
        return value;
    }

    /**
     * boolean 값 로드
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        boolean value = prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);
        return value;
    }

    /**
     * int 값 로드
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        int value = prefs.getInt(key, DEFAULT_VALUE_INT);
        return value;
    }

    /**
     * long 값 로드
     * @param context
     * @param key
     * @return
     */
    public static long getLong(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        long value = prefs.getLong(key, DEFAULT_VALUE_LONG);
        return value;
    }

    /**
     * float 값 로드
     * @param context
     * @param key
     * @return
     */
    public static float getFloat(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        float value = prefs.getFloat(key, DEFAULT_VALUE_FLOAT);
        return value;
    }

    /**
     * 키 값 삭제
     * @param context
     * @param key
     */
    public static void removeKey(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(key);
        edit.commit();
    }

    /**
     * 모든 저장 데이터 삭제
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
}
