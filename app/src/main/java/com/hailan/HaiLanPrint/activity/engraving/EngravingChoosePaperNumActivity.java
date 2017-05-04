package com.hailan.HaiLanPrint.activity.engraving;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ActivityEngravingChoosePaperNumBinding;
import com.hailan.HaiLanPrint.databinding.ItemEngravingChoosePagerNumBinding;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.Size;
import com.hailan.HaiLanPrint.models.Template;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.OrderUtils;
import com.hailan.HaiLanPrint.utils.SelectImageUtils;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.socks.library.KLog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/11/16.
 */
public class EngravingChoosePaperNumActivity extends ToolbarActivity<ActivityEngravingChoosePaperNumBinding> {

    private EngravingChoosePaperNumAdapter mAdapter;

    private Template template;

    private RadioButton checkButton = null;

    private View.OnClickListener onRadioButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkButton == v) {
                return;
            }
            if (checkButton != null) {
                checkButton.setChecked(false);
            }
            ((RadioButton) v).setChecked(true);
            checkButton = (RadioButton) v;
            Template.PropertyListBean propertyListBean = (Template.PropertyListBean) v.getTag();
            for (Template.MaterialListBean materialListBean : template.getMaterialList()) {
                if (materialListBean.getMaterialID() == propertyListBean.getMaterialID()) {
                    mDataBinding.tvPaperQuality.setText(materialListBean.getMaterialDescX());
                    break;
                }
            }
        }
    };

    @Override
    protected int getContentLayout() {
        return R.layout.activity_engraving_choose_paper_num;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        mDataBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mAdapter = new EngravingChoosePaperNumAdapter();
        mDataBinding.recyclerView.setAdapter(mAdapter);

        GetPhotoTemplateByTypeDesc(MDGroundApplication.sInstance.getChoosedMeasurement().getTypeDescID());
    }

    @Override
    protected void setListener() {
//        mDataBinding.rgMaterial.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.rbCrystal:
//                        mDataBinding.tvPaperQuality.setText(R.string.crystal_paper_quality);
//                        break;
//                    case R.id.rbYogon:
//                        mDataBinding.tvPaperQuality.setText(R.string.yogon_paper_quality);
//                        break;
//                }
//            }
//        });
    }

    /**
     * 根据所选的尺寸Id获取对应模板信息
     *
     * @param TypeDescID
     */
    private void GetPhotoTemplateByTypeDesc(final int TypeDescID) {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetPhotoTemplateByTypeDesc(TypeDescID, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                template = response.body().getContent(Template.class);

                checkButton = null;
                mDataBinding.rgMaterial.removeAllViews();

                if (template != null && template.getPropertyList().size() > 0) {
                    for (Template.PropertyListBean propertyListBean : template.getPropertyList()) {
                        addButtonView(propertyListBean);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ViewUtils.dismiss();
            }
        });
    }

    /**
     * 添加材质控件
     */
    private void addButtonView(Template.PropertyListBean propertyListBean) {
        View view = LayoutInflater.from(this).inflate(R.layout.widget_prepay_radio_button, null);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.rb_button);
        radioButton.setText(propertyListBean.getMaterialName());
        radioButton.setTag(propertyListBean);
        radioButton.setOnClickListener(onRadioButtonClick);
        if (mDataBinding.rgMaterial.getChildCount() == 0) {
            radioButton.setChecked(true);
            checkButton = radioButton;
            for (Template.MaterialListBean materialListBean : template.getMaterialList()) {
                if (materialListBean.getMaterialID() == propertyListBean.getMaterialID()) {
                    mDataBinding.tvPaperQuality.setText(materialListBean.getMaterialDescX());
                    break;
                }
            }
        }
        mDataBinding.rgMaterial.addView(view);
    }

    //region ACTION
    public void nextStepAction(View view) {
        if (checkButton == null) {
            ViewUtils.toast(R.string.choose_material_first);
            return;
        }

        ViewUtils.loading(this);
        Template.PropertyListBean propertyListBean = (Template.PropertyListBean) checkButton.getTag();
        String workMaterial = propertyListBean.getMaterialName();
//        if (mDataBinding.rbCrystal.isChecked()) {
//            workMaterial = ProductMaterial.Engraving_Crystal.getText();
//        } else {
//            workMaterial = ProductMaterial.Engraving_Yogon.getText();
//        }

        MDGroundApplication.sOrderutUtils = new OrderUtils(this, false, 1, propertyListBean.getPrice(), null, workMaterial, null);
        MDGroundApplication.sOrderutUtils.uploadPrintPhotoOrEngravingImageRequest(this, 0);
    }
    //endregion

    //region ADAPTER
    public class EngravingChoosePaperNumAdapter extends RecyclerView.Adapter<EngravingChoosePaperNumAdapter.BindingHolder> {

        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_engraving_choose_pager_num, parent, false);
            BindingHolder holder = new BindingHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(final BindingHolder holder, final int position) {
            holder.viewDataBinding.setImage(SelectImageUtils.sAlreadySelectImage.get(position));
            holder.viewDataBinding.setHandlers(holder);
            holder.viewDataBinding.ivImage.setVisibility(View.GONE);
            holder.viewDataBinding.ivImageTwo.setVisibility(View.GONE);
            GlideUtil.getImageOriginalSize2(MDGroundApplication.sInstance, SelectImageUtils.sAlreadySelectImage.get(position),
                    new SimpleTarget<Size>() {
                        @Override
                        public void onResourceReady(Size resource, GlideAnimation<? super Size> glideAnimation) {
                            KLog.e("width :" + resource.width);
                            KLog.e("height :" + resource.height);
                            if (resource.width > resource.height) {
                                holder.viewDataBinding.ivImage.setVisibility(View.VISIBLE);
                                GlideUtil.loadImageByMDImage(holder.viewDataBinding.ivImage,
                                        SelectImageUtils.sAlreadySelectImage.get(position), true);

                            } else {
                                holder.viewDataBinding.ivImageTwo.setVisibility(View.VISIBLE);
                                GlideUtil.loadImageByMDImage(holder.viewDataBinding.ivImageTwo,
                                        SelectImageUtils.sAlreadySelectImage.get(position), true);
                            }
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return SelectImageUtils.sAlreadySelectImage.size();
        }

        public class BindingHolder extends RecyclerView.ViewHolder {

            public ItemEngravingChoosePagerNumBinding viewDataBinding;

            public BindingHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }

            public void addPrintNumAction(View view) {
                int position = getAdapterPosition();

                MDImage mdImage = SelectImageUtils.sAlreadySelectImage.get(position);
                int photoCount = mdImage.getPhotoCount();

                if (photoCount == 1) {
                    viewDataBinding.ivMinus.setImageResource(R.drawable.btn_optionbox_reduce_sel);
                }
                viewDataBinding.ivMinus.setEnabled(true);

                mdImage.setPhotoCount(++photoCount);
            }

            public void minusPrintNumAction(View view) {
                int position = getAdapterPosition();

                MDImage mdImage = SelectImageUtils.sAlreadySelectImage.get(position);
                int photoCount = mdImage.getPhotoCount();

                if (photoCount == 1) {
                    return;
                }

                if (photoCount == 2) {
                    view.setEnabled(false);
                    viewDataBinding.ivMinus.setImageResource(R.drawable.btn_optionbox_reduce_nor);
                }

                mdImage.setPhotoCount(--photoCount);

            }
        }
    }
    //endregion
}
