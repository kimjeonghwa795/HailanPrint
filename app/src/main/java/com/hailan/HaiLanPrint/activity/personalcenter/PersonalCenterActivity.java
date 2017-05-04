package com.hailan.HaiLanPrint.activity.personalcenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.activity.coupon.MyCouponActivity;
import com.hailan.HaiLanPrint.activity.messagecenter.MessageCenterActivity;
import com.hailan.HaiLanPrint.activity.myworks.MyWorksActivity;
import com.hailan.HaiLanPrint.activity.orders.MyOrdersActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityPersonalCenterBinding;
import com.hailan.HaiLanPrint.greendao.Location;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.Message;
import com.hailan.HaiLanPrint.models.User;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.JsonUtil;
import com.hailan.HaiLanPrint.utils.PreferenceUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yoghourt on 5/30/16.
 */
public class PersonalCenterActivity extends ToolbarActivity<ActivityPersonalCenterBinding> {

    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.ACTION_PUSH.equals(action)) {
                getMessageCount();
            }
        }
    };

    @Override
    protected int getContentLayout() {
        return R.layout.activity_personal_center;
    }

    @Override
    protected void initData() {
        getMessageCount();

        LocalBroadcastManager.getInstance(this).registerReceiver(pushReceiver, new IntentFilter(Constants.ACTION_PUSH));
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = MDGroundApplication.sInstance.getLoginUser();
        MDImage mdImage = new MDImage();
        mdImage.setPhotoID(user.getPhotoID());
        mdImage.setPhotoSID(user.getPhotoSID());
        GlideUtil.loadImageByMDImage(mDataBinding.civAvatar, mdImage, false);
        mDataBinding.tvName.setText(user.getUserNickName());
        if (!StringUtil.isEmpty(user.getPhone())) {
            mDataBinding.tvPhoneNum.setText(user.getPhone());
            mDataBinding.tvPhoneNum.setTextColor(getResources().getColor(R.color.color_text_normal));
        } else {
            mDataBinding.tvPhoneNum.setText(R.string.not_bound);
            mDataBinding.tvPhoneNum.setTextColor(getResources().getColor(R.color.color_389060));
        }
        Location city = MDGroundApplication.sDaoSession.getLocationDao().load((long) user.getCityID());
        Location county = MDGroundApplication.sDaoSession.getLocationDao().load((long) user.getDistrictID());
        String cityStr = "", countyStr = "";
        if (city != null && county != null) {
            cityStr = city.getLocationName();
            countyStr = county.getLocationName();
        }
        cityStr = PreferenceUtils.getPrefString(Constants.KEY_CITY, cityStr);
        countyStr = PreferenceUtils.getPrefString(Constants.KEY_COUNTY, countyStr);
        if (StringUtil.isEmpty(cityStr) || StringUtil.isEmpty(countyStr)) {
            mDataBinding.tvCity.setText(R.string.not_filled);
        } else {
            mDataBinding.tvCity.setText(cityStr + " " + countyStr);
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pushReceiver);
        super.onDestroy();
    }

    /**
     * 更新推送统计UI
     */
    private void getMessageCount() {
        List<Message> messages = JsonUtil.fromJson(PreferenceUtils.getPrefString(Constants.KEY_PUSH_MESSAGE, ""), new TypeToken<List<Message>>() {
        });
        if (messages == null) {
            messages = new ArrayList<>();
        }
        int messageCnt = 0, creditCnt = 0, couponCnt = 0;
        for (Message message : messages) {
            if (message.isMessageType()) {
                messageCnt++;
            } else if (message.isCreditType()) {
                creditCnt++;
            } else if (message.isCouponType()) {
                couponCnt++;
            }
        }
        if (messageCnt > 0) {
            mDataBinding.messageCenterDot.setVisibility(View.VISIBLE);
            mDataBinding.messageCenterDot.setText(String.valueOf(messageCnt));
        } else {
            mDataBinding.messageCenterDot.setVisibility(View.INVISIBLE);
        }

        if (creditCnt > 0) {
            mDataBinding.creditDot.setVisibility(View.VISIBLE);
            mDataBinding.creditDot.setText(String.valueOf(creditCnt));
        } else {
            mDataBinding.creditDot.setVisibility(View.INVISIBLE);
        }

        if (couponCnt > 0) {
            mDataBinding.couponDot.setVisibility(View.VISIBLE);
            mDataBinding.couponDot.setText(String.valueOf(couponCnt));
        } else {
            mDataBinding.couponDot.setVisibility(View.INVISIBLE);
        }
    }

    //region ACTION
    public void toPersonalInformationAction(View view) {
        Intent intent = new Intent(this, PersonalInformationActivity.class);
        startActivity(intent);
    }

    public void toMyOrdersActivityAction(View view) {
        Intent intent = new Intent(this, MyOrdersActivity.class);
        startActivity(intent);
    }

    public void toMyCouponActivityAction(View view) {
        Intent intent = new Intent(this, MyCouponActivity.class);
        startActivity(intent);
    }

    //设置
    public void toSettingActivityAction(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    //积分查询
    public void tOMyCreditActivity(View v) {
        Intent intent = new Intent(this, MyCreditActivity.class);
        startActivity(intent);
    }

    //我的作品
    public void toMyWorksActivity(View view) {
        Intent intent = new Intent(this, MyWorksActivity.class);
        startActivity(intent);
    }

    //去消息中心
    public void toMessageCenter(View view) {
        Intent intent = new Intent(this, MessageCenterActivity.class);
        startActivity(intent);
    }
    //endregion

}
