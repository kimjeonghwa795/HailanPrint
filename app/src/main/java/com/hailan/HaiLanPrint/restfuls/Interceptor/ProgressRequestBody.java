package com.hailan.HaiLanPrint.restfuls.Interceptor;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;

/**
 * Created by yoghourt on 5/13/16.
 */
public class ProgressRequestBody extends RequestBody {

    private UploadCallbacks mListener;
    private String mJsonString;

    private static final int DEFAULT_BUFFER_SIZE = 10240;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);

        void onError();

        void onFinish();
    }

    public ProgressRequestBody(String jsonString, final UploadCallbacks listener) {
        mJsonString = jsonString;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/json");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Charset charset = contentType().charset();
        if (charset == null) {
            charset = Util.UTF_8;
        }
        byte[] bytes = mJsonString.getBytes(charset);

        long uploaded = 0;

        int offset = 0;
        int byteCount = DEFAULT_BUFFER_SIZE;

//        KLog.e("bytes.length : " + bytes.length);

        Handler handler = new Handler(Looper.getMainLooper());

        do {
            if ((byteCount + offset) > bytes.length) {
                byteCount = bytes.length - offset;
            }

            uploaded += byteCount;
            // update progress on UI thread
            handler.post(new ProgressUpdater(uploaded, bytes.length));

            sink.write(bytes, offset, byteCount);
            offset += byteCount;
//            KLog.e("offset : " + offset);
        } while (offset < bytes.length);
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;

        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            if (mListener != null) {
                mListener.onProgressUpdate((int) (100 * mUploaded / mTotal));
            }
        }
    }
}