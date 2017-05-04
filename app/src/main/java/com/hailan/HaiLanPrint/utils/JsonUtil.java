package com.hailan.HaiLanPrint.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class JsonUtil {

    private static Gson gson = null;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(JsonObject.class, new JsonDeserializer<Object>() {
            @Override
            public Object deserialize(JsonElement jsonElement, Type type,
                                      JsonDeserializationContext jsonDeserializationContext)
                    throws JsonParseException {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                return jsonObject;
            }
        });

        gson = builder.disableHtmlEscaping().create();
    }

    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        return gson.toJson(object);
    }

    public static <T> T fromJson(String content, Class<T> clazz) {
        if (StringUtil.isEmpty(content) || clazz == null) {
            return null;
        }
        try {
            return gson.fromJson(content, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String content, TypeToken<T> token) {
        if (StringUtil.isEmpty(content) || token == null) {
            return null;
        }
        try {
            return gson.fromJson(content, token.getType());
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

}
