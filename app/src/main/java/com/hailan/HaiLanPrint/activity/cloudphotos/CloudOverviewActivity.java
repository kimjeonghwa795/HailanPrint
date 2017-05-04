package com.hailan.HaiLanPrint.activity.cloudphotos;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.databinding.ActivityCloudOverviewBinding;
import com.hailan.HaiLanPrint.databinding.ItemCloudOverviewBinding;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.itemdecoration.DividerItemDecoration;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/11/16.
 */
public class CloudOverviewActivity extends ToolbarActivity<ActivityCloudOverviewBinding> {

    private CloudOverviewAdapter mAdapter;

    private ArrayList<MDImage> mImagesList = new ArrayList<MDImage>();

    @Override
    protected int getContentLayout() {
        return R.layout.activity_cloud_overview;
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPhotoCountRequest();
    }

    @Override
    protected void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataBinding.recyclerView.setLayoutManager(layoutManager);
        mDataBinding.recyclerView.addItemDecoration(new DividerItemDecoration(0));

        mAdapter = new CloudOverviewAdapter();
        mDataBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {

    }

    //region SERVER
    private void getPhotoCountRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetCloudPhotoCount(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                if (ResponseCode.isSuccess(response.body())) {
                    mImagesList.clear();

                    ArrayList<MDImage> tempImagesList = response.body().getContent(new TypeToken<ArrayList<MDImage>>() {
                    });

                    mImagesList.addAll(tempImagesList);

                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ViewUtils.dismiss();
            }
        });
    }
    //endregion

    public class BindingHandler {

        public void toCloudDetailActivityAction(View view) {
            int position = mDataBinding.recyclerView.getChildAdapterPosition(view);

            MDImage mdImage = mImagesList.get(position);
            NavUtils.toCloudDetailActivity(view.getContext(), mdImage);
        }
    }

    private class CloudOverviewAdapter extends RecyclerView.Adapter<CloudOverviewAdapter.ViewHolder> {

        private BindingHandler bindingHandler = new BindingHandler();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cloud_overview, parent, false);
            ViewHolder holder = new ViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.viewDataBinding.setImage(mImagesList.get(position));
            holder.viewDataBinding.setHandlers(bindingHandler);
            GlideUtil.loadImageByMDImage(holder.viewDataBinding.firstImage, mImagesList.get(position), true);
        }

        @Override
        public int getItemCount() {
            return mImagesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ItemCloudOverviewBinding viewDataBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }
        }
    }
}
