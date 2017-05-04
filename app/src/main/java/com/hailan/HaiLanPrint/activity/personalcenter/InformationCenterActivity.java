package com.hailan.HaiLanPrint.activity.personalcenter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.databinding.ActivityInformationCenterBinding;
import com.hailan.HaiLanPrint.databinding.ItemInformationCenterBinding;
import com.hailan.HaiLanPrint.models.InformationInfo;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by PC on 2016-06-17.
 */

public class InformationCenterActivity extends ToolbarActivity<ActivityInformationCenterBinding> {

    private List<InformationInfo> mInformationList = new ArrayList<>();
    private int mPageIndex = 0;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean mIsLoadeMore;
    private InformationAdapter mAdapter;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_information_center;
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetUserMessageListRequset(mPageIndex);
        if (mInformationList.size() == 0) {
            mDataBinding.lltNofind.setVisibility(View.VISIBLE);
            mDataBinding.recyclerView.setVisibility(View.GONE);
        } else {
            mDataBinding.lltNofind.setVisibility(View.GONE);
            mDataBinding.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {
        mRecyclerView = new RecyclerView(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new InformationAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLinearLayoutManager.getItemCount();
                if (lastVisibleItem == totalItemCount) {
                    if (mIsLoadeMore) {
                        GetUserMessageListRequset(mPageIndex);
                        mAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
    }

    //region SEVER
    public void GetUserMessageListRequset(int index) {
        GlobalRestful.getInstance().GetUserMessageList(mPageIndex, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                List<InformationInfo> list = response.body().getContent(new TypeToken<List<InformationInfo>>() {
                });
                mInformationList.addAll(list);
                if (list != null) {
                    if (list.size() < 20) {
                        mIsLoadeMore = false;
                    } else {
                        mIsLoadeMore = true;
                        mPageIndex++;
                    }
                } else {
                    mIsLoadeMore = false;
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    //endregion

    //region ADAPTER
    public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(InformationCenterActivity.this).inflate(R.layout.item_information_center, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            InformationInfo informationInfo = mInformationList.get(position);
            holder.itemInformationCenterBinding.tvCreateTime.setText(informationInfo.getCreatedTime());
            holder.itemInformationCenterBinding.tvMessage.setText(informationInfo.getMessage());
        }

        @Override
        public int getItemCount() {
            return mInformationList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ItemInformationCenterBinding itemInformationCenterBinding;

            public MyViewHolder(View itemView) {
                super(itemView);
                itemInformationCenterBinding = DataBindingUtil.bind(itemView);
            }
        }
    }
    //endregion
}
