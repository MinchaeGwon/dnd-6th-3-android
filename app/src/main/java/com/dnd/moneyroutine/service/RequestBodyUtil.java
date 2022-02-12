package com.dnd.moneyroutine.service;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RequestBodyUtil {
    public static RequestBody toRequestBody (String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body ;
    }
}
