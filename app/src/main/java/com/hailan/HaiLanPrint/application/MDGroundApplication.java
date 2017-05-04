package com.hailan.HaiLanPrint.application;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.greendao.DaoMaster;
import com.hailan.HaiLanPrint.greendao.DaoSession;
import com.hailan.HaiLanPrint.greendao.DatabaseOpenHelper;
import com.hailan.HaiLanPrint.models.Measurement;
import com.hailan.HaiLanPrint.models.PhotoTypeExplain;
import com.hailan.HaiLanPrint.models.Template;
import com.hailan.HaiLanPrint.models.User;
import com.hailan.HaiLanPrint.utils.FileUtils;
import com.hailan.HaiLanPrint.utils.OrderUtils;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by yoghourt on 5/6/16.
 */
public class MDGroundApplication extends Application {

    /**
     * 对外提供整个应用生命周期的Context
     **/
    public static MDGroundApplication sInstance;

    public static DaoSession sDaoSession;

    public static OrderUtils sOrderutUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        initDataBase();

        ShareSDK.initSDK(this);

        initJPush();

//        initExceptionHandler();
    }

    /**
     * 初始化极光推送
     */
    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());
    }

    private void initDataBase() {
        DatabaseOpenHelper helper = new DatabaseOpenHelper(this, Constants.DATABASE_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        sDaoSession = daoMaster.newSession();
    }

    private void initExceptionHandler() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    public void resetData() {
        setChoosedProductType(null);
        setChoosedMeasurement(null);
        setChoosedTemplate(null);
    }

    public boolean getAutoLogin(){
        return (boolean) FileUtils.getObject(Constants.KEY_AUTO_LOGIN);
    }

    public User getLoginUser() {
        return (User) FileUtils.getObject(Constants.KEY_ALREADY_LOGIN_USER);
    }

    public void setLoginUser(User loginUser) {
        FileUtils.setObject(Constants.KEY_ALREADY_LOGIN_USER, loginUser);
    }

    public ProductType getChoosedProductType() {
        return (ProductType) FileUtils.getObject(Constants.KEY_ALREADY_CHOOSED_PRODUCT_TYPE);
    }

    public void setChoosedProductType(ProductType choosedProductType) {
        FileUtils.setObject(Constants.KEY_ALREADY_CHOOSED_PRODUCT_TYPE, choosedProductType);
    }

    public Measurement getChoosedMeasurement() {
        return (Measurement) FileUtils.getObject(Constants.KEY_ALREADY_CHOOSED_MEASUREMENT);
    }

    public void setChoosedMeasurement(Measurement choosedMeasurement) {
        FileUtils.setObject(Constants.KEY_ALREADY_CHOOSED_MEASUREMENT, choosedMeasurement);
    }

    public Template getChoosedTemplate() {
        return (Template) FileUtils.getObject(Constants.KEY_ALREADY_CHOOSED_TEMPLATE);
    }

    public void setChoosedTemplate(Template choosedTemplate) {
        FileUtils.setObject(Constants.KEY_ALREADY_CHOOSED_TEMPLATE, choosedTemplate);
    }

    public ArrayList<PhotoTypeExplain> getPhotoTypeExplainArrayList() {
        return (ArrayList<PhotoTypeExplain>) FileUtils.getObject(Constants.KEY_PHOTO_TYPE_EXPLAIN_ARRAYLIST);
    }

    public void setPhotoTypeExplainArrayList(ArrayList<PhotoTypeExplain> photoTypeExplainArrayList) {
        FileUtils.setObject(Constants.KEY_PHOTO_TYPE_EXPLAIN_ARRAYLIST, photoTypeExplainArrayList);
    }
}
