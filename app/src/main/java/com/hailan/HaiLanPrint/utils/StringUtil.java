package com.hailan.HaiLanPrint.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.greendao.Location;
import com.hailan.HaiLanPrint.models.DeliveryAddress;
import com.hailan.HaiLanPrint.models.OrderWork;
import com.hailan.HaiLanPrint.models.WorkInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Created by yoghourt on 5/6/16.
 */
public class StringUtil {

    public static boolean isEmpty(String string) {
        if (string == null || "".equals(string) || TextUtils.isEmpty(string) || "[]".equals(string) || "null".equals(string) || "[null]".equals(string)) {
            return true;
        }
        return false;
    }

    public static String toYuanWithInteger(int amount) {
        return String.valueOf(amount / 100);
    }

    public static String toYuanWithoutUnit(float amount) {
        return String.format("%.02f", amount / 100);
    }

    public static String toYuanWithoutUnit(double amount) {
        return String.format("%.02f", amount / 100);
    }

    public static String toYuanWithUnit(float amount) {
        return String.format("%.02f", amount / 100) + "元";
    }

    public static String getCompleteAddress(DeliveryAddress deliveryAddress) {
        Location provinceLocation = MDGroundApplication.sDaoSession.getLocationDao().load(deliveryAddress.getProvinceID());
        Location cityLocation = MDGroundApplication.sDaoSession.getLocationDao().load(deliveryAddress.getCityID());
        Location countyLocation = MDGroundApplication.sDaoSession.getLocationDao().load(deliveryAddress.getDistrictID());

        String province = "";
        if (provinceLocation != null) {
            province = provinceLocation.getLocationName();
        }

        String city = "";
        if (provinceLocation != null) {
            city = cityLocation.getLocationName();
        }

        String county = "";
        if (countyLocation != null) {
            county = countyLocation.getLocationName();
        }

        String completeAddress = province + city + county + deliveryAddress.getStreet();

        return completeAddress;
    }

    public static String getProductName(OrderWork orderWork) {
        String showProductName = null;

        ProductType productType = ProductType.fromValue(orderWork.getTypeID());

        String typeName = orderWork.getTypeName();
        if (typeName == null) {
            typeName = "";
        }

        String typeTitle = orderWork.getTypeTitle();
        if (typeTitle == null) {
            typeTitle = "";
        }

        String workMaterial = orderWork.getWorkMaterial();
        if (workMaterial == null) {
            workMaterial = "";
        }

        String workFormat = orderWork.getWorkFormat();
        if (workFormat == null) {
            workFormat = "";
        }

        String workStyle = orderWork.getWorkStyle();
        if (workStyle == null) {
            workStyle = "";
        }

        String templateName = orderWork.getTemplateName();
        if (templateName == null) {
            templateName = "";
        }

        switch (productType) {
            case PrintPhoto:
                showProductName = typeName + " (" + typeTitle + " " + workMaterial + ")";
                break;
            case Postcard:
                showProductName = typeName;
                break;
            case MagazineAlbum:
            case ArtAlbum:
                showProductName = typeName + " (" + typeTitle + " " + orderWork.getPhotoCount() + "P)";
                break;
            case PictureFrame:
                showProductName = typeName + " (" + workFormat + " " + workStyle + ")";
                break;
            case Calendar:
                showProductName = typeName + " (" + typeTitle + ")";
                break;
            case PhoneShell:
                showProductName = typeName + " (" + templateName + " " + workMaterial + ")";
                break;
            case Poker:
                showProductName = typeName + " (" + typeTitle + ")";
                break;
            case Puzzle:
                showProductName = typeName;
                break;
            case MagicCup:
                showProductName = typeName + " (" + typeTitle + ")";
                break;
            case LOMOCard:
                showProductName = typeName + " (" + typeTitle + ")";
                break;
            case Engraving:
                showProductName = workMaterial + typeName;
                break;
        }
        return showProductName;
    }

    public static String getProductName(WorkInfo orderWork) {
        String showProductName = null;

        ProductType productType = ProductType.fromValue(orderWork.getTypeID());

        String typeName = orderWork.getTypeName();
        if (typeName == null) {
            typeName = "";
        }

        String typeTitle = orderWork.getTypeTitle();
        if (typeTitle == null) {
            typeTitle = "";
        }

        String workMaterial = orderWork.getWorkMaterial();
        if (workMaterial == null) {
            workMaterial = "";
        }

        String workFormat = orderWork.getWorkFormat();
        if (workFormat == null) {
            workFormat = "";
        }

        String workStyle = orderWork.getWorkStyle();
        if (workStyle == null) {
            workStyle = "";
        }

//        String templateName = orderWork.getTemplateName();
//        if (templateName == null) {
//            templateName = "";
//        }

        switch (productType) {
            case PrintPhoto:
                showProductName = typeName + " (" + typeTitle + " " + workMaterial + ")";
                break;
            case Postcard:
                showProductName = typeName;
                break;
            case MagazineAlbum:
            case ArtAlbum:
                showProductName = typeName + " (" + typeTitle + " " + orderWork.getPhotoCount() + "P)";
                break;
            case PictureFrame:
                showProductName = typeName + " (" + workFormat + " " + workStyle + ")";
                break;
            case Calendar:
                showProductName = typeName + " (" + typeTitle + ")";
                break;
            case PhoneShell:
                showProductName = typeName + " (" + workMaterial + ")";
                break;
            case Poker:
                showProductName = typeName + " (" + typeTitle + ")";
                break;
            case Puzzle:
                showProductName = typeName;
                break;
            case MagicCup:
                showProductName = typeName + " (" + typeTitle + ")";
                break;
            case LOMOCard:
                showProductName = typeName + " (" + typeTitle + ")";
                break;
            case Engraving:
                showProductName = workMaterial + typeName;
                break;
        }
        return showProductName;
    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = MDGroundApplication.sInstance.getPackageManager();
            PackageInfo info = manager.getPackageInfo(MDGroundApplication.sInstance.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取版本号（数字）
     *
     * @return 当前应用的版本号（数字）
     */
    public static int getVersionCode() {
        try {
            PackageManager manager = MDGroundApplication.sInstance.getPackageManager();
            PackageInfo info = manager.getPackageInfo(MDGroundApplication.sInstance.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static <T> T getInstanceByJsonString(String jsonString, TypeToken<T> type) {
        if (type == null) {
            return null;
        }
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        if (jsonString == null) {
            return null;
        } else {
            return gson.fromJson(jsonString, type.getType());
        }
    }

    public static <T> T getInstanceByJsonString(String jsonString, Class<? extends T> clazz) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        if (jsonString == null) {
            return null;
        } else {
            return gson.fromJson(jsonString, clazz);
        }
    }

    /**
     * 判断两个string是否相等
     */
    public static boolean checkEquels(Object strObj0, Object strObj1) {
        String str0 = strObj0 + "";
        String str1 = strObj1 + "";
        if (str0.equals(str1)) {
            return true;
        }
        return false;
    }
}
