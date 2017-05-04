package com.hailan.HaiLanPrint.activity.coupon;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityMyCouponBinding;
import com.hailan.HaiLanPrint.databinding.ItemMyCouponBinding;
import com.hailan.HaiLanPrint.models.Coupon;
import com.hailan.HaiLanPrint.models.Message;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.DateUtils;
import com.hailan.HaiLanPrint.utils.JsonUtil;
import com.hailan.HaiLanPrint.utils.PreferenceUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/23/16.
 */
public class MyCouponActivity extends ToolbarActivity<ActivityMyCouponBinding> {

    private MyCouponAdapter mAdapter;

    private ArrayList<Coupon> mAllCouponArrayList = new ArrayList<>();
    private ArrayList<Coupon> mShowCouponArrayList = new ArrayList<>();

    private boolean mIsAvailable = true;//默认是"可使用"

    @Override
    protected int getContentLayout() {
        return R.layout.activity_my_coupon;
    }

    @Override
    protected void initData() {
        mDataBinding.tabLayout.addTab(mDataBinding.tabLayout.newTab().setText(getString(R.string.available_coupon, 0)));
        mDataBinding.tabLayout.addTab(mDataBinding.tabLayout.newTab().setText(getString(R.string.unavailable_coupon, 0)));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataBinding.recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyCouponAdapter();
        mDataBinding.recyclerView.setAdapter(mAdapter);

        getUserCouponListRequest();
    }

    @Override
    protected void onDestroy() {
        List<Message> messages = JsonUtil.fromJson(PreferenceUtils.getPrefString(Constants.KEY_PUSH_MESSAGE, ""), new TypeToken<List<Message>>() {
        });
        if (messages == null) {
            messages = new ArrayList<>();
        }
        List<Message> deleteList = new ArrayList<>();
        for (Message message : messages) {
            if (message.isCouponType()) {
                deleteList.add(message);
            }
        }
        messages.removeAll(deleteList);
        PreferenceUtils.setPrefString(Constants.KEY_PUSH_MESSAGE, JsonUtil.toJson(messages));

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.ACTION_PUSH));

        super.onDestroy();
    }

    @Override
    protected void setListener() {
        mDataBinding.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int currentSelectedTabIndex = tab.getPosition();

                if (currentSelectedTabIndex == 0) {
                    mIsAvailable = true; // 可使用
                } else {
                    mIsAvailable = false; // 不可以使用
                }

                refreshRecyclerView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void refreshRecyclerView() {
        mShowCouponArrayList.clear();
        for (Coupon coupon : mAllCouponArrayList) {
            // 判断优惠券是否可用条件：当前时间在ExpireTime之前
            boolean couponStatus = DateUtils.isBeforeExpireTime(coupon.getExpireTime());
            boolean isCouponStatusAvailable = coupon.getCouponStatus() == 0;

            if (mIsAvailable) {//可用
                if (couponStatus && isCouponStatusAvailable) {
                    mShowCouponArrayList.add(coupon);
                }
            } else {//不可用
                if (!couponStatus || !isCouponStatusAvailable) {
                    mShowCouponArrayList.add(coupon);
                }
            }
        }

        if (mShowCouponArrayList.size() > 0) {
            mDataBinding.ivEmpty.setVisibility(View.GONE);
            mDataBinding.recyclerView.setVisibility(View.VISIBLE);

            mAdapter.notifyDataSetChanged();
        } else {
            mDataBinding.ivEmpty.setVisibility(View.VISIBLE);
            mDataBinding.recyclerView.setVisibility(View.GONE);
        }
    }

    //region ACTION
    public void activateCouponAction(View view) {
        String activationCode = mDataBinding.cetActivationCode.getText().toString();

        if (StringUtil.isEmpty(activationCode)) {
            ViewUtils.toast(R.string.input_activation_code);
            return;
        }

        ViewUtils.loading(this);
        GlobalRestful.getInstance().ActivatingCoupon(activationCode, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {

                getUserCouponListRequest();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }
    //endregion

    //region SERVER
    private void getUserCouponListRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetUserCouponList(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                mAllCouponArrayList = response.body().getContent(new TypeToken<ArrayList<Coupon>>() {
                });

                int availableCount = 0;
                int unavailableCount = 0;

                for (Coupon coupon : mAllCouponArrayList) {
                    boolean couponStatus = DateUtils.isBeforeExpireTime(coupon.getExpireTime());
                    boolean isCouponStatusAvailable = coupon.getCouponStatus() == 0;

                    if (couponStatus && isCouponStatusAvailable) {
                        availableCount++;
                    } else {
                        unavailableCount++;
                    }
                }

                mDataBinding.tabLayout.getTabAt(0).setText(getString(R.string.available_coupon, availableCount));
                mDataBinding.tabLayout.getTabAt(1).setText(getString(R.string.unavailable_coupon, unavailableCount));

                refreshRecyclerView();

                ViewUtils.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }
    //endregion

    //region ADAPTER
    public class MyCouponAdapter extends RecyclerView.Adapter<MyCouponAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_coupon, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.viewDataBinding.setCoupon(mShowCouponArrayList.get(position));
            if (mIsAvailable) {
                holder.viewDataBinding.couponBg.setBackgroundResource(R.drawable.bg_wdgrzx);
            } else {
                if (mShowCouponArrayList.get(position).getCouponStatus() == 2) {
                    holder.viewDataBinding.couponBg.setBackgroundResource(R.drawable.bg_wdgrzx_has_use);
                } else {
                    holder.viewDataBinding.couponBg.setBackgroundResource(R.drawable.bg_wdgrzx_beoverdue);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mShowCouponArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ItemMyCouponBinding viewDataBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }
        }
    }
    //endregion

}
