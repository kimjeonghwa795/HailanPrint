package com.hailan.HaiLanPrint.utils;

import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.models.MDImage;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by yoghourt on 5/16/16.
 */

public class SelectImageUtils {

    public static int originalSelectImageSize = 0;

    public static ArrayList<MDImage> sAlreadySelectImage = new ArrayList<>();

    public static ArrayList<MDImage> sTemplateImage = new ArrayList<>();

    private static SelectImageUtils sIntance = new SelectImageUtils();

    public interface UploadAllImageSuccessListener {
        public void uploadAllImageSuccess();
    }

    private SelectImageUtils() {

    }

    public static SelectImageUtils getInstance() {
        if (sIntance == null) {
            sIntance = new SelectImageUtils();
        }
        return sIntance;
    }

    public static void addImage(MDImage mdImage) {
        for (MDImage item : sAlreadySelectImage) {
            if (isSameImage(item, mdImage)) {
                return;
            }
        }
        sAlreadySelectImage.add(mdImage);
    }

    public static void removeImage(MDImage mdImage) {
        Iterator<MDImage> iterator = sAlreadySelectImage.iterator();
        while (iterator.hasNext()) {

            MDImage item = iterator.next();

            if (isSameImage(item, mdImage)) {
                iterator.remove();
                break;
            }
        }
    }

    public static int getMaxUserSelectImageNum() {
        int maxNum = 0;

        // 杂志册,艺术册,个性月历 这三个功能块有定位块
        switch (MDGroundApplication.sInstance.getChoosedProductType()) {
            case ArtAlbum:
            case Calendar:
                for (MDImage mdImage : sTemplateImage) {
                    if (mdImage.getPhotoTemplateAttachFrameList() != null) {
                        maxNum += mdImage.getPhotoTemplateAttachFrameList().size();
                    }
                }
                break;
            default:
                // 其他没有定位块的功能块,等于服务器返回的pageCount
                maxNum = MDGroundApplication.sInstance.getChoosedTemplate().getPhotoCount();
        }

        return maxNum;
    }

    public static int getPageIndexBySelectPhotoIndex(int selectPhotoInex) {
        int count = 0;

        if (TemplateUtils.isTemplateHasModules()) {
            for (int i = 0; i < SelectImageUtils.sTemplateImage.size(); i++) {
                count += SelectImageUtils.sTemplateImage.get(i).getPhotoTemplateAttachFrameList().size();

                if (selectPhotoInex < count) {
                    return i;
                }
            }
        } else {
            count = selectPhotoInex;
        }

        return count;
    }

    public static MDImage getMdImageByPageIndexAndModuleIndex(int pageIndex, int moduleIndex) {
        int count = 0;
        if (TemplateUtils.isTemplateHasModules()) {
            for (int i = 0; i < SelectImageUtils.sTemplateImage.size(); i++) {
                if (i < pageIndex) {
                    count += SelectImageUtils.sTemplateImage.get(i).getPhotoTemplateAttachFrameList().size();
                } else {
                    count += moduleIndex;
                    break;
                }
            }
        } else {
            count = pageIndex;
        }

        if (count < SelectImageUtils.sAlreadySelectImage.size()) {
            return SelectImageUtils.sAlreadySelectImage.get(count);
        }
        return null;
    }

    public static boolean isSameImage(MDImage originalImage, MDImage compareImage) {
        if ((originalImage.getAutoID() != 0 && originalImage.getAutoID() == compareImage.getAutoID())) { // 同一张网络图片
            return true;
        }
        if (((originalImage.getImageLocalPath() != null && compareImage.getImageLocalPath() != null)
                && originalImage.getImageLocalPath().equals(compareImage.getImageLocalPath()))) {       // 同一张本地图片
            return true;
        }
        return false;
    }

    public static int getMaxSelectImageNum(ProductType productType) {
        switch (productType) {
            case PrintPhoto:
                return Constants.PRINT_PHOTO_MAX_SELECT_IMAGE_NUM;
            case PictureFrame:
                return Constants.PICTURE_FRAME_MAX_SELECT_IMAGE_NUM;
            case MagicCup:
                return Constants.MAGIC_CUP_MAX_SELECT_IMAGE_NUM;
            case Puzzle:
                return Constants.PUZZLEL_MAX_SELECT_IMAGE_NUM;
            case PhoneShell:
                return Constants.PHONE_SHELL_MAX_SELECT_IMAGE_NUM;
            case Engraving:
                return Constants.ENGRAVING_MAX_SELECT_IMAGE_NUM;
        }
        return 0;
    }

    public static int getPrintPhotoOrEngravingOrderCount() {
        int count = 0;
        for (MDImage mdImage : sAlreadySelectImage) {
            count += mdImage.getPhotoCount();
        }
        return count;
    }
}
