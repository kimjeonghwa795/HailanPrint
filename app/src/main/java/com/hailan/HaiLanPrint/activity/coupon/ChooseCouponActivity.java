package com.hailan.HaiLanPrint.activity.coupon;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityChooseCouponBinding;
import com.hailan.HaiLanPrint.databinding.ItemChooseCouponBinding;
import com.hailan.HaiLanPrint.models.Coupon;
import com.hailan.HaiLanPrint.utils.ViewUtils;

import java.util.ArrayList;

/**
 * Created by yoghourt on 5/23/16.
 */

public class ChooseCouponActivity extends ToolbarActivity<ActivityChooseCouponBinding> {

    private ChooseCouponAdapter mAdapter;

    private ArrayList<Coupon> mAvailableCouponArrayList = new ArrayList<>();

    private Coupon mSelectedCoupon;

    private int mOrderAmountFee;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_choose_coupon;
    }

    @Override
    protected void initData() {
        mSelectedCoupon = getIntent().getParcelableExtra(Constants.KEY_SELECTED_COUPON);
        mAvailableCouponArrayList = getIntent().getParcelableArrayListExtra(Constants.KEY_COUPON_LIST);
        mOrderAmountFee = getIntent().getIntExtra(Constants.KEY_ORDER_AMOUNT_FEE, 0);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataBinding.recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ChooseCouponAdapter();
        mDataBinding.recyclerView.setAdapter(mAdapter);

        refreshRecyclerView(mAvailableCouponArrayList);
    }

    @Override
    protected void setListener() {

    }

    private void refreshRecyclerView(ArrayList<Coupon> allCouponArrayList) {
        if (mAvailableCouponArrayList.size() > 0) {
            mDataBinding.lltEmpty.setVisibility(View.GONE);
            mDataBinding.recyclerView.setVisibility(View.VISIBLE);

            mAdapter.notifyDataSetChanged();
        } else {
            mDataBinding.lltEmpty.setVisibility(View.VISIBLE);
            mDataBinding.recyclerView.setVisibility(View.GONE);
        }
    }

    //region ACTION
    public void onCouponSelectAction(View view) {
        int position = mDataBinding.recyclerView.getChildAdapterPosition(view);

        Coupon coupon = mAvailableCouponArrayList.get(position);
        if (coupon.getPriceLimit() <= mOrderAmountFee) {
            Intent intent = new Intent();
            intent.putExtra(Constants.KEY_SELECTED_COUPON, coupon);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            ViewUtils.toast(R.string.not_match_coupon_condition);
        }
    }
    //endregion

    //region ADAPTER
    public class ChooseCouponAdapter extends RecyclerView.Adapter<ChooseCouponAdapter.ViewHolder> {

        @Override
        public ChooseCouponAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_choose_coupon, parent, false);
            ChooseCouponAdapter.ViewHolder holder = new ChooseCouponAdapter.ViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(ChooseCouponAdapter.ViewHolder holder, int position) {
            holder.viewDataBinding.setCoupon(mAvailableCouponArrayList.get(position));
            holder.viewDataBinding.setHandlers(ChooseCouponActivity.this);
            boolean isSelected = false;
            if (mSelectedCoupon != null) {
                if (mAvailableCouponArrayList.get(position).getAutoID() == mSelectedCoupon.getAutoID()) {
                    isSelected = true;
                }
            }
            holder.viewDataBinding.setIsSelected(isSelected);
        }

        @Override
        public int getItemCount() {
            return mAvailableCouponArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ItemChooseCouponBinding viewDataBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }
        }
    }
    //endregion

}
