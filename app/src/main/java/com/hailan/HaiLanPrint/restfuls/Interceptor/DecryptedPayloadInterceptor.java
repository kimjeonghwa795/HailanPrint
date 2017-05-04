package com.hailan.HaiLanPrint.restfuls.Interceptor;

import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.EncryptUtil;
import com.google.gson.GsonBuilder;
import com.socks.library.KLog;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by yoghourt on 5/5/16.
 */
public class DecryptedPayloadInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Response originalResponse;

        if (originalRequest.body() != null) {
            String bodyContent = requestBodyToString(originalRequest.body());

//            KLog.e("\n" + bodyContent.replaceAll("\\\\", ""));
            try {
                bodyContent = EncryptUtil.encrypt(bodyContent);  // 加密
                bodyContent = URLEncoder.encode(bodyContent, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            RequestBody newBody = RequestBody.create(originalRequest.body().contentType(), bodyContent);

//            KLog.e("RequestBody是 : " + requestBodyToString(originalRequest.body()));

            // Request customization: add request headers
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .method(originalRequest.method(), newBody);

            Request newRequest = requestBuilder.build();

//            KLog.e(String.format("Sending request %s on %s%n%s",
//                    newRequest.url(), chain.connection(), newRequest.headers()));

            originalResponse = chain.proceed(newRequest);
        } else {
            originalResponse = chain.proceed(originalRequest);
        }

        if (!originalResponse.isSuccessful()) {
            KLog.e("请求失败");
        }

//        String responseContent = new Buffer().write(originalResponse.body().bytes()).readUtf8();

//        KLog.e("返回的是 : " + new String(originalResponse.body().bytes()));

        String responseContent = originalResponse.body().string();
        try {
            responseContent = URLDecoder.decode(responseContent, "UTF-8");
            responseContent = EncryptUtil.decrypt(responseContent);  // 解密
        } catch (Exception e) {
            e.printStackTrace();
        }

        ResponseData mResponseData = new GsonBuilder().create().fromJson(responseContent, ResponseData.class);

        Response newResponse = originalResponse.newBuilder().body(ResponseBody.create(originalResponse.body().contentType(), responseContent)).build();

        return newResponse;
    }

    private String requestBodyToString(final RequestBody requestBody) {
        try {
            final RequestBody copy = requestBody;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}

