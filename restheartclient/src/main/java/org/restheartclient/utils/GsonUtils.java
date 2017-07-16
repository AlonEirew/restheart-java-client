package org.restheartclient.utils;

import com.google.gson.Gson;

public class GsonUtils {

    private static final Gson gson = new Gson();

    public static String toJson(Object jsonElement) {
        if (jsonElement != null) {
            return gson.toJson(jsonElement);
        }
        return null;
    }
}
