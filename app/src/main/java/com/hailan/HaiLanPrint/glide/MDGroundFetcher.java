package com.hailan.HaiLanPrint.glide;

import android.content.Context;
import android.util.Base64;

import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.restfuls.FileRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.socks.library.KLog;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yoghourt on 3/21/16.
 */
public class MDGroundFetcher implements DataFetcher<InputStream> {

    private Context mContext;

    private final MDImage mImage;

    private InputStream mInputStream;

    public MDGroundFetcher(MDImage image, Context context) {
        this.mImage = image;
        this.mContext = context;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {

        ResponseData responseData = FileRestful.getInstance().GetPhoto(mImage.getPhotoID());

        if (responseData.getCode() == ResponseCode.SystemError.getValue()) {
            KLog.e("Glide请求大图失败,再次请求缩略图");
            responseData = FileRestful.getInstance().GetPhoto(mImage.getPhotoSID());
        }

        if (responseData.getCode() == ResponseCode.Normal.getValue()) {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(responseData.getContent(), Base64.DEFAULT);
            mInputStream = new ByteArrayInputStream(bitmapArray);
            KLog.e("Glide请求图片成功");
            return mInputStream;
        }

        KLog.e("Glide请求图片返回null, 图片PhotoID : "  + mImage.getPhotoID() + " PhotoSID : " + mImage.getPhotoSID());

        return null;
    }

    @Override
    public void cleanup() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                //e.printStackTrace();
            } finally {
                mInputStream = null;
            }
        }
    }

    /**
     * 在UI线程中调用，返回用于区别数据的唯一id
     *
     * @return
     */
    @Override
    public String getId() {
        return String.valueOf(mImage.getPhotoSID());
    }

    /**
     * 在UI线程中调用，取消加载任务
     */
    @Override
    public void cancel() {

    }
}
