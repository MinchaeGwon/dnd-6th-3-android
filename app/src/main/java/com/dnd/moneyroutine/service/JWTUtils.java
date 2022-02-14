package com.dnd.moneyroutine.service;

import android.util.Base64;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.util.Date;

// JWT 토큰 관련 service class
public class JWTUtils {

    public static JsonObject decoded(String JWTEncoded) {
        if (JWTEncoded == null || JWTEncoded.length() == 0) {
            return null;
        }

        try {
            String[] split = JWTEncoded.split("\\.");

            if (split.length < 2) {
                return null;
            }

            return JsonParser.parseString(getJson(split[1])).getAsJsonObject();
        } catch (UnsupportedEncodingException e) {
            //Error
        }
        return null;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);

        return new String(decodedBytes, "UTF-8");
    }

    public static int getUserId(String token) {
        JsonObject jsonObject = decoded(token);

        if (jsonObject == null) {
            return -1;
        }

        return jsonObject.get("sub") != null ? jsonObject.get("sub").getAsInt() : -1;
    }

    public static long getExpireTime(String token) {
        JsonObject jsonObject = decoded(token);

        if (jsonObject == null) {
            return 0;
        }

        return jsonObject.get("exp").getAsLong();
    }

    public static long validateToken(String token) {
        long expireTime = JWTUtils.getExpireTime(token);
        Date date = new Date();
        long nowTime = date.getTime() / 1000;

        return expireTime - nowTime;
    }

    public static int validateTokenWithMinute(String token) {
        long diff = validateToken(token);
        return (int) diff / 60;
    }
}