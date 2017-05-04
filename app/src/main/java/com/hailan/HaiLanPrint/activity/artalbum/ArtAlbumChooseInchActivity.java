package com.hailan.HaiLanPrint.activity.artalbum;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.activity.phototype.PhotoTypeActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ActivityArtAlbumChooseInchBinding;
import com.hailan.HaiLanPrint.databinding.ItemArtAlbumChooseInchBinding;
import com.hailan.HaiLanPrint.enumobject.PhotoExplainTypeEnum;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.Measurement;
import com.hailan.HaiLanPrint.models.PhotoTypeExplain;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.itemdecoration.DividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/11/16.
 */
public class ArtAlbumChooseInchActivity extends ToolbarActivity<ActivityArtAlbumChooseInchBinding> {

    private ArtAlbumChooseInchAdapter mAdapter;

    private ArrayList<Measurement> mSpecList = new ArrayList<>();

    @Override
    protected int getContentLayout() {
        return R.layout.activity_art_album_choose_inch;
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSpecificationRequest();
    }

    @Override
    protected void initData() {
        for (PhotoTypeExplain photoTypeExplain : MDGroundApplication.sInstance.getPhotoTypeExplainArrayList()) {
            if (photoTypeExplain.getExplainType() == PhotoExplainTypeEnum.Banner.value()
                    && photoTypeExplain.getTypeID() == ProductType.ArtAlbum.value()) {
                GlideUtil.loadImageByPhotoSIDWithDialog(mDataBinding.ivBanner,
                        photoTypeExplain.getPhotoSID());
                break;
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataBinding.recyclerView.setLayoutManager(layoutManager);
        mDataBinding.recyclerView.addItemDecoration(new DividerItemDecoration(12));

        mAdapter = new ArtAlbumChooseInchAdapter();
        mDataBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
    }

    //region ACTION
    public void toIllutrationActivityAction(View view) {
        Intent intent = new Intent(this, ArtAlbumForIllustrationActivity.class);
        startActivity(intent);
    }
    //endregion

    //region SERVER
    private void getSpecificationRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetPhotoType(ProductType.ArtAlbum, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                if (ResponseCode.isSuccess(response.body())) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().getContent());

                        String PhotoTypeDescList = jsonObject.getString("PhotoTypeDescList");

                        mSpecList = StringUtil.getInstanceByJsonString(PhotoTypeDescList, new TypeToken<ArrayList<Measurement>>() {
                        });

                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ViewUtils.dismiss();
            }
        });
    }
    //endregion

    //region ADAPTER
    public class ArtAlbumChooseInchAdapter extends RecyclerView.Adapter<ArtAlbumChooseInchAdapter.BindingHolder> {

        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_art_album_choose_inch, parent, false);
            BindingHolder holder = new BindingHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(BindingHolder holder, int position) {
            holder.viewDataBinding.setMeasurement(mSpecList.get(position));
            holder.viewDataBinding.setHandlers(holder);
        }

        @Override
        public int getItemCount() {
            return mSpecList.size();
        }

        public class BindingHolder extends RecyclerView.ViewHolder {

            public ItemArtAlbumChooseInchBinding viewDataBinding;

            public BindingHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }

            public void toSelectTemplateActivityAction(View view) {
                MDGroundApplication.sInstance.setChoosedMeasurement(mSpecList.get(getAdapterPosition()));

                Intent intent = new Intent(ArtAlbumChooseInchActivity.this, PhotoTypeActivity.class);
                startActivity(intent);
            }
        }
    }
    //endregion
}
