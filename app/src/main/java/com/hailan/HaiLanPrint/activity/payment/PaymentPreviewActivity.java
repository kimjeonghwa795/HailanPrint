package com.hailan.HaiLanPrint.activity.payment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.activity.coupon.ChooseCouponActivity;
import com.hailan.HaiLanPrint.activity.deliveryaddress.ChooseDeliveryAddressActivity;
import com.hailan.HaiLanPrint.activity.payment.wechat.PayCallback;
import com.hailan.HaiLanPrint.activity.payment.wechat.PayUtils;
import com.hailan.HaiLanPrint.activity.payment.wechat.WechatPayCallback;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityPaymentPreviewBinding;
import com.hailan.HaiLanPrint.databinding.ItemPaymentPreviewBinding;
import com.hailan.HaiLanPrint.enumobject.PayType;
import com.hailan.HaiLanPrint.enumobject.ProductMaterial;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.enumobject.SettingType;
import com.hailan.HaiLanPrint.models.Coupon;
import com.hailan.HaiLanPrint.models.DeliveryAddress;
import com.hailan.HaiLanPrint.models.Measurement;
import com.hailan.HaiLanPrint.models.OrderWork;
import com.hailan.HaiLanPrint.models.SystemSetting;
import com.hailan.HaiLanPrint.models.Template;
import com.hailan.HaiLanPrint.models.UserIntegral;
import com.hailan.HaiLanPrint.models.wechat.WechatPayResult;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.DateUtils;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.JsonUtil;
import com.hailan.HaiLanPrint.utils.OrderUtils;
import com.hailan.HaiLanPrint.utils.SelectImageUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hailan.HaiLanPrint.application.MDGroundApplication.sInstance;
import static com.hailan.HaiLanPrint.enumobject.ProductType.PhoneShell;

/**
 * Created by yoghourt on 5/23/16.
 */
public class PaymentPreviewActivity extends ToolbarActivity<ActivityPaymentPreviewBinding> {

    private final int REQEUST_CODE_SELECT_DELIVERY_ADDRESS = 0x11;
    private final int REQEUST_CODE_SELECT_COUPON = 0x12;

    private DeliveryAddress mDeliveryAddress;

    private ArrayList<OrderWork> mOrderWorkArrayList;

    private ArrayList<UserIntegral> mUserCreditArrayList = new ArrayList<>();

    private ArrayList<Coupon> mAvailableCouponArrayList = new ArrayList<>();

    private SystemSetting mPayIntegralAmountSetting, mDeliveryFeeSetting;

    private Coupon mSelectedCoupon;

    private AlertDialog mAlertDialog;

    private int mCredit;

    private boolean mHadChangedOrderCount;

    private RadioButton checkButton = null;

    private PaymentPreviewAdapter paymentPreviewAdapter;

    private View.OnClickListener onRadioButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkButton == v) {
                return;
            }
            if (checkButton != null) {
                checkButton.setChecked(false);
            }
            ((RadioButton) v).setChecked(true);
            checkButton = (RadioButton) v;
            Template.PropertyListBean propertyListBean = (Template.PropertyListBean) v.getTag();
            refreshOrderWorkArrayList(propertyListBean);
        }
    };

    private WechatPayCallback wechatPayCallback = new WechatPayCallback();
    private PayCallback payCallback = new PayCallback() {
        @Override
        public void onPayCallback(PayType payType, int code, Object info) {
            if (payType == PayType.Wechat && code == PayCallback.SUCCESS) {
                MDGroundApplication.sOrderutUtils.getOrderInfoRequestWithOutId();
            }
        }
    };
    private OrderUtils.WechatCallback wechatCallback = new OrderUtils.WechatCallback() {
        @Override
        public void callback(final WechatPayResult wechatPayResult) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PayUtils.wechatPay(PaymentPreviewActivity.this, wechatPayResult.getPrepayID(), wechatPayResult.getPartnerID(),
                            wechatPayResult.getNonceStr(), wechatPayResult.getTimeStamp(), wechatPayResult.getSign());
                }
            });
        }
    };

    @Override
    protected int getContentLayout() {
        return R.layout.activity_payment_preview;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserIntegralInfoRequest();
        getUserCouponListRequest();
        getSystemSettingRequest();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wechatPayCallback.setPayCallback(payCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        WechatPayCallback.register(this, wechatPayCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 页面关闭清空订单
        MDGroundApplication.sOrderutUtils.mOrderWorkArrayList.clear();
        WechatPayCallback.unregister(this, wechatPayCallback);
    }

    @Override
    protected void initData() {
        mAlertDialog = ViewUtils.createAlertDialog(this, getString(R.string.within_12_hours_refund),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mHadChangedOrderCount) {
                            saveOrderWorkRequest(0);
                        } else {
                            updateOrderPrepay();
                        }
                    }
                });

        mOrderWorkArrayList = MDGroundApplication.sOrderutUtils.mOrderWorkArrayList;

        KLog.e(JsonUtil.toJson(mOrderWorkArrayList));

        Measurement measurement = sInstance.getChoosedMeasurement();
        if (measurement != null) {
            for (OrderWork orderWork : mOrderWorkArrayList) {
                if ("".equals(orderWork.getWorkFormat())) {
                    if (sInstance.getChoosedProductType() != PhoneShell) {
                        orderWork.setWorkFormat(measurement.getTitle() + "(" + measurement.getTitleSub() + ")");

                    } else {
                        orderWork.setWorkFormat(measurement.getTitle());
                    }
                }
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataBinding.productRecyclerView.setLayoutManager(layoutManager);

        paymentPreviewAdapter = new PaymentPreviewAdapter();
        mDataBinding.productRecyclerView.setAdapter(paymentPreviewAdapter);

//        refreshDisplayFee();

        if (sInstance.getChoosedProductType() != ProductType.PrintPhoto
//                && MDGroundApplication.sInstance.getChoosedProductType() != ProductType.Engraving
                && sInstance.getChoosedProductType() != ProductType.PhoneShell
                && sInstance.getChoosedProductType() != ProductType.PictureFrame) {

            GetPhotoTemplate(mOrderWorkArrayList.get(0).getTemplateID());
//            GetPhotoTemplate(247);// for test
//            GetPhotoTemplatePropertyList(247);// for test
        }
    }

    /**
     * 刷新价格和材质
     *
     * @param propertyListBean
     */
    private void refreshOrderWorkArrayList(Template.PropertyListBean propertyListBean) {

        Measurement measurement = sInstance.getChoosedMeasurement();
        for (OrderWork orderWork : mOrderWorkArrayList) {
            orderWork.setPrice(propertyListBean.getPrice());
            orderWork.setWorkMaterial(propertyListBean.getMaterialName());
            if (measurement != null && "".equals(orderWork.getWorkFormat())) {
                if (sInstance.getChoosedProductType() != PhoneShell) {
                    orderWork.setWorkFormat(measurement.getTitle() + "(" + measurement.getTitleSub() + ")");

                } else {
                    orderWork.setWorkFormat(measurement.getTitle());
                }
            }
        }
        paymentPreviewAdapter.notifyDataSetChanged();
        refreshDisplayFee();
        mDataBinding.tvCreditYuan.setText(getString(R.string.offset_fee, StringUtil.toYuanWithoutUnit(getCreditFee())));
    }

    @Override
    protected void setListener() {
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                NavUtils.toMainActivity(PaymentPreviewActivity.this);
//            }
//        });

        mDataBinding.cbUseCredit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDataBinding.tvCreditYuan.setText(getString(R.string.offset_fee, StringUtil.toYuanWithoutUnit(getCreditFee())));
                } else {
                    mDataBinding.tvCreditYuan.setText(getString(R.string.offset_fee, "0.00"));
                }
                refreshDisplayFee();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQEUST_CODE_SELECT_DELIVERY_ADDRESS:
                    mDeliveryAddress = data.getParcelableExtra(Constants.KEY_DELIVERY_ADDRESS);

                    if (mDeliveryAddress != null) {
                        mDataBinding.tvName.setText(mDeliveryAddress.getReceiver());
                        mDataBinding.tvPhone.setText(mDeliveryAddress.getPhone());

                        mDataBinding.tvAddress.setText(StringUtil.getCompleteAddress(mDeliveryAddress));

                        mDataBinding.tvChooseFirst.setVisibility(View.GONE);
                        mDataBinding.tvName.setVisibility(View.VISIBLE);
                        mDataBinding.tvPhone.setVisibility(View.VISIBLE);
                        mDataBinding.tvAddress.setVisibility(View.VISIBLE);
                        mDataBinding.ivDeliveryAddress.setVisibility(View.VISIBLE);
                    }

                    break;
                case REQEUST_CODE_SELECT_COUPON:
                    mSelectedCoupon = data.getParcelableExtra(Constants.KEY_SELECTED_COUPON);

                    refreshDisplayFee();
                    break;
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        NavUtils.toMainActivity(PaymentPreviewActivity.this);
//    }

    private double getSingleOrderWorkAmountFee(OrderWork orderWork) {
        ProductType productType = ProductType.fromValue(orderWork.getTypeID());

        switch (productType) {
            case PrintPhoto:
//            case Engraving:
                return orderWork.getPrice() * orderWork.getPhotoCount();
            case Engraving:
            case Postcard:
            case MagazineAlbum:
            case ArtAlbum:
            case PictureFrame:
            case Calendar:
            case PhoneShell:
            case Poker:
            case Puzzle:
            case MagicCup:
            case LOMOCard:
                return orderWork.getPrice() * orderWork.getOrderCount();
        }
        return 0;
    }

    private int getAllOrderWorkAmountFee() {
        int amountFee = 0;
        for (OrderWork orderWork : mOrderWorkArrayList) {
            amountFee += getSingleOrderWorkAmountFee(orderWork);
        }

        return amountFee;
    }

    private int getCouponFee() {
        int couponFee = 0;
        if (mSelectedCoupon != null) {
            couponFee = mSelectedCoupon.getPrice();
        }
        return couponFee;
    }

    private int getCouponID() {
        if (mSelectedCoupon != null) {
            return mSelectedCoupon.getAutoID();
        }
        return 0;
    }

    private int getCreditFee() {
        int creditFee = 0;
        if (mPayIntegralAmountSetting != null && mDataBinding.cbUseCredit.isChecked()) {
            creditFee = mCredit * mPayIntegralAmountSetting.getValue();
        }
        if (creditFee > getReceivableFee()) {
            creditFee = getReceivableFee();
        }
        if (mPayIntegralAmountSetting != null) {
            creditFee = creditFee / mPayIntegralAmountSetting.getValue() * mPayIntegralAmountSetting.getValue();//取整
        }
        return creditFee;
    }

    private int getDeliveryFee() {
        int deliveryFee = 0;
        if (mDeliveryFeeSetting != null) {
            deliveryFee = mDeliveryFeeSetting.getValue();
        }
        return deliveryFee;
    }

    private int getReceivableFee() {
        int amountFee = getAllOrderWorkAmountFee() - getCouponFee();
        return amountFee;
    }

    private int getFinalReceivableFee() {
        int amountFee = getReceivableFee() - getCreditFee() + getDeliveryFee();
        if (amountFee <= 0) {
            amountFee = 1;
        }
        return amountFee;
    }

    private void refreshDisplayFee() {
        mHadChangedOrderCount = true;

        // 总额
        int amountFee = getAllOrderWorkAmountFee();
        mDataBinding.tvAmount.setText(getString(R.string.yuan_amount, StringUtil.toYuanWithoutUnit(amountFee)));

        int matchLimitCoupon = 0;
        for (Coupon coupon : mAvailableCouponArrayList) {
            if (coupon.getPriceLimit() <= getAllOrderWorkAmountFee()) {
                matchLimitCoupon++;
            }
        }

        mDataBinding.tvAvailableCoupon.setText(getString(R.string.available_num, mAvailableCouponArrayList.size()));
        if (mAvailableCouponArrayList.size() == 0) {
            mSelectedCoupon = null;
        }

        // 优惠劵
        int couponFee = 0;
        if (mSelectedCoupon != null) {
            couponFee = mSelectedCoupon.getPrice();
        }
        mDataBinding.tvCouponYuan.setText(getString(R.string.offset_fee, StringUtil.toYuanWithoutUnit(couponFee)));
        mDataBinding.tvCouponFee.setText(getString(R.string.minus_yuan_amount, StringUtil.toYuanWithoutUnit(couponFee)));

        // 应付金额
        int receivableFee = getFinalReceivableFee();
        mDataBinding.tvReceivable.setText(getString(R.string.receivables, StringUtil.toYuanWithoutUnit(receivableFee)));
    }

    private void updateOrderPrepay() {
        PayType payType = PayType.Alipay;
        if (mDataBinding.rgPayment.getCheckedRadioButtonId() == R.id.rbWechatPay) {
            payType = PayType.WeChat;

            MDGroundApplication.sOrderutUtils.updateOrderPrepayRequest(PaymentPreviewActivity.this,
                    mDeliveryAddress, payType, getAllOrderWorkAmountFee(), getFinalReceivableFee(), getCouponID(), getCouponFee(),
                    getDeliveryFee(), getCreditFee() / mPayIntegralAmountSetting.getValue(), wechatCallback);
            return;
        }

        MDGroundApplication.sOrderutUtils.updateOrderPrepayRequest(PaymentPreviewActivity.this,
                mDeliveryAddress, payType, getAllOrderWorkAmountFee(), getFinalReceivableFee(), getCouponID(), getCouponFee(),
                getDeliveryFee(), getCreditFee() / mPayIntegralAmountSetting.getValue());
    }

    //region ACTION
    public void toDeliveryAddressListActivityAction(View view) {
        Intent intent = new Intent(this, ChooseDeliveryAddressActivity.class);
        intent.putExtra(Constants.KEY_DELIVERY_ADDRESS, mDeliveryAddress);
        startActivityForResult(intent, REQEUST_CODE_SELECT_DELIVERY_ADDRESS);
    }

    public void toChooseCouponActivityAction(View view) {
        Intent intent = new Intent(this, ChooseCouponActivity.class);
        intent.putExtra(Constants.KEY_SELECTED_COUPON, mSelectedCoupon);
        intent.putExtra(Constants.KEY_COUPON_LIST, mAvailableCouponArrayList);
        intent.putExtra(Constants.KEY_ORDER_AMOUNT_FEE, getAllOrderWorkAmountFee());
        startActivityForResult(intent, REQEUST_CODE_SELECT_COUPON);
    }

    public void payAction(View view) {
        if (mDeliveryAddress == null) {
            ViewUtils.toast(R.string.choose_address_first);
            return;
        }
        if (sInstance.getChoosedProductType() != ProductType.PrintPhoto
//                && MDGroundApplication.sInstance.getChoosedProductType() != ProductType.Engraving
                && sInstance.getChoosedProductType() != ProductType.PhoneShell
                && sInstance.getChoosedProductType() != ProductType.PictureFrame) {

//            if (checkButton == null) {
//                ViewUtils.toast(R.string.choose_material_first);
//                return;
//            }
        }

        KLog.e("OrderWork", JsonUtil.toJson(mOrderWorkArrayList));

//        if (1 == 1) { // for test
//            return;
//        }

//        mAlertDialog.show();

        if (mHadChangedOrderCount) {
            saveOrderWorkRequest(0);
        } else {
            updateOrderPrepay();
        }
    }

    public void itemMinusNumAction(View view) {
        int position = mDataBinding.productRecyclerView.getChildAdapterPosition(view);

        OrderWork orderWork = mOrderWorkArrayList.get(position);
        int orderCount = orderWork.getOrderCount();

        if (orderCount == 1) {
            return;
        }

        orderWork.setOrderCount(--orderCount);
    }

    public void itemAddNumAction(View view) {
        int position = mDataBinding.productRecyclerView.getChildAdapterPosition(view);

        OrderWork orderWork = mOrderWorkArrayList.get(position);
        int orderCount = orderWork.getOrderCount();

        orderWork.setOrderCount(++orderCount);
    }
    //endregion

    //region SERVER
    private void getUserIntegralInfoRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetUserIntegralInfo(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response.body().getContent());
                    mCredit = jsonObject.getInt("TotalAmount");
                    mDataBinding.tvCredit.setText(getString(R.string.credit_total, String.valueOf(mCredit)));

                    if (mCredit == 0) {
                        mDataBinding.cbUseCredit.setEnabled(false);
                    }

                    String UserIntegralList = jsonObject.getString("UserIntegralList");
                    mUserCreditArrayList = StringUtil.getInstanceByJsonString(UserIntegralList, new TypeToken<ArrayList<UserIntegral>>() {
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                refreshDisplayFee();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void getUserCouponListRequest() {
        GlobalRestful.getInstance().GetUserCouponList(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ArrayList<Coupon> allCouponArrayList = response.body().getContent(new TypeToken<ArrayList<Coupon>>() {
                });

                mAvailableCouponArrayList.clear();
                for (Coupon coupon : allCouponArrayList) {
                    // 判断优惠券是否可用条件：当前时间在ExpireTime之前
                    boolean isAvailable = DateUtils.isBeforeExpireTime(coupon.getExpireTime());
                    boolean isCouponStatusAvailable = coupon.getCouponStatus() == 0;

                    if (isAvailable && isCouponStatusAvailable) {
                        mAvailableCouponArrayList.add(coupon);
                    }
                }
                refreshDisplayFee();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void getSystemSettingRequest() {
        GlobalRestful.getInstance().GetSystemSetting(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ArrayList<SystemSetting> systemSettingArrayList = response.body().getContent(new TypeToken<ArrayList<SystemSetting>>() {
                });

                for (SystemSetting systemSetting : systemSettingArrayList) {
                    SettingType settingType = SettingType.fromValue(systemSetting.getSettingType());
                    if (settingType == SettingType.PayIntegralAmount) {
                        mPayIntegralAmountSetting = systemSetting;

                        if (mDataBinding.cbUseCredit.isChecked()) {
                            mDataBinding.cbUseCredit.setChecked(false);
                            mDataBinding.cbUseCredit.setChecked(true);
                        } else {
                            mDataBinding.cbUseCredit.setChecked(false);
                        }
                    }

                    if (settingType == SettingType.DeliveryFee) {
                        mDeliveryFeeSetting = systemSetting;

                        mDataBinding.tvDeliveryFee.setText(StringUtil.toYuanWithoutUnit(mDeliveryFeeSetting.getValue()));
                    }
                }

                refreshDisplayFee();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void saveOrderWorkRequest(final int saveIndex) {
        if (saveIndex < mOrderWorkArrayList.size()) {
            GlobalRestful.getInstance().SaveOrderWork(mOrderWorkArrayList.get(saveIndex), new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                    int nextSaveIndex = saveIndex + 1;
                    saveOrderWorkRequest(nextSaveIndex);
                }

                @Override
                public void onFailure(Call<ResponseData> call, Throwable t) {
                    ViewUtils.dismiss();
                }
            });
        } else {
            updateOrderPrepay();
        }
    }

    /**
     * 根据模板id获取材质信息
     *
     * @param TemplateID
     */
    private void GetPhotoTemplate(final int TemplateID) {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetPhotoTemplate(TemplateID, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                Template template = response.body().getContent(Template.class);

                checkButton = null;
                mDataBinding.propertyGridLayout.removeAllViews();

                if (template != null && template.getPropertyList().size() > 0) {
                    mDataBinding.propertyGridLayout.setVisibility(View.VISIBLE);
                    for (Template.PropertyListBean propertyListBean : template.getPropertyList()) {
                        addButtonView(propertyListBean);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ViewUtils.dismiss();
            }
        });
    }

    /**
     * 根据模板Id获取材质信息
     *
     * @param TemplateID
     */
    private void GetPhotoTemplatePropertyList(final int TemplateID) {
        GlobalRestful.getInstance().GetPhotoTemplatePropertyList(TemplateID, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }

    /**
     * 添加材质控件
     */
    private void addButtonView(Template.PropertyListBean propertyListBean) {
        View view = LayoutInflater.from(this).inflate(R.layout.widget_prepay_radio_button, null);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.rb_button);
        radioButton.setText(propertyListBean.getMaterialName());
        radioButton.setTag(propertyListBean);
        radioButton.setOnClickListener(onRadioButtonClick);
        if (mDataBinding.propertyGridLayout.getChildCount() == 0) {
            radioButton.setChecked(true);
            checkButton = radioButton;
            refreshOrderWorkArrayList(propertyListBean);
        }
        mDataBinding.propertyGridLayout.addView(view);
    }

    //endregion

    //region ADAPTER
    public class PaymentPreviewAdapter extends RecyclerView.Adapter<PaymentPreviewAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_preview, parent, false);
            ViewHolder holder = new ViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            OrderWork orderWork = mOrderWorkArrayList.get(position);

            holder.viewDataBinding.setOrderWork(orderWork);

            GlideUtil.loadImageByPhotoSID(holder.viewDataBinding.ivImage, orderWork.getPhotoCover(), true);

            String showProductName = StringUtil.getProductName(orderWork);
            holder.viewDataBinding.tvProductType.setText(showProductName);

            ProductType productType = ProductType.fromValue(orderWork.getTypeID());
            String orderCount;
            switch (productType) {
                case PrintPhoto:
//                case Engraving:
                    orderCount = getString(R.string.num_with_colon, SelectImageUtils.getPrintPhotoOrEngravingOrderCount());
                    holder.viewDataBinding.rltQuantity.setVisibility(View.GONE);
                    holder.viewDataBinding.viewDidiver1.setVisibility(View.GONE);
                    break;
                case PictureFrame:
                    holder.viewDataBinding.rltQuantity.setVisibility(View.GONE);
                    holder.viewDataBinding.viewDidiver1.setVisibility(View.GONE);
                    orderCount = getString(R.string.num_with_colon, orderWork.getOrderCount());
                    break;
                default:
                    orderCount = getString(R.string.num_with_colon, orderWork.getOrderCount());
                    break;
            }
            holder.viewDataBinding.tvNum.setText(orderCount);

//            if (productType == ArtAlbum) {
//                holder.viewDataBinding.rltSpecification.setVisibility(View.VISIBLE);
//                holder.viewDataBinding.viewDidiver2.setVisibility(View.VISIBLE);
//            } else {
//                holder.viewDataBinding.rltSpecification.setVisibility(View.GONE);
//                holder.viewDataBinding.viewDidiver2.setVisibility(View.GONE);
//            }

            //都隐藏
            holder.viewDataBinding.rltSpecification.setVisibility(View.GONE);
            holder.viewDataBinding.viewDidiver2.setVisibility(View.GONE);

            setItemListener(holder, position);
        }

        private void setItemListener(ViewHolder holder, final int position) {
            holder.viewDataBinding.ivMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderWork orderWork = mOrderWorkArrayList.get(position);
                    int orderCount = orderWork.getOrderCount();

                    if (orderCount == 1) {
                        return;
                    }

                    orderWork.setOrderCount(--orderCount);

                    mSelectedCoupon = null;
                    refreshDisplayFee();
                }
            });

            holder.viewDataBinding.ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderWork orderWork = mOrderWorkArrayList.get(position);
                    int orderCount = orderWork.getOrderCount();

                    orderWork.setOrderCount(++orderCount);

                    mSelectedCoupon = null;
                    refreshDisplayFee();
                }
            });

            holder.viewDataBinding.rgSpecification.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    String workFormat = null;
                    switch (checkedId) {
                        case R.id.rbBoutique:
                            workFormat = ProductMaterial.ArtAlbum_Boutique.toString();
                            break;
                        case R.id.rbCrystal:
                            workFormat = ProductMaterial.ArtAlbum_Crystal.toString();
                            break;
                    }
                    OrderWork orderWork = mOrderWorkArrayList.get(position);
                    orderWork.setWorkFormat(workFormat);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mOrderWorkArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ItemPaymentPreviewBinding viewDataBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }
        }
    }
    //endregion
}
