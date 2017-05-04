package com.hailan.HaiLanPrint.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.payment.PaymentSuccessActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.enumobject.OrderStatus;
import com.hailan.HaiLanPrint.enumobject.PayType;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.enumobject.UploadType;
import com.hailan.HaiLanPrint.models.DeliveryAddress;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.Measurement;
import com.hailan.HaiLanPrint.models.OrderInfo;
import com.hailan.HaiLanPrint.models.OrderWork;
import com.hailan.HaiLanPrint.models.OrderWorkPhoto;
import com.hailan.HaiLanPrint.models.OrderWorkPhotoEdit;
import com.hailan.HaiLanPrint.models.PhotoTemplateAttachFrame;
import com.hailan.HaiLanPrint.models.Template;
import com.hailan.HaiLanPrint.models.WorkInfo;
import com.hailan.HaiLanPrint.models.WorkPhoto;
import com.hailan.HaiLanPrint.models.WorkPhotoEdit;
import com.hailan.HaiLanPrint.models.alipay.PayResult;
import com.hailan.HaiLanPrint.models.wechat.WechatPayResult;
import com.hailan.HaiLanPrint.restfuls.FileRestful;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.alipay.SignUtils;
import com.socks.library.KLog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import jp.co.cyberagent.android.gpuimage.GPUImageTransformFilter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hailan.HaiLanPrint.constants.Constants.RSA_PRIVATE;
import static com.hailan.HaiLanPrint.enumobject.ProductType.PhoneShell;

/**
 * Created by yoghourt on 6/12/16.
 */
public class OrderUtils {

    private Activity mActivity;

    public int mOrderCount;

    public int mPrice;

    public OrderInfo mOrderInfo;

    public String mWorkFormat = "";

    public String mWorkMaterial = "";

    public String mWorkStyle = "";

    private WorkInfo mWorkInfo;

    // true : 只是保存到我的作品,没有下单
    public boolean mIsJustSaveUserWork;

    public ArrayList<OrderWork> mOrderWorkArrayList = new ArrayList<>();

    private static final int SDK_PAY_FLAG = 1;

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 支付成功
//                        Toast.makeText(PaymentPreviewActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        getOrderInfoRequest(mOrderInfo.getOrderID());
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            // 支付结果确认中
//                            Toast.makeText(PaymentPreviewActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            // 支付失败
                            Toast.makeText(mActivity, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    public OrderUtils(OrderInfo orderInfo, ArrayList<OrderWork> orderWorkArrayList) {
        mOrderInfo = orderInfo;
        mOrderWorkArrayList = orderWorkArrayList;
    }

    public OrderUtils(Activity activity, boolean isJustSaveUserWork, int orderCount, int price) {
        this.mActivity = activity;
        mOrderCount = orderCount;
        mPrice = price;
        mIsJustSaveUserWork = isJustSaveUserWork;
    }

    public OrderUtils(Activity activity, boolean isJustSaveUserWork, int orderCount, int price,
                      String workFormat, String workMaterial, String workStyle) {
        this.mActivity = activity;
        mOrderCount = orderCount;
        mPrice = price;
        mIsJustSaveUserWork = isJustSaveUserWork;

        if (workFormat != null) {
            this.mWorkFormat = workFormat;
        }

        if (workMaterial != null) {
            this.mWorkMaterial = workMaterial;
        }

        if (workStyle != null) {
            this.mWorkStyle = workStyle;
        }
    }

    private GPUImageTransformFilter getTransformFilter(float scaleFactor, float rotationDegree) {
        float[] transform = new float[16];
        Matrix.setIdentityM(transform, 0);
        Matrix.setRotateM(transform, 0, rotationDegree, 0, 0, 1.0f);
        scaleFactor = scaleFactor;
        if (scaleFactor < 0) {
            scaleFactor = 1;
        }
        Matrix.scaleM(transform, 0, scaleFactor, scaleFactor, 1.0f);

        GPUImageTransformFilter transformFilter = new GPUImageTransformFilter();
        transformFilter.setTransform3D(transform);
        return transformFilter;
    }

    // 上传每页的合成照片
    public void uploadAllCompositeImageReuqest(final Context context, final List<String> compositeImageList,
                                               final int upload_image_index) {
        final int nextUploadIndex = upload_image_index + 1;

        if (upload_image_index < compositeImageList.size()) {
            File file = new File(compositeImageList.get(upload_image_index));

            FileRestful.getInstance().UploadPhoto(UploadType.Order, file, new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                    KLog.e("上传合成照片成功");
                    final MDImage responseImage = response.body().getContent(MDImage.class);

                    MDImage templateImage = SelectImageUtils.sTemplateImage.get(upload_image_index);

                    WorkPhoto workPhoto = templateImage.getWorkPhoto();
                    workPhoto.setPhoto2ID(responseImage.getPhotoID());
                    workPhoto.setPhoto2SID(responseImage.getPhotoSID());
//                    SelectImageUtils.sTemplateImage.set(upload_image_index, responseImage);

                    uploadAllCompositeImageReuqest(context, compositeImageList, nextUploadIndex);
                }

                @Override
                public void onFailure(Call<ResponseData> call, Throwable t) {

                }
            });
        } else {
            uploadAllUserSelectImageRequest(context, 0);
            // 现在只需要合成图的照片,不需要上传用户选择的照片
//            saveUserWorkReqeust();
        }
    }

    // 上传用户选择的所有照片
    public void uploadAllUserSelectImageRequest(final Context context, final int upload_image_index) {
        final int nextUploadIndex = upload_image_index + 1;

        if (upload_image_index < SelectImageUtils.getMaxUserSelectImageNum()) {
            final MDImage selectImage = SelectImageUtils.sAlreadySelectImage.get(upload_image_index);

            // 本地图片
            if (selectImage.getImageLocalPath() != null && !StringUtil.isEmpty(selectImage.getImageLocalPath())) {
                File file = new File(selectImage.getImageLocalPath());

                FileRestful.getInstance().UploadPhoto(UploadType.Order, file, new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        KLog.e("上传本地照片成功");
                        final MDImage responseImage = response.body().getContent(MDImage.class);

                        selectImage.setPhotoID(responseImage.getPhotoID());
                        selectImage.setPhotoSID(responseImage.getPhotoSID());
//                        SelectImageUtils.sAlreadySelectImage.set(upload_image_index, responseImage);

                        uploadAllUserSelectImageRequest(context, nextUploadIndex);
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {

                    }
                });
            } else {
                uploadAllUserSelectImageRequest(context, nextUploadIndex);
            }
        } else {
            switch (MDGroundApplication.sInstance.getChoosedProductType()) {
                case PrintPhoto:
//                case PictureFrame:
//                case Engraving:
                    // 全部图片上传完之后,生成订单
                    saveOrderRequest();
                    break;
                default:
                    saveUserWorkReqeust();
                    break;
            }
        }
    }

    // 上传每页的合成照片（隐式保存）
    public void uploadAllCompositeImageReuqestNotEnable(final Context context, final List<String> compositeImageList,
                                                        final int upload_image_index) {

        //2017.2.23 不需要上传合成图片
        boolean needUploadCompositeImage = false;
        if (!needUploadCompositeImage) {
            uploadAllUserSelectImageRequestNotEnable(context, 0);
            return;
        }

        final int nextUploadIndex = upload_image_index + 1;

        if (upload_image_index < compositeImageList.size()) {
            File file = new File(compositeImageList.get(upload_image_index));

            FileRestful.getInstance().UploadPhotoNotEnabled(UploadType.Order, file, new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                    KLog.e("上传合成照片成功");
                    final MDImage responseImage = response.body().getContent(MDImage.class);

                    MDImage templateImage = SelectImageUtils.sTemplateImage.get(upload_image_index);

                    WorkPhoto workPhoto = templateImage.getWorkPhoto();
                    workPhoto.setPhoto2ID(responseImage.getPhotoID());
                    workPhoto.setPhoto2SID(responseImage.getPhotoSID());
//                    SelectImageUtils.sTemplateImage.set(upload_image_index, responseImage);

                    uploadAllCompositeImageReuqestNotEnable(context, compositeImageList, nextUploadIndex);
                }

                @Override
                public void onFailure(Call<ResponseData> call, Throwable t) {

                }
            });
        } else {
            uploadAllUserSelectImageRequestNotEnable(context, 0);
            // 现在只需要合成图的照片,不需要上传用户选择的照片
//            saveUserWorkReqeust();
        }
    }

    // 上传用户选择的所有照片（隐式保存）
    public void uploadAllUserSelectImageRequestNotEnable(final Context context, final int upload_image_index) {
        final int nextUploadIndex = upload_image_index + 1;

        if (upload_image_index < SelectImageUtils.getMaxUserSelectImageNum()) {
            ViewUtils.setLoadingMessage("正在上传原图" + nextUploadIndex + "/" + SelectImageUtils.originalSelectImageSize);
            final MDImage selectImage = SelectImageUtils.sAlreadySelectImage.get(upload_image_index);

            // 本地图片
            if (selectImage.getImageLocalPath() != null && !StringUtil.isEmpty(selectImage.getImageLocalPath())) {
                File file = new File(selectImage.getImageLocalPath());

                FileRestful.getInstance().UploadPhotoNotEnabled(UploadType.Order, file, new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        KLog.e("上传本地照片成功");
                        final MDImage responseImage = response.body().getContent(MDImage.class);

                        selectImage.setPhotoID(responseImage.getPhotoID());
                        selectImage.setPhotoSID(responseImage.getPhotoSID());
//                        SelectImageUtils.sAlreadySelectImage.set(upload_image_index, responseImage);

                        uploadAllUserSelectImageRequestNotEnable(context, nextUploadIndex);
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {

                    }
                });
            } else {
                uploadAllUserSelectImageRequestNotEnable(context, nextUploadIndex);
            }
        } else {
            ViewUtils.setLoadingMessage(context.getString(R.string.loading));
            switch (MDGroundApplication.sInstance.getChoosedProductType()) {
                case PrintPhoto:
//                case PictureFrame:
//                case Engraving:
                    // 全部图片上传完之后,生成订单
                    saveOrderRequest();
                    break;
                default:
                    saveUserWorkReqeust();
                    break;
            }
        }
    }

    // 上传"冲印模块"或者"版画模块"选中的照片
    public void uploadPrintPhotoOrEngravingImageRequest(final Context context, final int upload_image_index) {
        if (upload_image_index < SelectImageUtils.sAlreadySelectImage.size()) {
            final MDImage selectImage = SelectImageUtils.sAlreadySelectImage.get(upload_image_index);

            MDImage tempTemplateImage;
            if (upload_image_index < SelectImageUtils.sTemplateImage.size()) {
                tempTemplateImage = SelectImageUtils.sTemplateImage.get(upload_image_index);
            } else {
                tempTemplateImage = new MDImage();
            }
            final MDImage templateImage = tempTemplateImage;

            final int nextUploadIndex = upload_image_index + 1;
            ViewUtils.setLoadingMessage("正在上传原图" + nextUploadIndex + "/" + SelectImageUtils.originalSelectImageSize);

            // 本地图片
            if (selectImage.getImageLocalPath() != null && !StringUtil.isEmpty(selectImage.getImageLocalPath())) {
                File file = new File(selectImage.getImageLocalPath());

                // 上传本地照片
                FileRestful.getInstance().UploadPhotoNotEnabled(UploadType.Order, file, new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        KLog.e("上传本地照片成功");
                        final MDImage responseImage = response.body().getContent(MDImage.class);

                        // 上传本地图片成功, 设置对应的PhotoID, PhotoSID
                        selectImage.setPhotoID(responseImage.getPhotoID());
                        selectImage.setPhotoSID(responseImage.getPhotoSID());
//                        responseImage.setPhotoCount(selectImage.getPhotoCount());

                        final WorkPhoto workPhoto = selectImage.getWorkPhoto();
                        if (workPhoto != null) {
                            workPhoto.setPhoto1ID(responseImage.getPhotoID());
                            workPhoto.setPhoto1SID(responseImage.getPhotoSID());
//                            responseImage.setWorkPhoto(workPhoto);
                        }

//                        SelectImageUtils.sAlreadySelectImage.set(upload_image_index, responseImage);
                        uploadPrintPhotoOrEngravingImageRequest(context, nextUploadIndex);
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                    }
                });
            } else {
                // 网络图片,则继续上传
                uploadPrintPhotoOrEngravingImageRequest(context, nextUploadIndex);
            }
        } else {
            ViewUtils.setLoadingMessage(context.getString(R.string.loading));
            switch (MDGroundApplication.sInstance.getChoosedProductType()) {
                case PrintPhoto:
//                case PictureFrame:
//                case Engraving:
                    // 全部图片上传完之后,生成订单
                    saveOrderRequest();
                    break;
                default:
                    saveUserWorkReqeust();
                    break;
            }
        }
    }

    public void saveUserWorkReqeust() {
        WorkInfo workInfo = new WorkInfo();
        workInfo.setCreatedTime(DateUtils.getServerDateStringByDate(new Date()));
        workInfo.setPhotoCount(SelectImageUtils.sTemplateImage.size());
        workInfo.setPrice(mPrice);
        workInfo.setTypeID(MDGroundApplication.sInstance.getChoosedProductType().value());
        if (MDGroundApplication.sInstance.getChoosedMeasurement() != null) {
            workInfo.setTypeTitle(MDGroundApplication.sInstance.getChoosedMeasurement().getTitle());
        }
        workInfo.setTemplateID(MDGroundApplication.sInstance.getChoosedTemplate().getTemplateID());
        workInfo.setTypeName(ProductType.getProductName(MDGroundApplication.sInstance.getChoosedProductType()));
        workInfo.setUserID(MDGroundApplication.sInstance.getLoginUser().getUserID());

        //缺省值判断
        if("".equals(mWorkFormat)){
            mWorkFormat = MDGroundApplication.sInstance.getChoosedTemplate().getTypeDesc();
        }

        workInfo.setWorkMaterial(mWorkMaterial);
        workInfo.setWorkStyle(mWorkStyle);
        workInfo.setWorkFormat(mWorkFormat);
        workInfo.setWorkDesc("");

        if (mIsJustSaveUserWork) {
            workInfo.setEnabled(true);
        } else {
            workInfo.setEnabled(false);
        }

        GlobalRestful.getInstance().SaveUserWork(workInfo, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                mWorkInfo = response.body().getContent(WorkInfo.class);

                saveUserWorkPhotoListRequest(mWorkInfo);
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void saveUserWorkPhotoListRequest(WorkInfo responseWorkInfo) {
        List<WorkPhoto> workPhotoList = new ArrayList<>();

        int moduleIndexCount = 0;
        for (int pageIndex = 0; pageIndex < SelectImageUtils.sTemplateImage.size(); pageIndex++) {
            MDImage mdImage = SelectImageUtils.sTemplateImage.get(pageIndex);
            WorkPhoto workPhoto = mdImage.getWorkPhoto();
            workPhoto.setWorkID(responseWorkInfo.getWorkID());
            workPhoto.setPhoto2ID(0);
            workPhoto.setPhoto2SID(0);

            if (TemplateUtils.isTemplateHasModules()) {
                List<WorkPhotoEdit> workPhotoEditList = new ArrayList<>();

                List<PhotoTemplateAttachFrame> photoTemplateAttachFrameList = mdImage.getPhotoTemplateAttachFrameList();
                for (PhotoTemplateAttachFrame photoTemplateAttachFrame : photoTemplateAttachFrameList) {
                    MDImage uploadUserSelectImage = SelectImageUtils.sAlreadySelectImage.get(moduleIndexCount);

                    WorkPhotoEdit workPhotoEdit = uploadUserSelectImage.getWorkPhotoEdit();

                    workPhotoEdit.setPhotoID(uploadUserSelectImage.getPhotoID());
                    workPhotoEdit.setPhotoSID(uploadUserSelectImage.getPhotoSID());

                    workPhotoEditList.add(workPhotoEdit);

                    moduleIndexCount++;
                }
                workPhoto.setWorkPhotoEditList(workPhotoEditList);
            } else {
                List<WorkPhotoEdit> workPhotoEditList = new ArrayList<>();

                MDImage uploadUserSelectImage = SelectImageUtils.sAlreadySelectImage.get(pageIndex);

                WorkPhotoEdit workPhotoEdit = uploadUserSelectImage.getWorkPhotoEdit();

                workPhotoEdit.setPhotoID(uploadUserSelectImage.getPhotoID());
                workPhotoEdit.setPhotoSID(uploadUserSelectImage.getPhotoSID());

                workPhotoEditList.add(workPhotoEdit);

                workPhoto.setWorkPhotoEditList(workPhotoEditList);
            }

            workPhotoList.add(workPhoto);
        }

        GlobalRestful.getInstance().SaveUserWorkPhotoList(workPhotoList, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ArrayList<WorkPhoto> responseWorkPhotoList = response.body().getContent(new TypeToken<ArrayList<WorkPhoto>>() {
                });

                for (int i = 0; i < responseWorkPhotoList.size(); i++) {
                    WorkPhoto workPhoto = responseWorkPhotoList.get(i);

                    WorkPhoto tempWorkPhoto = SelectImageUtils.sTemplateImage.get(i).getWorkPhoto();
                    tempWorkPhoto.setPhoto2ID(workPhoto.getPhoto2ID());
                    tempWorkPhoto.setPhoto2SID(workPhoto.getPhoto2SID());
                }

                List<MDImage> mdImageList = new ArrayList<MDImage>();
                for (WorkPhoto workPhoto : responseWorkPhotoList) {
                    MDImage mdImage = new MDImage();
                    mdImage.setPhotoID(workPhoto.getPhoto2ID());
                    mdImage.setPhotoName("");
                    mdImage.setPhotoSID(workPhoto.getPhoto2SID());
                    mdImage.setShared(false);
                    mdImage.setUserID(MDGroundApplication.sInstance.getLoginUser().getUserID());
                    mdImageList.add(mdImage);
                }
                savePhotoCloudListRequest(mdImageList);
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }

    private void savePhotoCloudListRequest(List<MDImage> photoCloudList) {
        GlobalRestful.getInstance().SavePhotoCloudList(photoCloudList, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (mIsJustSaveUserWork) {
                    ViewUtils.dismiss();
                    ViewUtils.toast("已保存到我的作品");
                    NavUtils.toMainActivity(mActivity);
                } else {
                    saveOrderRequest();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }

    public void saveOrderRequest() {
        GlobalRestful.getInstance().SaveOrder(MDGroundApplication.sInstance.getChoosedProductType(), new
                Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {

                        mOrderInfo = response.body().getContent(OrderInfo.class);

                        OrderWork orderWork = createOrderWork(mOrderInfo);
                        mOrderWorkArrayList.add(orderWork);
                        saveOrderWorkRequest(0);
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        ViewUtils.dismiss();
                    }
                });
    }

    private OrderWork createOrderWork(OrderInfo orderInfo) {
        OrderWork orderWork = new OrderWork();
        orderWork.setCreateTime(DateUtils.getServerDateStringByDate(new Date()));
        orderWork.setOrderCount(mOrderCount);
        orderWork.setOrderID(orderInfo.getOrderID());
        if (MDGroundApplication.sInstance.getChoosedProductType() == ProductType.PrintPhoto
//                || MDGroundApplication.sInstance.getChoosedProductType() == ProductType.Engraving
                ) {
            orderWork.setPhotoCount(SelectImageUtils.getPrintPhotoOrEngravingOrderCount());
            orderWork.setPhotoCover(SelectImageUtils.sAlreadySelectImage.get(0).getPhotoSID()); //封面，第一张照片的缩略图ID
        } else {
            orderWork.setPhotoCount(SelectImageUtils.sTemplateImage.size());
            orderWork.setPhotoCover(SelectImageUtils.sTemplateImage.get(0).getWorkPhoto().getPhoto2SID()); //封面，第一张照片的合成照片
        }

        orderWork.setPrice(mPrice);
//        orderWork.setPrice(1); // 测试,全部设成0.01元
        orderWork.setTypeID(MDGroundApplication.sInstance.getChoosedProductType().value()); //作品类型（getPhotoType接口返回的TypeID）
        orderWork.setTypeName(ProductType.getProductName(MDGroundApplication.sInstance.getChoosedProductType()));
        Measurement measurement = MDGroundApplication.sInstance.getChoosedMeasurement();
        if (measurement != null) {
            orderWork.setTypeTitle(measurement.getTitle());
            if (MDGroundApplication.sInstance.getChoosedProductType() != PhoneShell) {
                orderWork.setWorkFormat(measurement.getTitle() + "(" + measurement.getTitleSub() + ")");
            } else {
                orderWork.setWorkFormat(measurement.getTitle());
            }
        }
        Template template = MDGroundApplication.sInstance.getChoosedTemplate();
        if (template != null) {
            orderWork.setTemplateID(template.getTemplateID());
            orderWork.setTemplateName(template.getTemplateName());
            orderWork.setWorkMaterial(template.getMaterialTypeString());
        }
        //Title（getPhotoType接口返回的Title）
        orderWork.setWorkFormat(mWorkFormat);
        orderWork.setWorkMaterial(mWorkMaterial);
        orderWork.setWorkStyle(mWorkStyle);

        return orderWork;
    }

    public void saveOrderWorkRequest(final int saveIndex) {
        GlobalRestful.getInstance().SaveOrderWork(mOrderWorkArrayList.get(saveIndex), new
                Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        OrderWork responseOrderWork = response.body().getContent(OrderWork.class);
                        mOrderWorkArrayList.set(saveIndex, responseOrderWork);

                        saveOrderPhotoListRequest(responseOrderWork);
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        ViewUtils.dismiss();
                    }
                });
    }

    private void saveOrderPhotoListRequest(final OrderWork orderWork) {
        List<OrderWorkPhoto> orderWorkPhotoList = new ArrayList<>();

        ArrayList<MDImage> mdImageArrayList = null;

        if (MDGroundApplication.sInstance.getChoosedProductType() == ProductType.PrintPhoto
//                || MDGroundApplication.sInstance.getChoosedProductType() == ProductType.Engraving
                ) {
            mdImageArrayList = SelectImageUtils.sAlreadySelectImage;
        } else {
            mdImageArrayList = SelectImageUtils.sTemplateImage;
        }

        int moduleIndexCount = 0;
        for (int pageIndex = 0; pageIndex < mdImageArrayList.size(); pageIndex++) {
            MDImage mdImage = mdImageArrayList.get(pageIndex);
            WorkPhoto workPhoto = mdImage.getWorkPhoto();

            OrderWorkPhoto orderWorkPhoto = new OrderWorkPhoto();

            if (MDGroundApplication.sInstance.getChoosedProductType() == ProductType.PrintPhoto
//                    || MDGroundApplication.sInstance.getChoosedProductType() == ProductType.Engraving
                    ) {
                orderWorkPhoto.setAutoID(mdImage.getAutoID());
                orderWorkPhoto.setWorkOID(orderWork.getWorkOID());
                orderWorkPhoto.setPhoto1ID(mdImage.getPhotoID());
                orderWorkPhoto.setPhoto1SID(mdImage.getPhotoSID());
                int index = pageIndex + 1;
                orderWorkPhoto.setPhotoIndex(index);
                orderWorkPhoto.setPhotoCount(mdImage.getPhotoCount());//图片数量

            } else {
                orderWorkPhoto.setBrightLevel(workPhoto.getBrightLevel());
                orderWorkPhoto.setPhoto1ID(workPhoto.getPhoto1ID());
                orderWorkPhoto.setPhoto1SID(workPhoto.getPhoto1SID());
                orderWorkPhoto.setPhoto2ID(workPhoto.getPhoto2ID());
                orderWorkPhoto.setPhoto2SID(workPhoto.getPhoto2SID());
                orderWorkPhoto.setPhotoCount(mdImage.getPhotoCount());
                orderWorkPhoto.setPhotoIndex(workPhoto.getPhotoIndex());
                orderWorkPhoto.setWorkOID(orderWork.getWorkOID());
                orderWorkPhoto.setTemplatePID(mdImage.getPhotoID());
                orderWorkPhoto.setTemplatePSID(mdImage.getPhotoSID());

                if (TemplateUtils.isTemplateHasModules()) {
                    List<OrderWorkPhotoEdit> orderWorkPhotoEditList = new ArrayList<>();

                    List<PhotoTemplateAttachFrame> photoTemplateAttachFrameList = mdImage.getPhotoTemplateAttachFrameList();
                    for (PhotoTemplateAttachFrame photoTemplateAttachFrame : photoTemplateAttachFrameList) {
                        OrderWorkPhotoEdit orderWorkPhotoEdit = new OrderWorkPhotoEdit();

                        MDImage uploadUserSelectImage = SelectImageUtils.sAlreadySelectImage.get(moduleIndexCount);

                        WorkPhotoEdit workPhotoEdit = uploadUserSelectImage.getWorkPhotoEdit();

                        orderWorkPhotoEdit.setPhotoID(uploadUserSelectImage.getPhotoID());
                        orderWorkPhotoEdit.setPhotoSID(uploadUserSelectImage.getPhotoSID());

                        // 复制workPhotoEdit的数据过来
                        orderWorkPhotoEdit.setFrameID(workPhotoEdit.getFrameID());
                        orderWorkPhotoEdit.setPositionX(workPhotoEdit.getPositionX());
                        orderWorkPhotoEdit.setPositionY(workPhotoEdit.getPositionY());
                        orderWorkPhotoEdit.setRotate(workPhotoEdit.getRotate());
                        orderWorkPhotoEdit.setZoomSize(workPhotoEdit.getZoomSize());
                        orderWorkPhotoEdit.setMatrix(workPhotoEdit.getMatrix());

                        orderWorkPhotoEditList.add(orderWorkPhotoEdit);

                        moduleIndexCount++;
                    }
                    orderWorkPhoto.setOrderWorkPhotoEditList(orderWorkPhotoEditList);
                } else {
                    List<OrderWorkPhotoEdit> orderWorkPhotoEditList = new ArrayList<>();

                    OrderWorkPhotoEdit orderWorkPhotoEdit = new OrderWorkPhotoEdit();

                    MDImage uploadUserSelectImage = SelectImageUtils.sAlreadySelectImage.get(pageIndex);

                    WorkPhotoEdit workPhotoEdit = uploadUserSelectImage.getWorkPhotoEdit();

                    orderWorkPhotoEdit.setPhotoID(uploadUserSelectImage.getPhotoID());
                    orderWorkPhotoEdit.setPhotoSID(uploadUserSelectImage.getPhotoSID());
                    orderWorkPhotoEdit.setMatrix(workPhotoEdit.getMatrix());

                    orderWorkPhotoEditList.add(orderWorkPhotoEdit);

                    orderWorkPhoto.setOrderWorkPhotoEditList(orderWorkPhotoEditList);
                }
            }

            orderWorkPhotoList.add(orderWorkPhoto);
        }

        GlobalRestful.getInstance().SaveOrderPhotoList(orderWorkPhotoList, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                NavUtils.toPaymentPreviewActivity(mActivity);
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ViewUtils.dismiss();
            }
        });
    }

    public void updateOrderPrepayRequest(final Activity activity, DeliveryAddress deliveryAddress, final PayType
            payType, int amountFee, int receivableFee, int couponID, int couponFee, int deliveryFee, int creditFee) {
        updateOrderPrepayRequest(activity, deliveryAddress, payType, amountFee, receivableFee, couponID, couponFee, deliveryFee, creditFee, null);
    }

    public void updateOrderPrepayRequest(final Activity activity, DeliveryAddress deliveryAddress, final PayType
            payType, int amountFee, int receivableFee, int couponID, int couponFee, int deliveryFee, int creditFee, final WechatCallback callback) {
//        ViewUtils.loading(activity);
        this.mActivity = activity;
        mOrderInfo.setAddressID(deliveryAddress.getAutoID());
        mOrderInfo.setAddressReceipt(StringUtil.getCompleteAddress(deliveryAddress));
        mOrderInfo.setCreatedTime(DateUtils.getServerDateStringByDate(new Date()));
        mOrderInfo.setOrderStatus(OrderStatus.Paid.value());
        mOrderInfo.setPayType(payType.value());
        mOrderInfo.setPhone(deliveryAddress.getPhone());
        mOrderInfo.setReceiver(deliveryAddress.getReceiver());
        mOrderInfo.setTotalFee(amountFee);
        mOrderInfo.setTotalFeeReal(receivableFee);
        mOrderInfo.setCouponID(couponID);
        mOrderInfo.setCouponFee(couponFee);
        mOrderInfo.setDeliveryFee(deliveryFee);
        mOrderInfo.setIntegralFee(creditFee);

        GlobalRestful.getInstance().UpdateOrderPrepay(mOrderInfo, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, final Response<ResponseData> response) {
//                ViewUtils.dismiss();
//                Intent intent = new Intent(activity, PaymentSuccessActivity.class);
//                intent.putExtra(Constants.KEY_ORDER_INFO, mOrderInfo);
//                intent.putExtra(Constants.KEY_WORK_INFO, mWorkInfo);
//                activity.startActivity(intent);

                switch (payType) {
                    case Alipay:
                        pay(activity, response.body().getContent());
                        break;

                    case WeChat:
                        WechatPayResult wechatPayResult = response.body().getContent(WechatPayResult.class);
                        if (callback != null) {
                            callback.callback(wechatPayResult);
                        }
                        break;
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    public void getOrderInfoRequestWithOutId() {
        GlobalRestful.getInstance().GetOrderInfo(mOrderInfo.getOrderID(), new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();

                OrderInfo orderInfo = response.body().getContent(OrderInfo.class);
                if (orderInfo.getOrderStatus() == OrderStatus.Paid.value()) {
                    toPaymentSuccessActivity();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void getOrderInfoRequest(int orderId) {
        GlobalRestful.getInstance().GetOrderInfo(orderId, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();

                OrderInfo orderInfo = response.body().getContent(OrderInfo.class);
                if (orderInfo.getOrderStatus() == OrderStatus.Paid.value()) {
                    toPaymentSuccessActivity();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }

    private void toPaymentSuccessActivity() {
        Intent intent = new Intent(mActivity, PaymentSuccessActivity.class);
        intent.putExtra(Constants.KEY_ORDER_INFO, mOrderInfo);
        intent.putExtra(Constants.KEY_WORK_INFO, mWorkInfo);
        mActivity.startActivity(intent);
    }

    public interface WechatCallback {
        void callback(WechatPayResult wechatPayResult);
    }

    //region 支付宝支付
    private void pay(final Activity activity, final String responsePayInfo) {

        // 这里是demo测试时用到的,现在payInfo已经直接可以从服务器返回了
//        String orderInfo = getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");
//
//        /**
//         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
//         */
//        String sign = sign(orderInfo);
//        try {
//            /**
//             * 仅需对sign 做URL编码
//             */
//            sign = URLEncoder.encode(sign, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        /**
//         * 完整的符合支付宝参数规范的订单信息
//         */
//        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activity);
                // 调用支付接口，获取支付结果
//                String result = alipay.pay(payInfo, true);
                String result = alipay.pay(responsePayInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;


                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + Constants.PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + Constants.SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }
    //endregion
}
