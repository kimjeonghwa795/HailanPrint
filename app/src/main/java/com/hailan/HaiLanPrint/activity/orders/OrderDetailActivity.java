package com.hailan.HaiLanPrint.activity.orders;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityOrderDetailBinding;
import com.hailan.HaiLanPrint.databinding.ItemApplyRefundBinding;
import com.hailan.HaiLanPrint.databinding.ItemOrderDetailBinding;
import com.hailan.HaiLanPrint.enumobject.OrderStatus;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.OrderInfo;
import com.hailan.HaiLanPrint.models.OrderWork;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.DateUtils;
import com.hailan.HaiLanPrint.utils.EncryptUtil;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.PreferenceUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.itemdecoration.GridSpacingItemDecoration;
import com.socks.library.KLog;

import java.net.URLEncoder;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/30/16.
 */
public class OrderDetailActivity extends ToolbarActivity<ActivityOrderDetailBinding> {

    private OrderInfo mOrderInfo;

    private OrderStatus mOrderStatus;

    private ArrayList<OrderWork> mOrderWorkArrayList = new ArrayList<>();

    private ArrayList<MDImage> mUploadImageArrayList = new ArrayList<>();

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void initData() {
        mOrderWorkArrayList = getIntent().getParcelableArrayListExtra(Constants.KEY_ORDER_WORK_LIST);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataBinding.productRecyclerView.setLayoutManager(layoutManager);

        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter();
        mDataBinding.productRecyclerView.setAdapter(orderDetailAdapter);

        mOrderInfo = getIntent().getParcelableExtra(Constants.KEY_ORDER_INFO);

        mDataBinding.tvReceiver.setText(mOrderInfo.getReceiver());
        mDataBinding.tvPhone.setText(mOrderInfo.getPhone());
        mDataBinding.tvAddress.setText(mOrderInfo.getAddressReceipt());
        mDataBinding.tvOrder.setText(mOrderInfo.getOrderNo());

        mOrderStatus = OrderStatus.fromValue(mOrderInfo.getOrderStatus());
        mDataBinding.tvOrderStatus.setText(OrderStatus.getOrderStatus(this, mOrderStatus));

        switch (mOrderStatus) {
            case Paid:
                mDataBinding.lltMoreDetail.setVisibility(View.GONE);
                mDataBinding.btnOperation.setVisibility(View.GONE);
                if (DateUtils.isAfter12Hours(mOrderInfo.getCreatedTime())) {
                    mDataBinding.btnApplyRefund.setVisibility(View.GONE);
                }
                mDataBinding.lltOperation.setVisibility(View.GONE);
                break;
            case Delivered:
                mDataBinding.tvDetail1Title.setText(R.string.express_company_with_colon);
                mDataBinding.tvDetail1.setText(mOrderInfo.getExpressCompany());

                mDataBinding.tvDetail2Title.setText(R.string.express_number_with_colon);
                mDataBinding.tvDetail2.setText(mOrderInfo.getExpressNo());

                mDataBinding.rltUploadImage.setVisibility(View.GONE);

                mDataBinding.btnApplyRefund.setText("");
                mDataBinding.btnApplyRefund.setEnabled(false);
                mDataBinding.btnApplyRefund.setVisibility(View.GONE);

                mDataBinding.btnOperation.setText(R.string.confirm_receive);
                break;
            case Finished:
                mDataBinding.lltMoreDetail.setVisibility(View.GONE);
                mDataBinding.lltOperation.setVisibility(View.GONE);
                break;
            case Refunding:
            case RefundFail:
                mDataBinding.tvOrderStatusLogo.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_refund_pay),
                        null, null, null);
                mDataBinding.tvOrderStatusLogo.setText(R.string.refund_info);

                mDataBinding.tvDetail1Title.setText(R.string.refund_detail_with_colon);
                mDataBinding.tvDetail1.setText(mOrderInfo.getRefundReason());

                mDataBinding.tvDetail2Title.setText(R.string.refund_amount_with_colon);
                mDataBinding.tvDetail2.setText(getString(R.string.yuan_amount, StringUtil.toYuanWithoutUnit(mOrderInfo.getRefundFee())));

                // 上传图片
                if (mOrderInfo.getRefundPhoto1SID() != 0) {
                    MDImage mdImage = new MDImage();
                    mdImage.setPhotoSID(mOrderInfo.getRefundPhoto1SID());
                    mUploadImageArrayList.add(mdImage);
                }
                if (mOrderInfo.getRefundPhoto2SID() != 0) {
                    MDImage mdImage = new MDImage();
                    mdImage.setPhotoSID(mOrderInfo.getRefundPhoto2SID());
                    mUploadImageArrayList.add(mdImage);
                }
                if (mOrderInfo.getRefundPhoto3SID() != 0) {
                    MDImage mdImage = new MDImage();
                    mdImage.setPhotoSID(mOrderInfo.getRefundPhoto3SID());
                    mUploadImageArrayList.add(mdImage);
                }

                if (mUploadImageArrayList.size() > 0) {
                    mDataBinding.uploadImageRecyclerView.setHasFixedSize(true);
                    mDataBinding.uploadImageRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, ViewUtils.dp2px(2), false));
                    mDataBinding.uploadImageRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

                    UploadImageAdapter uploadImageAdapter = new UploadImageAdapter();
                    mDataBinding.uploadImageRecyclerView.setAdapter(uploadImageAdapter);
                }

                mDataBinding.lltOperation.setVisibility(View.GONE);
                break;
        }

        mDataBinding.tvAmount.setText(StringUtil.toYuanWithoutUnit(mOrderInfo.getTotalFee()));
        mDataBinding.tvIntegralFee.setText(mOrderInfo.getIntegralFeeString());
        mDataBinding.tvCouponFee.setText(StringUtil.toYuanWithoutUnit(mOrderInfo.getCouponFee()));
        mDataBinding.tvFreightFee.setText(StringUtil.toYuanWithoutUnit(mOrderInfo.getDeliveryFee()));
        mDataBinding.tvRealFee.setText(getString(R.string.actual_payment_with_colon)
                + "  " + StringUtil.toYuanWithoutUnit(mOrderInfo.getTotalFeeReal()));
        mDataBinding.tvOrderTime.setText(getString(R.string.order_time_with_colon, mOrderInfo.getCreatedTime()));
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    public void onExpressDetailClick(View view) {
        WebViewActivity.queryExpress(this, mOrderInfo.getExpressCompany(), mOrderInfo.getExpressNo());
    }

    public String createOrderDetailURL(int orderId) {
        String orderDetailUrl = null;
        String encryptOrderIdString = null;
        try {
            encryptOrderIdString = EncryptUtil.encrypt(String.valueOf(orderId));
            encryptOrderIdString = URLEncoder.encode(encryptOrderIdString, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        orderDetailUrl = Constants.HOST + "ShareOrderPhoto.aspx?orderId=" + encryptOrderIdString;

        String InvitationCode = PreferenceUtils.getPrefString(Constants.KEY_INVITATIONCODE, "");
        if (!"".equals(InvitationCode)) {
            orderDetailUrl += "&InvitationCode=" + InvitationCode;
        }

        KLog.e(orderDetailUrl);
        return orderDetailUrl;
    }

    //region ACTION
    public void btnOperationAction(View view) {
        switch (mOrderStatus) {
            case Paid:
                Intent intent = new Intent(OrderDetailActivity.this, ApplyRefundActivity.class);
                intent.putExtra(Constants.KEY_ORDER_INFO, mOrderInfo);
                startActivityForResult(intent, 0);
                break;
            case Delivered:
                updateOrderFinishedRequest(mOrderInfo.getOrderID());
                break;
        }
    }
    //endregion

    //region SERVER
    private void updateOrderFinishedRequest(int orderID) {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().UpdateOrderFinished(orderID, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                finish();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }
    //endregion

    //region ADAPTER
    public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
            ViewHolder holder = new ViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final OrderWork orderWork = mOrderWorkArrayList.get(position);

            holder.viewDataBinding.setOrderWork(orderWork);
            GlideUtil.loadImageByPhotoSID(holder.viewDataBinding.ivImage, orderWork.getPhotoCover(), true);

            if ("".equals(orderWork.getWorkMaterial()) || "null".equals(orderWork.getWorkMaterial())) {
                holder.viewDataBinding.tvformat.setText(orderWork.getWorkFormat());
            } else {
                holder.viewDataBinding.tvformat.setText(orderWork.getWorkMaterial() + " " + orderWork.getWorkFormat());
            }

            holder.viewDataBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrderDetailActivity.this, ProductionInfoActivity.class);
                    String orderUrl = createOrderDetailURL(orderWork.getOrderID());
                    intent.putExtra(Constants.KEY_ORDER_URL, orderUrl);
                    startActivity(intent);
                }
            });

            if (orderWork.getTypeID() == ProductType.PrintPhoto.value()) {
                holder.viewDataBinding.tvNum.setText(getString(R.string.num_with_colon, orderWork.getPhotoCount()));
            } else {
                holder.viewDataBinding.tvNum.setText(getString(R.string.num_with_colon, orderWork.getOrderCount()));
            }
        }

        @Override
        public int getItemCount() {
            return mOrderWorkArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ItemOrderDetailBinding viewDataBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }
        }
    }


    public class UploadImageAdapter extends RecyclerView.Adapter<UploadImageAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apply_refund, parent, false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.viewDataBinding.setImage(mUploadImageArrayList.get(position));
            GlideUtil.loadImageByMDImage(holder.viewDataBinding.ivImage, mUploadImageArrayList.get(position), true);
        }

        @Override
        public int getItemCount() {
            return mUploadImageArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemApplyRefundBinding viewDataBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }
        }
    }

    //endregion
}
