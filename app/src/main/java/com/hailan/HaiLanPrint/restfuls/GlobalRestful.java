package com.hailan.HaiLanPrint.restfuls;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.enumobject.OrderStatus;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.enumobject.ThirdPartyLoginType;
import com.hailan.HaiLanPrint.enumobject.restfuls.BusinessType;
import com.hailan.HaiLanPrint.models.DeliveryAddress;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.OrderInfo;
import com.hailan.HaiLanPrint.models.OrderWork;
import com.hailan.HaiLanPrint.models.OrderWorkPhoto;
import com.hailan.HaiLanPrint.models.User;
import com.hailan.HaiLanPrint.models.WorkInfo;
import com.hailan.HaiLanPrint.models.WorkPhoto;
import com.hailan.HaiLanPrint.restfuls.bean.Device;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import retrofit2.Callback;

/**
 * Created by yoghourt on 5/6/16.
 */
public class GlobalRestful extends BaseRestful {

    private static GlobalRestful sIntance = new GlobalRestful();

    @Override
    protected BusinessType getBusinessType() {
        return BusinessType.Global;
    }

    @Override
    protected String getHost() {
        return Constants.HOST;
    }

    private GlobalRestful() {
    }

    public static GlobalRestful getInstance() {
        if (sIntance == null) {
            sIntance = new GlobalRestful();
        }
        return sIntance;
    }

    // 用户登录
    public void LoginUser(String loginID, String pwd, Callback<ResponseData> callback) {
        Device device = DeviceUtil.getDeviceInfo(MDGroundApplication.sInstance);

//        device.setDeviceToken(XGPushConfig.getToken(MDGroundApplication.sInstance)); // 信鸽的token, XGPushConfig.getToken(this);
        device.setDeviceToken(JPushInterface.getRegistrationID(MDGroundApplication.sInstance));//极光的token
//        device.setDeviceToken("abc");
        device.setDeviceID(DeviceUtil.getDeviceId());

        JsonObject obj = new JsonObject();
        obj.addProperty("LoginID", loginID);
        obj.addProperty("Pwd", pwd);
        obj.add("Device", new Gson().toJsonTree(device));

        asynchronousPost("LoginUser", obj, callback);
    }

    // 刷新用户
    public void RefreshUser(Callback<ResponseData> callback) {
        asynchronousPost("RefreshUser", null, callback);
    }

    // 退出登录
    public void LogoutUser(Callback<ResponseData> callback) {
        asynchronousPost("LogoutUser", null, callback);
    }

    public void LoginUserByThirdParty(ThirdPartyLoginType loginType, String openID,
                                      String PhotoUrl, String UserNickName,
                                      String UserName, Callback<ResponseData> callback) {
        Device device = DeviceUtil.getDeviceInfo(MDGroundApplication.sInstance);
//        device.setDeviceToken("abc");   // 信鸽的token, XGPushConfig.getToken(this);
        device.setDeviceToken(JPushInterface.getRegistrationID(MDGroundApplication.sInstance));//极光的token
        device.setDeviceID(DeviceUtil.getDeviceId());

        JsonObject obj = new JsonObject();
        switch (loginType) {
            case Wechat:
                obj.addProperty("WXOpenID", openID);
                break;
            case QQ:
                obj.addProperty("QQOpenID", openID);
                break;
            case Weibo:
                obj.addProperty("WBUID", openID);
                break;
        }
        obj.addProperty("PhotoUrl", PhotoUrl);
        obj.addProperty("UserNickName", UserNickName);
        obj.addProperty("UserName", UserName);
        obj.add("Device", new Gson().toJsonTree(device));

        asynchronousPost("LoginUserByThirdParty", obj, callback);
    }

    public void CheckUserPhone(String phone, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("Phone", phone);

        asynchronousPost("CheckUserPhone", obj, callback);
    }

    // 用户注册
    public void RegisterUser(User user, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("Phone", user.getPhone());
        obj.addProperty("Pwd", user.getPassword());
        obj.addProperty("UserName", user.getUserName());
        obj.addProperty("KidName1", user.getKidName1());
        obj.addProperty("KidDOB1", user.getKidDOB1());
        obj.addProperty("KidSchool1", user.getKidSchool1());
        obj.addProperty("KidClass1", user.getKidClass1());
        obj.addProperty("KidName2", user.getKidName2());
        obj.addProperty("KidDOB2", user.getKidDOB2());
        obj.addProperty("KidSchool2", user.getKidSchool2());
        obj.addProperty("KidClass2", user.getKidClass2());
        obj.addProperty("InvitationCode", user.getInvitationCode());
        asynchronousPost("RegisterUser", obj, callback);
    }

    // 找回密码
    public void ChangeUserPassword(String phone, String password, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("Phone", phone);
        obj.addProperty("Password", password);

        asynchronousPost("ChangeUserPassword", obj, callback);
    }

    // 获取云相册统计接口
    public void GetCloudPhotoCount(Callback<ResponseData> callback) {
        asynchronousPost("GetCloudPhotoCount", null, callback);
    }

    // 获取共享相册的类别
    public void GetCloudPhotoCategoryList(Callback<ResponseData> callback) {
        asynchronousPost("GetCloudPhotoCategoryList", null, callback);
    }

    // 获取云相册所有图片
    public void GetCloudPhoto(int PageIndex, boolean IsShared, int categoryID, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("PageIndex", PageIndex);
        obj.addProperty("IsShared", IsShared);
        obj.addProperty("CategoryID", categoryID);

        asynchronousPost("GetCloudPhoto", obj, callback);
    }

    // 删除个人云相册接口
    public void DeleteCloudPhoto(ArrayList<Integer> AutoIDList, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.add("AutoIDList", new Gson().toJsonTree(AutoIDList));

        asynchronousPost("DeleteCloudPhoto", obj, callback);
    }

    //转存到个人相册
    public void TransferCloudPhoto(boolean IsShared, ArrayList<Integer> AutoIDList, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("IsShared", IsShared);
        obj.add("AutoIDList", new Gson().toJsonTree(AutoIDList));

        asynchronousPost("TransferCloudPhoto", obj, callback);
    }

    // 获取获取产品类型信息以及规格明细
    public void GetPhotoType(ProductType productType, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("ProductType", productType.value());

        asynchronousPost("GetPhotoType", obj, callback);
    }

    // 获取首页轮播图片列表
    public void GetBannerPhotoList(Callback<ResponseData> callback) {
        asynchronousPost("GetBannerPhotoList", null, callback);
    }

    // 用于获取所有类型图片说明/Banner图/介绍页
    public void GetPhotoTypeExplainList(Callback<ResponseData> callback) {
        asynchronousPost("GetPhotoTypeExplainList", null, callback);
    }

    // 保存订单接口
    public void SaveOrder(ProductType productType, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("TypeID", productType.value());

        asynchronousPost("SaveOrder", obj, callback);
    }

    public void SaveUserWork(WorkInfo workInfo, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.add("WorkInfo", new Gson().toJsonTree(workInfo));

        asynchronousPost("SaveUserWork", obj, callback);
    }

    public void SaveUserWorkPhotoList(List<WorkPhoto> workPhotoList, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.add("WorkPhotoList", new Gson().toJsonTree(workPhotoList));

        asynchronousPost("SaveUserWorkPhotoList", obj, callback);
    }

    public void SavePhotoCloudList(List<MDImage> photoCloudList, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.add("PhotoCloudList", new Gson().toJsonTree(photoCloudList));

        asynchronousPost("SavePhotoCloudList", obj, callback);
    }

    // 保存订单作品接口
    public void SaveOrderWork(OrderWork orderWork, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.add("OrderWork", new Gson().toJsonTree(orderWork));

        asynchronousPost("SaveOrderWork", obj, callback);
    }

    // 保存作品与相片关系接口
    public void SaveOrderPhotoList(List<OrderWorkPhoto> OrderWorkPhotoList, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.add("OrderWorkPhotoList", new Gson().toJsonTree(OrderWorkPhotoList));

        asynchronousPost("SaveOrderPhotoList", obj, callback);
    }

    public void GetUserAddressList(Callback<ResponseData> callback) {
        asynchronousPost("GetUserAddressList", null, callback);
    }

    public void SaveUserAddress(DeliveryAddress deliveryAddress, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.add("UserAddress", new Gson().toJsonTree(deliveryAddress));

        asynchronousPost("SaveUserAddress", obj, callback);
    }

    public void GetPhotoTemplate(int templateID, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("TemplateID", templateID);

        asynchronousPost("GetPhotoTemplate", obj, callback);
    }

    public void GetPhotoTemplateList(int typeDescID, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("TypeDescID", typeDescID);

        asynchronousPost("GetPhotoTemplateList", obj, callback);
    }

    public void GetPhotoTemplateListByType(ProductType productType, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("ProductType", productType.value());

        asynchronousPost("GetPhotoTemplateListByType", obj, callback);
    }

    public void GetPhotoTemplateAttachList(int templateID, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("TemplateID", templateID);

        asynchronousPost("GetPhotoTemplateAttachList", obj, callback);
    }

    public void GetPhotoTemplateTypeList(Callback<ResponseData> callback) {
        asynchronousPost("GetPhotoTemplateTypeList", null, callback);
    }

    public void GetPhotoTemplateCoverList(int templateID, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("TemplateID", templateID);

        asynchronousPost("GetPhotoTemplateCoverList", obj, callback);
    }

    public void GetUserOrderList(int pageIndex, OrderStatus orderStatus, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("OrderStatus", orderStatus.value());
        obj.addProperty("PageIndex", pageIndex);

        asynchronousPost("GetUserOrderList", obj, callback);
    }

    public void ActivatingCoupon(String activationCode, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("ActivationCode", activationCode);

        asynchronousPost("ActivatingCoupon", obj, callback);
    }

    public void GetUserCouponList(Callback<ResponseData> callback) {
        asynchronousPost("GetUserCouponList", null, callback);
    }

    public void GetSystemSetting(Callback<ResponseData> callback) {
        asynchronousPost("GetSystemSetting", null, callback);
    }

    // 确认支付调用接口（返回微信prepayid 给sdk调用）//支付宝暂时没做，后续做的话会改动
    public void UpdateOrderPrepay(OrderInfo orderInfo, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.add("OrderInfo", new Gson().toJsonTree(orderInfo));

        asynchronousPost("UpdateOrderPrepay", obj, callback);
    }

    public void GetOrderInfo(int orderId, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("OrderID", orderId);

        asynchronousPost("GetOrderInfo", obj, callback);
    }

    // 确认收货接口
    public void UpdateOrderFinished(int orderID, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("OrderID", orderID);

        asynchronousPost("UpdateOrderFinished", obj, callback);
    }

    // 申请退款接口
    public void UpdateOrderRefunding(OrderInfo orderInfo, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.add("OrderInfo", new Gson().toJsonTree(orderInfo));

        asynchronousPost("UpdateOrderRefunding", obj, callback);
    }

    //获取用户积分查询接口
    public void GetUserIntegralInfo(Callback<ResponseData> callback) {
        asynchronousPost("GetUserIntegralInfo", null, callback);
    }

    //获取我的作品列表接口
    public void GetUserWorkList(Callback<ResponseData> callback) {
        asynchronousPost("GetUserWorkList", null, callback);
    }

    //修改用户信息
    public void SaveUserInfo(User user, Callback<ResponseData> callback) {
        JsonObject object = new JsonObject();
        object.add("UserInfo", new Gson().toJsonTree(user));

        asynchronousPost("SaveUserInfo", object, callback);
    }

    public void GetUserWork(int workID, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("WorkID", workID);
        asynchronousPost("GetUserWork", obj, callback);
    }

    //删除收货地址
    public void DeleteUserAddress(int AutoID, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("AutoID", AutoID);
        asynchronousPost("DeleteUserAddress", obj, callback);
    }

    //意见反馈接口
    public void SaveUserSuggestion(String Phone, String Suggestion, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.addProperty("Phone", Phone);
        obj.addProperty("Suggestion", Suggestion);
        asynchronousPost("SaveUserSuggestion", obj, callback);
    }

    // 删除我的作品
    public void DeleteUserWork(List<Integer> WorkIDList, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.add("WorkIDList", new Gson().toJsonTree(WorkIDList));
        asynchronousPost("DeleteUserWork", obj, callback);
    }

    // 购买我的作品
    public void SaveOrderByWork(List<Integer> workIDList, Callback<ResponseData> callback) {
        JsonObject obj = new JsonObject();
        obj.add("WorkIDList", new Gson().toJsonTree(workIDList));
        asynchronousPost("SaveOrderByWork", obj, callback);
    }

    //消息中心
    public void GetUserMessageList(int PageIndex, Callback<ResponseData> callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("PageIndex", PageIndex);
        asynchronousPost("GetUserMessageList", jsonObject, callback);
    }

    //根据所选的尺寸Id获取对应模板信息
    public void GetPhotoTemplateByTypeDesc(int TypeDescID, Callback<ResponseData> callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("TypeDescID", TypeDescID);
        asynchronousPost("GetPhotoTemplateByTypeDesc", jsonObject, callback);
    }

    //根据模板Id获取材质信息
    public void GetPhotoTemplatePropertyList(int TemplateID, Callback<ResponseData> callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("TemplateID", TemplateID);
        asynchronousPost("GetPhotoTemplatePropertyList", jsonObject, callback);
    }

    //获取验证码
    public void GetCheckCode(String Phone, Callback<ResponseData> callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Phone", Phone);
        asynchronousPost("GetCheckCode", jsonObject, callback);
    }

}
