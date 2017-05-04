package com.hailan.HaiLanPrint.utils;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.models.Album;
import com.hailan.HaiLanPrint.models.MDImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dee on 15/11/19.
 */
public class LocalMediaLoader {
    // load type
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;

    private final static String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID};

    private final static String[] VIDEO_PROJECTION = {
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DURATION};

    private int type = TYPE_IMAGE;
    private FragmentActivity activity;

    public LocalMediaLoader(FragmentActivity activity, int type) {
        this.activity = activity;
        this.type = type;
    }

    public void loadAllImage(final LocalMediaLoadListener imageLoadListener) {
        activity.getSupportLoaderManager().initLoader(type, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoader = null;
                if (id == TYPE_IMAGE) {
                    cursorLoader = new CursorLoader(
                            activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            IMAGE_PROJECTION, MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                            new String[]{"image/jpeg", "image/png"}, IMAGE_PROJECTION[2] + " DESC");
                } else if (id == TYPE_VIDEO) {
                    cursorLoader = new CursorLoader(
                            activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            VIDEO_PROJECTION, null, null, VIDEO_PROJECTION[2] + " DESC");
                }
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                ArrayList<Album> imageFolders = new ArrayList<Album>();
                Album allImageFolder = new Album();
                List<MDImage> allImages = new ArrayList<MDImage>();

                if (data != null) {
                    int count = data.getCount();
                    if (count > 0) {
                        data.moveToFirst();
                        do {
                            String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                            // 如原图路径不存在或者路径存在但文件不存在,就结束当前循环
                            if (TextUtils.isEmpty(path) || !new File(path).exists() || !isImage(new File(path))) {
                                continue;
                            }
                            String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                            long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                            int duration = (type == TYPE_VIDEO ? data.getInt(data.getColumnIndexOrThrow(VIDEO_PROJECTION[4])) : 0);

                            MDImage image = new MDImage(path, dateTime, duration);

                            Album folder = getImageFolder(path,imageFolders);
                            Log.i("FolderName",folder.getName());

                            folder.getImages().add(image);
                            folder.setImageNum(folder.getImageNum() + 1);


                            allImages.add(image);
                            allImageFolder.setImageNum(allImageFolder.getImageNum()+1);
                        } while (data.moveToNext());

                        allImageFolder.setFirstImagePath(allImages.get(0).getImageLocalPath());
                        allImageFolder.setName(activity.getString(R.string.all_image));
                        allImageFolder.setImages(allImages);

                        imageFolders.add(allImageFolder);
                        sortFolder(imageFolders);
                        imageLoadListener.loadComplete(imageFolders);
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
            }
        });
    }



    public static boolean isImage(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != 0 && options.outHeight != 0;
    }

    private void sortFolder(List<Album> imageFolders) {
        // 文件夹按图片数量排序
        Collections.sort(imageFolders, new Comparator<Album>() {
            @Override
            public int compare(Album lhs, Album rhs) {
                if (lhs.getImages() == null || rhs.getImages() == null) {
                    return 0;
                }
                int lsize = lhs.getImageNum();
                int rsize = rhs.getImageNum();
                return lsize == rsize ? 0 : (lsize < rsize ? 1 : -1);
            }
        });
    }

    private Album getImageFolder(String path, List<Album> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        for (Album folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        Album newFolder = new Album();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        newFolder.setFirstImagePath(path);
        imageFolders.add(newFolder);
        return newFolder;
    }

    public interface LocalMediaLoadListener {
        void loadComplete(List<Album> albums);
    }

}
