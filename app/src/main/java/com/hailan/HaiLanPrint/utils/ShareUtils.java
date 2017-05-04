package com.hailan.HaiLanPrint.utils;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.socks.library.KLog;

import java.net.URLEncoder;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by yoghourt on 6/24/16.
 */

public class ShareUtils {

    public static String createShareURL(String workId, String userId) {
        String shareUrl = null;
        String workID = null;
        String userID = null;
        try {
            workID = EncryptUtil.encrypt(workId);
            userID = EncryptUtil.encrypt(userId);
            workID = URLEncoder.encode(workID, "UTF-8");
            userID = URLEncoder.encode(userID, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!"".equals(workID) && !"".equals(userID)) {
            shareUrl = Constants.HOST + "ShareWorkPhoto.aspx?workId=" + workID + "&userId=" + userID;
        }

        String InvitationCode = PreferenceUtils.getPrefString(Constants.KEY_INVITATIONCODE, "");
        if (!"".equals(InvitationCode)) {
            shareUrl += "&InvitationCode=" + InvitationCode;
        }

        KLog.e(shareUrl);
        return shareUrl;
    }

    // 生成图片分享
    public static Platform.ShareParams initImageShareParams(Context context, String imagePath) {
        Platform.ShareParams shareParams = new Platform.ShareParams();
        shareParams.setTitle(context.getString(R.string.app_name));
        shareParams.setText(context.getString(R.string.app_name));
        shareParams.setImagePath(imagePath);
        shareParams.setShareType(Platform.SHARE_TEXT);
        shareParams.setShareType(Platform.SHARE_IMAGE);
        return shareParams;
    }

    //生成分享链接
    public static Platform.ShareParams initURLShareParams(Context context, String url) {
        Platform.ShareParams shareParams = new Platform.ShareParams();
        shareParams.setTitle(context.getString(R.string.app_name));
        shareParams.setText(context.getString(R.string.production_share, MDGroundApplication.sInstance.getLoginUser().getUserNickName()));
        shareParams.setTitleUrl(url);
        shareParams.setUrl(url);
        shareParams.setSite(context.getString(R.string.app_name));
        shareParams.setSiteUrl(url);
        shareParams.setShareType(Platform.SHARE_TEXT);
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        return shareParams;
    }

    /**
     * QQ分享
     */
    public static Platform.ShareParams getQQShareParams(Context context, String url, String imagePath) {
        String invitationContent = getInvitationContent();
        Platform.ShareParams shareParams = new Platform.ShareParams();
        if (!"".equals(url)) {//url分享
            shareParams.setTitle(context.getString(R.string.app_name));
            shareParams.setText(context.getString(R.string.production_share, MDGroundApplication.sInstance.getLoginUser().getUserNickName()) + invitationContent);
            shareParams.setTitleUrl(url);
            shareParams.setUrl(url);
            shareParams.setSite(context.getString(R.string.app_name));
            shareParams.setSiteUrl(url);

        } else {//图片分享
            shareParams.setImagePath(imagePath);
        }
        return shareParams;
    }

    /**
     * 微博分享
     */
    public static Platform.ShareParams getWeiBoShareParams(Context context, String url, String imagePath) {
        String invitationContent = getInvitationContent();
        Platform.ShareParams shareParams = new Platform.ShareParams();
        if (!"".equals(url)) {
            shareParams.setText(url + " " + context.getString(R.string.production_share,
                    MDGroundApplication.sInstance.getLoginUser().getUserNickName()) + " --- 来自" + context.getString(R.string.app_name) + invitationContent);

        } else {
            shareParams.setText(context.getString(R.string.production_share, MDGroundApplication.sInstance.getLoginUser().getUserNickName())
                    + " --- 来自" + context.getString(R.string.app_name) + invitationContent);
            shareParams.setImagePath(imagePath);
        }
        return shareParams;
    }

    /**
     * 微信分享
     */
    public static Platform.ShareParams getWechatShareParams(Context context, String url, String imagePath) {
        String invitationContent = getInvitationContent();
        Platform.ShareParams shareParams = new Platform.ShareParams();
        if (!"".equals(url)) {//url分享
            shareParams.setTitle(context.getString(R.string.app_name));
            shareParams.setText(context.getString(R.string.production_share, MDGroundApplication.sInstance.getLoginUser().getUserNickName()) + invitationContent);
            shareParams.setUrl(url);
            shareParams.setImageData(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon));
            shareParams.setShareType(Platform.SHARE_WEBPAGE);

        } else {//图片分享
            shareParams.setTitle(context.getString(R.string.app_name));
            shareParams.setText(context.getString(R.string.app_name) + invitationContent);
            shareParams.setImagePath(imagePath);
            shareParams.setShareType(Platform.SHARE_IMAGE);
        }
        return shareParams;
    }

    public static void shareToWechat(Context context, Platform.ShareParams shareParams) {
        if (shareParams != null) {
            Platform wechatPlatform = ShareSDK.getPlatform(Wechat.NAME);
            wechatPlatform.setPlatformActionListener(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                }

                @Override
                public void onCancel(Platform platform, int i) {
                }
            });
            wechatPlatform.share(shareParams);
        }
    }

    public static void shareToWechatMoment(Context context, Platform.ShareParams shareParams) {
        if (shareParams != null) {
            Platform wechatPlatform = ShareSDK.getPlatform(WechatMoments.NAME);
            wechatPlatform.setPlatformActionListener(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                }

                @Override
                public void onCancel(Platform platform, int i) {
                }
            });
            wechatPlatform.share(shareParams);
        }
    }

    public static void shareToQQ(Context context, Platform.ShareParams shareParams) {
        if (shareParams != null) {
            Platform qqPlatform = ShareSDK.getPlatform(QQ.NAME);
            qqPlatform.setPlatformActionListener(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                }

                @Override
                public void onCancel(Platform platform, int i) {
                }
            });
            qqPlatform.share(shareParams);
        }
    }

    public static void shareToWeibo(Context context, Platform.ShareParams shareParams) {
        if (shareParams != null) {
            Platform weiboPlatform = ShareSDK.getPlatform(SinaWeibo.NAME);
            weiboPlatform.setPlatformActionListener(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                }

                @Override
                public void onCancel(Platform platform, int i) {
                }
            });
            weiboPlatform.share(shareParams);
        }
    }

    /**
     * 获取邀请码
     *
     * @return
     */
    private static String getInvitationContent() {
        String invitationContent = "";
        String InvitationCode = PreferenceUtils.getPrefString(Constants.KEY_INVITATIONCODE, "");
        if (!"".equals(InvitationCode)) {
            invitationContent += " 邀请码：" + InvitationCode;
        }
//        return invitationContent;
        return "";
    }

}
