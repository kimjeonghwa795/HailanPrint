package com.hailan.HaiLanPrint.restfuls.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ResponseData {

    // / 响应编码
    public int Code = -1;

    // / 响应信息
    public String Message;

    // / 响应结果
    public String Content;

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public <T> T getContent(Class<? extends T> clazz) {
        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            if (Content == null) {
                return null;
            } else {
                return gson.fromJson(Content, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T getContent(TypeToken<T> type) {
        try {
            if (type == null) {
                return null;
            }
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            if (Content == null) {
                return null;
            } else {
                return gson.fromJson(Content, type.getType());
            }
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(this);
    }
}
