package com.hailan.HaiLanPrint.activity.payment;

import android.content.Intent;
import android.view.View;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.activity.orders.MyOrdersActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityPaymentSuccessBinding;
import com.hailan.HaiLanPrint.models.OrderInfo;
import com.hailan.HaiLanPrint.models.WorkInfo;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.ShareUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;

/**
 * Created by yoghourt on 5/23/16.
 */
public class PaymentSuccessActivity extends ToolbarActivity<ActivityPaymentSuccessBinding> {

    private String shareURL = "";

    @Override
    protected int getContentLayout() {
        return R.layout.activity_payment_success;
    }

    @Override
    protected void initData() {
        OrderInfo orderInfo = getIntent().getParcelableExtra(Constants.KEY_ORDER_INFO);

        mDataBinding.tvPaidAmount.setText(getString(R.string.yuan_amount, StringUtil.toYuanWithoutUnit(orderInfo.getTotalFeeReal())));
        mDataBinding.tvOrderNum.setText(getString(R.string.order_number, orderInfo.getOrderNo()));

        if (MDGroundApplication.sInstance.getChoosedProductType() != null) {
            switch (MDGroundApplication.sInstance.getChoosedProductType()) {
                case PrintPhoto:
                case PictureFrame:
                case Engraving:
                    mDataBinding.lltShare.setVisibility(View.GONE);
                    break;
            }
        }

        WorkInfo workInfo = (WorkInfo) getIntent().getSerializableExtra(Constants.KEY_WORK_INFO);
        if (workInfo != null) {
            int workId = workInfo.getWorkID();
            int userId = workInfo.getUserID();
            shareURL = ShareUtils.createShareURL(String.valueOf(workId), String.valueOf(userId));
        }
    }

    @Override
    protected void setListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.toMainActivity(PaymentSuccessActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        NavUtils.toMainActivity(PaymentSuccessActivity.this);
    }

    //region ACTION
    public void toMainActivityAction(View view) {
        NavUtils.toMainActivity(this);
    }

    public void toMyOrdersActivityAction(View view) {
        Intent intent = new Intent(this, MyOrdersActivity.class);
        intent.putExtra(Constants.KEY_FROM_PAYMENT_SUCCESS, true);
        startActivity(intent);
    }

    public void wechatShareAction(View view) {
        ShareUtils.shareToWechat(this, ShareUtils.getWechatShareParams(this, shareURL, ""));
    }

    public void wechatMomentShareAction(View view) {
        ShareUtils.shareToWechatMoment(this, ShareUtils.getWechatShareParams(this, shareURL, ""));
    }

    public void qqShareAction(View view) {
        ShareUtils.shareToQQ(this, ShareUtils.getQQShareParams(this, shareURL, ""));
    }

    public void weiboShareAction(View view) {
        ShareUtils.shareToWeibo(this, ShareUtils.getWeiBoShareParams(this, shareURL, ""));
    }
    //endregion
}
