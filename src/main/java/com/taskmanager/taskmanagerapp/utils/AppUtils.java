package com.taskmanager.taskmanagerapp.utils;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;

import com.taskmanager.taskmanagerapp.model.CommonResponse;

public class AppUtils {
    public static HashMap<String, Object> asHashMap(String key, Object value) {
        return new HashMap<String, Object>() {
            {
                put(key, value);
            }
        };
    }

    public static ResponseEntity<?> httpResponseBadRequest(String errMsg) {
        return httpResponseBadRequest(errMsg, null);
    }

    public static ResponseEntity<?> httpResponseBadRequest(String errMsg, Object data) {
        CommonResponse<?> _res = CommonResponse.<Object>builder()
                .isSuccess(false)
                .message(errMsg)
                .data(data)
                .build();
        return ResponseEntity.badRequest().body(_res);
    }

    public static ResponseEntity<?> httpResponseOk(Object data) {
        return httpResponseOk(null, data);
    }

    public static ResponseEntity<?> httpResponseOk(String msg, Object data) {
        CommonResponse<?> _res = CommonResponse.<Object>builder()
                .isSuccess(true)
                .message(msg)
                .data(data)
                .build();
        return ResponseEntity.ok().body(_res);
    }
}
