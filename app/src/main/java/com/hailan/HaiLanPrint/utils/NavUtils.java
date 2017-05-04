package com.hailan.HaiLanPrint.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.IntentCompat;

import com.hailan.HaiLanPrint.activity.cloudphotos.CloudDetailActivity;
import com.hailan.HaiLanPrint.activity.login.LoginActivity;
import com.hailan.HaiLanPrint.activity.main.MainActivity;
import com.hailan.HaiLanPrint.activity.payment.PaymentPreviewActivity;
import com.hailan.HaiLanPrint.activity.photoprint.PrintPhotoChoosePaperNumActivity;
import com.hailan.HaiLanPrint.activity.pictureframe.PictureFrameEditActivity;
import com.hailan.HaiLanPrint.activity.selectimage.SelectAlbumBeforeEditActivity;
import com.hailan.HaiLanPrint.activity.templateedit.GlobalTemplateEditActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.WorkPhoto;
import com.hailan.HaiLanPrint.models.WorkPhotoEdit;

/**
 * Created by yoghourt on 5/12/16.
 */
public class NavUtils {

    private static int sLoadCompleteCount = 0;

    public static void toLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void toMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void toCloudDetailActivity(Context context, MDImage image) {
        Intent intent = new Intent(context, CloudDetailActivity.class);
        intent.putExtra(Constants.KEY_CLOUD_IMAGE, image);
        context.startActivity(intent);
    }

    public static void toSelectAlbumActivity(Context context) {
        Intent intent = new Intent(context, SelectAlbumBeforeEditActivity.class);
        context.startActivity(intent);
    }

    public static void toPhotoEditActivity(final Context context) {
        ViewUtils.loading(context);
        if (SelectImageUtils.sAlreadySelectImage.size() == 0) {
            return;
        }

        boolean hasTemplate = true;
        final Intent intent = new Intent();
        switch (MDGroundApplication.sInstance.getChoosedProductType()) {
            // pager 1
            case PrintPhoto:
                hasTemplate = false;
                intent.setClass(context, PrintPhotoChoosePaperNumActivity.class);
                break;
            case Postcard:
                intent.setClass(context, GlobalTemplateEditActivity.class);
//                intent.setClass(context, PostcardEditActivity.class);
                break;
            case MagazineAlbum:
                intent.setClass(context, GlobalTemplateEditActivity.class);
//                intent.setClass(context, MagazineEditActivity.class);
                break;
            case ArtAlbum:
                intent.setClass(context, GlobalTemplateEditActivity.class);
//                intent.setClass(context, ArtAlbumEditActivity.class);
                break;
            case PictureFrame:
                intent.setClass(context, PictureFrameEditActivity.class);
                break;
            case Calendar:
                intent.setClass(context, GlobalTemplateEditActivity.class);
//                intent.setClass(context, CalendarEditActivity.class);
                break;
            case PhoneShell:
//                intent.setClass(context, PhoneShellEditActivity.class);
                intent.setClass(context, GlobalTemplateEditActivity.class);
                break;
            case Poker:
//                intent.setClass(context, PokerEditActivity.class);
                intent.setClass(context, GlobalTemplateEditActivity.class);
                break;
            case Puzzle:
//                intent.setClass(context, PuzzleEditActivity.class);
                intent.setClass(context, GlobalTemplateEditActivity.class);
                break;
            case MagicCup:
//                intent.setClass(context, MagicCupPhotoEditActivity.class);
                intent.setClass(context, GlobalTemplateEditActivity.class);
                break;
            case LOMOCard:
//                intent.setClass(context, LomoCardEditActivity.class);
                intent.setClass(context, GlobalTemplateEditActivity.class);
                break;
            case Engraving:
                intent.setClass(context, GlobalTemplateEditActivity.class);
//                hasTemplate = false;
//                intent.setClass(context, EngravingChoosePaperNumActivity.class);
                break;
        }

        SelectImageUtils.originalSelectImageSize = SelectImageUtils.sAlreadySelectImage.size();

        // 将所有选中图片的数量设为1
        for (MDImage mdImage : SelectImageUtils.sAlreadySelectImage) {
            mdImage.setPhotoCount(1);
        }

        if (hasTemplate) {
            // 先加载全部模版图片,再进入编辑界面
            sLoadCompleteCount = 0;

            if (SelectImageUtils.sTemplateImage.size() == 0) {
                SelectImageUtils.sTemplateImage.add(new MDImage());
            }

//            for (MDImage mdImage : sTemplateImage) {
//                if (mdImage.hasPhotoSID()) {
//                    Glide.with(context)
//                            .load(mdImage)
//                            .downloadOnly(new SimpleTarget<File>() {
//                                @Override
//                                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
//                                    sLoadCompleteCount++;
//                                    downloadTemplateSuccessfully(context, intent);
//                                }
//                            });
//                } else {
//                    sLoadCompleteCount++;
//                    downloadTemplateSuccessfully(context, intent);
//                }
//            }

            // 服务器返回的模板数量少于pagecount
            if (SelectImageUtils.sTemplateImage.size() < MDGroundApplication.sInstance.getChoosedTemplate().getPageCount()) {
                int difference = MDGroundApplication.sInstance.getChoosedTemplate().getPageCount() - SelectImageUtils.sTemplateImage.size();
                for (int i = 0; i < difference; i++) {
//                    sTemplateImage.add(new MDImage());
                }
            }

            // 需要选择图片的总数
            int allModuleImageCount = SelectImageUtils.getMaxUserSelectImageNum();

            // 将用户选择的图片放进模块类的MDImage里面
//            int count = 0;
//            for (MDImage mdImage : sTemplateImage) {
//                for (PhotoTemplateAttachFrame photoTemplateAttachFrame : mdImage.getPhotoTemplateAttachFrameList()) {
//                    if (count < SelectImageUtils.sAlreadySelectImage.size()) {
//                        photoTemplateAttachFrame.setUserSelectImage(SelectImageUtils.sAlreadySelectImage.get(count));
//                        count++;
//                    } else {
//                        photoTemplateAttachFrame.setUserSelectImage(new MDImage());
//                    }
//                }
//            }

            // 选择的图片数量小于模板的数量
            if (SelectImageUtils.sAlreadySelectImage.size() < allModuleImageCount) {
                int difference = allModuleImageCount - SelectImageUtils.sAlreadySelectImage.size();

                for (int i = 0; i < difference; i++) {
                    SelectImageUtils.sAlreadySelectImage.add(new MDImage());
                }
            }

            for (MDImage mdImage : SelectImageUtils.sAlreadySelectImage) {
                WorkPhotoEdit workPhotoEdit = mdImage.getWorkPhotoEdit();

                if (workPhotoEdit == null) {
                    workPhotoEdit = new WorkPhotoEdit();
                    mdImage.setWorkPhotoEdit(workPhotoEdit);
                }
            }

            // 将模版的PhotoID和PhotoSID赋值给MdImage
            for (int i = 0; i < SelectImageUtils.sTemplateImage.size(); i++) {
                MDImage template = SelectImageUtils.sTemplateImage.get(i);
//                MDImage selectImage = SelectImageUtils.sAlreadySelectImage.get(i);

                WorkPhoto workPhoto = template.getWorkPhoto();

                if (workPhoto == null) {
                    workPhoto = new WorkPhoto();
                    workPhoto.setPhotoIndex(i + 1);
                    workPhoto.setPhoto1ID(template.getPhotoID());
                    workPhoto.setPhoto1SID(template.getPhotoSID());
                    workPhoto.setPhoto2ID(template.getPhotoID()); // 合成图片默认等于模板图片
                    workPhoto.setPhoto2SID(template.getPhotoSID());
                    workPhoto.setTemplatePID(template.getPhotoID());
                    workPhoto.setTemplatePSID(template.getPhotoSID());
                    template.setWorkPhoto(workPhoto);
                }
            }
            ViewUtils.dismiss();
            context.startActivity(intent);
        } else {
            ViewUtils.dismiss();
            context.startActivity(intent);
        }
    }

    private static void downloadTemplateSuccessfully(Context context, Intent intent) {
        if (sLoadCompleteCount == SelectImageUtils.sTemplateImage.size()) {
            // 服务器返回的模板数量少于pagecount
            if (SelectImageUtils.sTemplateImage.size() < MDGroundApplication.sInstance.getChoosedTemplate().getPageCount()) {
                int difference = MDGroundApplication.sInstance.getChoosedTemplate().getPageCount() - SelectImageUtils.sTemplateImage.size();
                for (int i = 0; i < difference; i++) {
                    SelectImageUtils.sTemplateImage.add(new MDImage());
                }
            }
            // 选择的图片数量小于模板的数量
            if (SelectImageUtils.sAlreadySelectImage.size() < SelectImageUtils.sTemplateImage.size()) {
                int difference = SelectImageUtils.sTemplateImage.size() - SelectImageUtils.sAlreadySelectImage.size();

                for (int i = 0; i < difference; i++) {
                    SelectImageUtils.sAlreadySelectImage.add(new MDImage());
                }
            }

            // 将模版的PID和PSID赋值给MdImage
            for (int i = 0; i < SelectImageUtils.sTemplateImage.size(); i++) {
                MDImage template = SelectImageUtils.sTemplateImage.get(i);
                MDImage selectImage = SelectImageUtils.sAlreadySelectImage.get(i);

                WorkPhoto workPhoto = selectImage.getWorkPhoto();

                if (workPhoto == null) {
                    workPhoto = new WorkPhoto();
                    workPhoto.setPhotoIndex(i + 1);
                    workPhoto.setPhoto1ID(selectImage.getPhotoID());
                    workPhoto.setPhoto1SID(selectImage.getPhotoSID());
                    workPhoto.setPhoto2ID(template.getPhotoID()); // 合成图片默认等于模板图片
                    workPhoto.setPhoto2SID(template.getPhotoSID());
                    workPhoto.setTemplatePID(template.getPhotoID());
                    workPhoto.setTemplatePSID(template.getPhotoSID());
                    workPhoto.setZoomSize(100);
                    selectImage.setWorkPhoto(workPhoto);
                }
            }
            ViewUtils.dismiss();
            context.startActivity(intent);
        }
    }

    public static void toPaymentPreviewActivity(Context context) {
        Intent intent = new Intent(context, PaymentPreviewActivity.class);
        context.startActivity(intent);
    }

}
