package com.hailan.HaiLanPrint.activity.personalcenter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityMyWorksBinding;
import com.hailan.HaiLanPrint.databinding.ItemMyworksBinding;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.OrderInfo;
import com.hailan.HaiLanPrint.models.OrderWork;
import com.hailan.HaiLanPrint.models.WorkInfo;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.OrderUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by PC on 2016-06-09.
 */
public class MyWorksActivity extends ToolbarActivity<ActivityMyWorksBinding> {

    private List<WorkInfo> mAllWorkInfoList = new ArrayList<>();
    private List<WorkInfo> mSelectedWorkInfoList = new ArrayList<>();
    private MyWorksAdapter mAdapter;
    private boolean mIsEditMode = true;
    private boolean mFlag = false;//用于标记显示哪一张头部背景图

    @Override
    protected int getContentLayout() {
        return R.layout.activity_my_works;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserWorkListRequest();
    }

    @Override
    protected void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataBinding.myworksRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyWorksAdapter();
        mDataBinding.myworksRecyclerView.setAdapter(mAdapter);

        countPriceAndAmount();
    }

    @Override
    protected void setListener() {
        //编辑按钮
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsEditMode) {
                    mIsEditMode = true;
                    tvRight.setText(R.string.finish);
                    mDataBinding.llTotalprice.setVisibility(View.VISIBLE);
                    mDataBinding.tvBuy.setText(R.string.purchase);
                    mDataBinding.lltAmount.setBackgroundResource(R.color.colorOrange);
                } else {
                    mIsEditMode = false;
                    tvRight.setText(R.string.edit);
                    mDataBinding.llTotalprice.setVisibility(View.INVISIBLE);
                    mDataBinding.tvBuy.setText(R.string.delete);
                    mDataBinding.lltAmount.setBackgroundResource(R.color.colorRed);
                }
            }
        });

        //全选按钮
        mDataBinding.cbSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataBinding.cbSelectAll.isChecked()) {
                    mSelectedWorkInfoList = new ArrayList<WorkInfo>(mAllWorkInfoList);
                    float totalPrice = 0;
                    for (int i = 0; i < mSelectedWorkInfoList.size(); i++) {

                    }
                } else {
                    mSelectedWorkInfoList.clear();
                    mDataBinding.tvTotalPrice.setText("0.00");
                    mDataBinding.tvAmunt.setText("(0)");
                }
                countPriceAndAmount();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    //计算价格和数量并显示
    private void countPriceAndAmount() {
        if (mSelectedWorkInfoList.size() > 0) {
            float total = 0;
            int amunt = 0;
            for (int i = 0; i < mSelectedWorkInfoList.size(); i++) {
                total = mSelectedWorkInfoList.get(i).getPrice() + total;
            }
            mDataBinding.tvAmunt.setText("(" + mSelectedWorkInfoList.size() + ")");
            mDataBinding.tvTotalPrice.setText(StringUtil.toYuanWithoutUnit(total));
        } else {
            mDataBinding.tvAmunt.setText("(0)");
            mDataBinding.tvTotalPrice.setText("0.00");
        }
    }

    private boolean hadSelectAllOfSpecificTypeIDWork(WorkInfo specificWorkInfo) {
        int allWorkOfSpecificTypeIDCount = 0;
        for (WorkInfo workInfo : mAllWorkInfoList) {
            if (workInfo.getTypeID() == specificWorkInfo.getTypeID()) {
                allWorkOfSpecificTypeIDCount++;
            }
        }

        int selectedWorkOfSpecificTypeIDCount = 0;
        for (WorkInfo workInfo : mSelectedWorkInfoList) {
            if (workInfo.getTypeID() == specificWorkInfo.getTypeID()) {
                selectedWorkOfSpecificTypeIDCount++;
            }
        }

        if (allWorkOfSpecificTypeIDCount == selectedWorkOfSpecificTypeIDCount) {
            return true;
        } else {
            return false;
        }
    }

    //选中同一组内的作品
    private void selectGroupWorks(WorkInfo workInfo, boolean isSelected) {
        int selectType = workInfo.getTypeID();

        if (isSelected) {
            Iterator<WorkInfo> iterator = mSelectedWorkInfoList.iterator();
            while (iterator.hasNext()) {

                WorkInfo item = iterator.next();
                if (item.getTypeID() == selectType) {
                    iterator.remove();
                }
            }

            for (WorkInfo item : mAllWorkInfoList) {
                if (item.getTypeID() == selectType) {
                    mSelectedWorkInfoList.add(item);
                }
            }

            if (mSelectedWorkInfoList.size() == mAllWorkInfoList.size()) {
                mDataBinding.cbSelectAll.setChecked(true);
            }
            countPriceAndAmount();
        } else {
            mDataBinding.cbSelectAll.setChecked(false);

            Iterator<WorkInfo> iterator = mSelectedWorkInfoList.iterator();
            while (iterator.hasNext()) {

                WorkInfo item = iterator.next();
                if (item.getTypeID() == selectType) {
                    iterator.remove();
                }
            }

            countPriceAndAmount();
        }

        mAdapter.notifyDataSetChanged();
    }

    //选择指定作品
    private void selectSingleWork(WorkInfo workInfo, boolean isSelected) {
        int selectWorkID = workInfo.getWorkID();

        if (isSelected) {
            for (int i = 0; i < mSelectedWorkInfoList.size(); i++) {
                if (mSelectedWorkInfoList.get(i).getWorkID() == selectWorkID) {
                    return;
                }
            }
            mSelectedWorkInfoList.add(workInfo);
            if (mSelectedWorkInfoList.size() == mAllWorkInfoList.size()) {
                mDataBinding.cbSelectAll.setChecked(true);
            }
            countPriceAndAmount();
        } else {
            for (WorkInfo item : mSelectedWorkInfoList) {
                if (item.getWorkID() == selectWorkID) {
                    mSelectedWorkInfoList.remove(item);
                    break;
                }
            }
            countPriceAndAmount();
            mDataBinding.cbSelectAll.setChecked(false);
        }

        mAdapter.notifyDataSetChanged();
    }

    //跳到作品详情页
    private void toWorkDetailsActivity(WorkInfo workInfo) {
        Intent intent = new Intent(this, WorkDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_WORKS_DETAILS, workInfo);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //region ACTION
    //购买或者删除按钮
    public void toBuyOrDeleteAction(View view) {
        if (mIsEditMode) {
            //购买功能
            boolean isAllSameType = true;
            for (int i = 1; i < mSelectedWorkInfoList.size(); i++) {
                if (mSelectedWorkInfoList.get(i - 1).getTypeID() != mSelectedWorkInfoList.get(i).getTypeID()) {
                    isAllSameType = false;
                    break;
                }
            }

            if (!isAllSameType) {
                ViewUtils.toast(R.string.please_select_same_type);
                return;
            }

            List<Integer> workIDList = new ArrayList<>();
            for (int i = 0; i < mSelectedWorkInfoList.size(); i++) {
                int workID = mSelectedWorkInfoList.get(i).getWorkID();
                workIDList.add(workID);
            }

            saveOrderByWorkRequest(workIDList);
        } else {
            //删除功能
            List<Integer> workIDList = new ArrayList<>();
            for (int i = 0; i < mSelectedWorkInfoList.size(); i++) {
                int workID = mSelectedWorkInfoList.get(i).getWorkID();
                workIDList.add(workID);
            }

            deleteUserWorkRequest(workIDList);
        }
    }
    //endregion

    //region SEVER
    //删除掉选中列表
    public void deleteUserWorkRequest(List<Integer> WorkIDList) {
        GlobalRestful.getInstance().DeleteUserWork(WorkIDList, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {

            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    //获取作品列表
    public void getUserWorkListRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetUserWorkList(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (ResponseCode.isSuccess(response.body())) {
                    mAllWorkInfoList = response.body().getContent(new TypeToken<List<WorkInfo>>() {
                    });

                    Collections.sort(mAllWorkInfoList);

                    if (mAllWorkInfoList.size() > 0) {
                        tvRight.setText(R.string.edit);
                        tvRight.setVisibility(View.VISIBLE);
                        mDataBinding.llEnough.setVisibility(View.VISIBLE);
                        mDataBinding.llBlank.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        tvRight.setVisibility(View.GONE);
                        mDataBinding.llBlank.setVisibility(View.VISIBLE);
                        mDataBinding.llEnough.setVisibility(View.GONE);
                    }

                    ViewUtils.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }

    private void saveOrderByWorkRequest(List<Integer> workIDList) {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().SaveOrderByWork(workIDList, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (ResponseCode.isSuccess(response.body())) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().getContent());

                        OrderInfo orderInfo = StringUtil.getInstanceByJsonString(jsonObject.getString("OrderInfo"),
                                OrderInfo.class);
                        ArrayList<OrderWork> orderWorkArrayList = StringUtil.getInstanceByJsonString(
                                jsonObject.getString("OrderWorkList"),
                                new TypeToken<ArrayList<OrderWork>>() {
                                });

                        MDGroundApplication.sOrderutUtils = new OrderUtils(orderInfo, orderWorkArrayList);
                        ProductType productType = ProductType.fromValue(orderWorkArrayList.get(0).getTypeID());
                        MDGroundApplication.sInstance.setChoosedProductType(productType);

                        NavUtils.toPaymentPreviewActivity(MyWorksActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }
    //endregion

    //region ADAPTER
    public class MyWorksAdapter extends RecyclerView.Adapter<MyWorksAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myworks, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            WorkInfo workInfo = mAllWorkInfoList.get(position);
            holder.itemMyworksBinding.setWorkInfo(workInfo);
            GlideUtil.loadImageByPhotoSID(holder.itemMyworksBinding.ivImage, workInfo.getPhotoCover(), true);
            holder.itemMyworksBinding.tvWorksPice.setText(StringUtil.toYuanWithoutUnit(workInfo.getPrice()));
            if (isShowHeader(position)) {
                holder.itemMyworksBinding.rltHeader.setVisibility(View.VISIBLE);
                if (mFlag) {
                    mFlag = false;
                    holder.itemMyworksBinding.ivHeaderBack.setImageResource(R.drawable.bg_work_onehedr);
                } else {
                    mFlag = true;
                    holder.itemMyworksBinding.ivHeaderBack.setImageResource(R.drawable.bg_work_twoheader);
                }
            } else {
                holder.itemMyworksBinding.rltHeader.setVisibility(View.GONE);
            }
            holder.itemMyworksBinding.cbItem.setChecked(false);
            for (WorkInfo item : mSelectedWorkInfoList) {
                if (item.getWorkID() == workInfo.getWorkID()) {
                    holder.itemMyworksBinding.cbItem.setChecked(true);
                    break;
                }
            }

            holder.itemMyworksBinding.cbHeader.setChecked(false);
            if (isShowHeader(position)) {
                if (hadSelectAllOfSpecificTypeIDWork(workInfo)) {
                    holder.itemMyworksBinding.cbHeader.setChecked(true);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mAllWorkInfoList.size();
        }

        public boolean isShowHeader(int positon) {
            if (positon == 0) {
                return true;
            } else {
                WorkInfo currentWorkInfo = mAllWorkInfoList.get(positon);
                WorkInfo previousWorkInfo = mAllWorkInfoList.get(positon - 1);
                if (currentWorkInfo.getTypeID() != previousWorkInfo.getTypeID()) {
                    return true;
                }
            }
            return false;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ItemMyworksBinding itemMyworksBinding;

            public ViewHolder(final View itemView) {
                super(itemView);
                itemMyworksBinding = DataBindingUtil.bind(itemView);

                itemMyworksBinding.cbHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WorkInfo workInfo = mAllWorkInfoList.get(getAdapterPosition());

                        selectGroupWorks(workInfo, itemMyworksBinding.cbHeader.isChecked());
                    }
                });

                itemMyworksBinding.cbItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WorkInfo workInfo = mAllWorkInfoList.get(getAdapterPosition());

                        selectSingleWork(workInfo, itemMyworksBinding.cbItem.isChecked());
                    }
                });
                itemMyworksBinding.lltDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WorkInfo workInfo = mAllWorkInfoList.get(getAdapterPosition());
                        toWorkDetailsActivity(workInfo);
                    }
                });
            }
        }

    }
    //endregion
}
