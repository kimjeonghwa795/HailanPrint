package com.hailan.HaiLanPrint.activity.phoneshell;

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
import com.hailan.HaiLanPrint.activity.photoprint.PrintPhotoMeasurementDescriptionActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ActivityPhoneShellSelectModelBinding;
import com.hailan.HaiLanPrint.databinding.ItemPhoneSheelSelectModelBinding;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.Template;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.itemdecoration.DividerItemDecoration;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/11/16.
 */
public class PhoneShellSelectModelActivity extends ToolbarActivity<ActivityPhoneShellSelectModelBinding> {

    private PhoneShellSelectModelAdapter mAdapter;

    private ArrayList<Template> mTemplate = new ArrayList<>();

    @Override
    protected int getContentLayout() {
        return R.layout.activity_phone_shell_select_model;
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPhotoTemplateListRequest();
    }

    @Override
    protected void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataBinding.recyclerView.setLayoutManager(layoutManager);
        mDataBinding.recyclerView.addItemDecoration(new DividerItemDecoration(12));

        mAdapter = new PhoneShellSelectModelAdapter();
        mDataBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneShellSelectModelActivity.this, PrintPhotoMeasurementDescriptionActivity.class);
                startActivity(intent);
            }
        });
    }

    //region SERVER
    private void getPhotoTemplateListRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetPhotoTemplateList(MDGroundApplication.sInstance.getChoosedMeasurement().getTypeDescID(),
                new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        ViewUtils.dismiss();
                        if (ResponseCode.isSuccess(response.body())) {
                            mTemplate = response.body().getContent(new TypeToken<ArrayList<Template>>() {
                            });

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

    //region ADAPTER
    public class PhoneShellSelectModelAdapter extends RecyclerView.Adapter<PhoneShellSelectModelAdapter.BindingHolder> {

        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_phone_sheel_select_model, parent, false);
            BindingHolder holder = new BindingHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(BindingHolder holder, int position) {
            holder.viewDataBinding.setTemplate(mTemplate.get(position));
            holder.viewDataBinding.setHandlers(holder);
        }

        @Override
        public int getItemCount() {
            return mTemplate.size();
        }

        public class BindingHolder extends RecyclerView.ViewHolder {

            public ItemPhoneSheelSelectModelBinding viewDataBinding;

            public BindingHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }

            public void toPhoneShellSelectModelActivityAction(View view) {
                MDGroundApplication.sInstance.setChoosedTemplate(mTemplate.get(getAdapterPosition()));

                setResult(RESULT_OK);
                finish();
            }
        }
    }
    //endregion
}
