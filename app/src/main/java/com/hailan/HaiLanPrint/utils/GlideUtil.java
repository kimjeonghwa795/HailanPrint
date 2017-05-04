package com.hailan.HaiLanPrint.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.SimpleResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.glide.MDGroundLoader;
import com.hailan.HaiLanPrint.glide.decoder.OriginalSizeBitmapDecoder;
import com.hailan.HaiLanPrint.glide.decoder.OriginalSizeBitmapDecoder2;
import com.hailan.HaiLanPrint.glide.transformation.RotateTransformation;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.OriginalSizeBitmap;
import com.hailan.HaiLanPrint.models.Size;
import com.socks.library.KLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by yoghourt on 5/18/16.
 */
public class GlideUtil {

    public static void loadImageByMDImage(ImageView imageView, MDImage mdImage, boolean showPlaceHolder) {

        if (mdImage != null) {
//            File cacheFile = Glide.getPhotoCacheDir(MDGroundApplication.sInstance);
//            KLog.e(" cacheFile : " + cacheFile.getPath());
//            KLog.e("cacheFolder size : " + getFileSize(cacheFile));

            Glide.with(MDGroundApplication.sInstance)
                    .load(mdImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(showPlaceHolder ? R.drawable.layerlist_image_placeholder : 0)
                    .error(R.drawable.layerlist_image_placeholder)
                    .dontAnimate()
                    .into(imageView);

        }
    }

    public static void loadImageByMDImageWithDialog(final ImageView imageView, MDImage mdImage) {
        ViewUtils.loading(imageView.getContext());
        Glide.with(imageView.getContext())
                .load(mdImage)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        imageView.setImageBitmap(bitmap);
                        ViewUtils.dismiss();
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        ViewUtils.dismiss();
                    }
                });
    }

    public static void loadImageByMDImageWithDialogLimit(final ImageView imageView, MDImage mdImage) {
        ViewUtils.loading(imageView.getContext());
        Glide.with(imageView.getContext())
                .load(mdImage)
                .asBitmap()
                .override(320, 480)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        imageView.setImageBitmap(bitmap);
                        ViewUtils.dismiss();
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        ViewUtils.dismiss();
                    }
                });
    }

    public static void loadImageByPhotoSID(ImageView imageView, int photoSID, boolean showPlaceHolder) {
        MDImage mdImage = new MDImage();
        mdImage.setPhotoSID(photoSID);
        GlideUtil.loadImageByMDImage(imageView, mdImage, showPlaceHolder);
    }

    public static void loadImageByPhotoSIDWithDialog(ImageView imageView, int photoSID) {
        MDImage mdImage = new MDImage();
        mdImage.setPhotoSID(photoSID);
        GlideUtil.loadImageByMDImageWithDialog(imageView, mdImage);
    }

    public static void loadImageByPhotoSIDWithDialogLimit(ImageView imageView, int photoSID) {
        MDImage mdImage = new MDImage();
        mdImage.setPhotoSID(photoSID);
        GlideUtil.loadImageByMDImageWithDialogLimit(imageView, mdImage);
    }

    public static void getImageOriginalSize(Context context, MDImage mdImage,
                                            SimpleTarget<Options> simpleTarget) {
        GenericRequestBuilder<MDImage, InputStream, Options, Size> SIZE_REQUEST = Glide // cache for effectiveness (re-use in lists for example) and readability at usage
                .with(context)
                .using(new MDGroundLoader(context), InputStream.class)
                .from(MDImage.class)
                .as(Options.class)
                .transcode(new OptionsSizeResourceTranscoder(), Size.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new BitmapSizeDecoder())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);

        SIZE_REQUEST
                .load(mdImage)
                .into(new SimpleTarget<Size>() { // Target.SIZE_ORIGINAL is hidden in ctor
                    @Override
                    public void onResourceReady(Size resource, GlideAnimation glideAnimation) {
                        KLog.e("图片的original size是: ", String.format(Locale.ROOT, "%dx%d", resource.width, resource.height));
                    }
                });
    }

    public static void getImageOriginalSize2(Context context, MDImage mdImage,
                                             SimpleTarget<Size> target) {
        GenericRequestBuilder<MDImage, InputStream, Options, Size> SIZE_REQUEST = Glide // cache for effectiveness (re-use in lists for example) and readability at usage
                .with(context)
                .using(new MDGroundLoader(context), InputStream.class)
                .from(MDImage.class)
                .as(Options.class)
                .transcode(new OptionsSizeResourceTranscoder(), Size.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new BitmapSizeDecoder())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);

        SIZE_REQUEST
                .load(mdImage)
                .into(target);
    }

    public static void getOriginalSizeBitmap(Context context, MDImage mdImage,
                                             SimpleTarget<OriginalSizeBitmap> simpleTarget) {
        GenericRequestBuilder<MDImage, InputStream, OriginalSizeBitmap, OriginalSizeBitmap> SIZE_REQUEST = Glide // cache for effectiveness (re-use in lists for example) and readability at usage
                .with(context)
                .using(new MDGroundLoader(context), InputStream.class)
                .from(MDImage.class)
                .as(OriginalSizeBitmap.class)
                .override(1024, 1024)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new OriginalSizeBitmapDecoder(context))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);

        SIZE_REQUEST
                .load(mdImage)
                .into(simpleTarget);
    }

    public static void getOriginalSizeBitmap2(Context context, MDImage mdImage,
                                              SimpleTarget<OriginalSizeBitmap> simpleTarget) {
        GenericRequestBuilder<MDImage, InputStream, OriginalSizeBitmap, OriginalSizeBitmap> SIZE_REQUEST = Glide // cache for effectiveness (re-use in lists for example) and readability at usage
                .with(context)
                .using(new MDGroundLoader(context), InputStream.class)
                .from(MDImage.class)
                .as(OriginalSizeBitmap.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new OriginalSizeBitmapDecoder2(context))
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);

        SIZE_REQUEST
                .load(mdImage)
                .into(simpleTarget);
    }

    public static void loadImageAsBitmap(MDImage mdImage, Target target) {
        Glide.with(MDGroundApplication.sInstance)
                .load(mdImage)
                .asBitmap()
                .atMost()
                .override(1024, 1024)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(target);
    }

    public static void loadImageAsLimitBitmap(Context context, MDImage mdImage, SimpleTarget<Bitmap> target) {
        Glide.with(MDGroundApplication.sInstance)
                .load(mdImage)
                .asBitmap()
                .override(50, 50)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(target);
    }

    public static void loadImageAsBitmapWithoutCache(MDImage mdImage, Target target) {
        Glide.with(MDGroundApplication.sInstance)
                .load(mdImage)
                .asBitmap()
                .atMost()
                .override(1024, 1024)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(target);
    }

    public static Bitmap loadImageAsBitmap(MDImage mdImage) {
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(MDGroundApplication.sInstance)
                    .load(mdImage)
                    .asBitmap()
                    .atMost()
                    .override(1024, 1024)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static OriginalSizeBitmap loadImageAsOriginalSizeBitmap(MDImage mdImage) {
        GenericRequestBuilder<MDImage, InputStream, OriginalSizeBitmap, OriginalSizeBitmap> SIZE_REQUEST = Glide // cache for effectiveness (re-use in lists for example) and readability at usage
                .with(MDGroundApplication.sInstance)
                .using(new MDGroundLoader(MDGroundApplication.sInstance), InputStream.class)
                .from(MDImage.class)
                .as(OriginalSizeBitmap.class)
                .override(1024, 1024)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new OriginalSizeBitmapDecoder(MDGroundApplication.sInstance))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);

        try {
            return SIZE_REQUEST
                    .load(mdImage)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void loadImageRotated(Context context,
                                        MDImage mdImage, float rotateAngle, Target target) {
        Glide.with(MDGroundApplication.sInstance)
                .load(mdImage)
                .asBitmap()
                .atMost()
                .override(300, 300) // 图片如果超过300 * 300, 则压缩
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new RotateTransformation(context, rotateAngle))
                .into(target);
    }

    public static long getFileSize(final File file) {
        if (file == null || !file.exists())
            return 0;
        if (!file.isDirectory())
            return file.length();
        final List<File> dirs = new LinkedList<File>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null || listFiles.length == 0)
                continue;
            for (final File child : listFiles) {
                result += child.length();
                if (child.isDirectory())
                    dirs.add(child);
            }
        }
        return result;
    }

    static class BitmapSizeDecoder implements ResourceDecoder<File, Options> {
        @Override
        public Resource<Options> decode(File source, int width, int height) throws IOException {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            KLog.e("source.getAbsolutePath() : " + source.getAbsolutePath());
            BitmapFactory.decodeFile(source.getAbsolutePath(), options);
            KLog.e("width : " + options.outWidth + "height : " + options.outHeight);
            return new SimpleResource<>(options);
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }

    static class OptionsSizeResourceTranscoder implements ResourceTranscoder<Options, Size> {
        @Override
        public Resource<Size> transcode(Resource<Options> toTranscode) {
            Options options = toTranscode.get();
            Size size = new Size();
            size.width = options.outWidth;
            size.height = options.outHeight;
            return new SimpleResource<>(size);
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }
}
