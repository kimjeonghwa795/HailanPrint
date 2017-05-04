package com.hailan.HaiLanPrint.activity.phoneshell;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.activity.phototype.PhotoTypeActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ActivityPhoneShellStartBinding;
import com.hailan.HaiLanPrint.enumobject.PhotoExplainTypeEnum;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.Measurement;
import com.hailan.HaiLanPrint.models.PhotoTypeExplain;
import com.hailan.HaiLanPrint.models.Template;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.SelectImageUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/11/16.
 */
public class PhoneShellStartActivity extends ToolbarActivity<ActivityPhoneShellStartBinding> {

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
            Template template = MDGroundApplication.sInstance.getChoosedTemplate();
            template.setPrice(propertyListBean.getPrice());
            template.setMaterialTypeString(propertyListBean.getMaterialName());
            template.setSelectMaterial(propertyListBean.getMaterialName());
            MDGroundApplication.sInstance.setChoosedTemplate(template);
            mDataBinding.tvPrice.setText(StringUtil.toYuanWithUnit(MDGroundApplication.sInstance.getChoosedTemplate().getPrice()));
        }
    };

    @Override
    protected int getContentLayout() {
        return R.layout.activity_phone_shell_start;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        for (PhotoTypeExplain photoTypeExplain : MDGroundApplication.sInstance.getPhotoTypeExplainArrayList()) {
            if (photoTypeExplain.getExplainType() == PhotoExplainTypeEnum.Banner.value()
                    && photoTypeExplain.getTypeID() == ProductType.PhoneShell.value()) {
                GlideUtil.loadImageByPhotoSIDWithDialog(mDataBinding.ivBanner, photoTypeExplain.getPhotoSID());
                break;
            }
        }

//        getSpecificationRequest();
    }

    @Override
    protected void setListener() {
    }

    private void changeModelTextAndMaterialAvailable() {
        mDataBinding.tvPhoneModel.setText(MDGroundApplication.sInstance.getChoosedMeasurement().getTitle() + "-" + MDGroundApplication.sInstance.getChoosedTemplate().getTemplateName());
//        mDataBinding.tvPrice.setText(StringUtil.toYuanWithUnit(MDGroundApplication.sInstance.getChoosedTemplate().getPrice()));

        GetPhotoTemplate(MDGroundApplication.sInstance.getChoosedTemplate().getTemplateID());


//        mDataBinding.rgMaterial.clearCheck();
//
//        if ((MDGroundApplication.sInstance.getChoosedTemplate().getMaterialType() & MaterialType.Silicone.value()) != 0) {
//            mDataBinding.rbSilicone.setEnabled(true);
//            if (mDataBinding.rgMaterial.getCheckedRadioButtonId() == -1) {
//                mDataBinding.rbSilicone.setChecked(true);
//
//                Template template = MDGroundApplication.sInstance.getChoosedTemplate();
//                template.setSelectMaterial(ProductMaterial.PhoneShell_Silicone.getText());
//                MDGroundApplication.sInstance.setChoosedTemplate(template);
//            }
//        } else {
//            mDataBinding.rbSilicone.setEnabled(false);
//        }
//
//        if ((MDGroundApplication.sInstance.getChoosedTemplate().getMaterialType() & MaterialType.Plastic.value()) != 0) {
//            mDataBinding.rbPlastic.setEnabled(true);
//            if (mDataBinding.rgMaterial.getCheckedRadioButtonId() == -1) {
//                mDataBinding.rbPlastic.setChecked(true);
//
//                Template template = MDGroundApplication.sInstance.getChoosedTemplate();
//                template.setSelectMaterial(ProductMaterial.PhoneShell_Plastic.getText());
//                MDGroundApplication.sInstance.setChoosedTemplate(template);
//            }
//        } else {
//            mDataBinding.rbPlastic.setEnabled(false);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (MDGroundApplication.sInstance.getChoosedTemplate() != null) {
                changeModelTextAndMaterialAvailable();
            }
        }
    }

    //region ACTION
    public void toPhoneShellIllutrationActivityAction(View view) {
        Intent intent = new Intent(this, PhoneShellForIllustrationActivity.class);
        startActivity(intent);
    }
    //endregion

    //region ACTION
    public void toSelectBrandActivityAction(View view) {
        Intent intent = new Intent(this, PhoneShellSelectBrandActivity.class);
        startActivityForResult(intent, 0);
    }

    public void nextStepAction(View view) {
        if (MDGroundApplication.sInstance.getChoosedTemplate() == null) {
            ViewUtils.toast(R.string.please_select_phone_model);
            return;
        }
        if (checkButton == null) {
            ViewUtils.toast(R.string.please_select_phone_material);
            return;
        }
        getPhotoTemplateAttachListRequest(MDGroundApplication.sInstance.getChoosedTemplate().getTemplateID());
    }
    //endregion

    //region SERVER
    private void getSpecificationRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetPhotoType(ProductType.PhoneShell, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                if (ResponseCode.isSuccess(response.body())) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().getContent());

                        String PhotoTypeDescList = jsonObject.getString("PhotoTypeDescList");

                        ArrayList<Measurement> specList = StringUtil.getInstanceByJsonString(PhotoTypeDescList, new TypeToken<ArrayList<Measurement>>() {
                        });

                        if (specList.size() > 0) {
                            MDGroundApplication.sInstance.setChoosedMeasurement(specList.get(0));
                            getPhotoTemplateListRequest();
                        }

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

    private void getPhotoTemplateListRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetPhotoTemplateList(MDGroundApplication.sInstance.getChoosedMeasurement().getTypeDescID(),
                new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        ViewUtils.dismiss();
                        if (ResponseCode.isSuccess(response.body())) {
                            ArrayList<Template> templateList = response.body().getContent(new TypeToken<ArrayList<Template>>() {
                            });

                            if (templateList.size() > 0) {
                                Template template = templateList.get(0);

                                MDGroundApplication.sInstance.setChoosedTemplate(template);

                                changeModelTextAndMaterialAvailable();
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
     * 根据模板id获取材质信息
     *
     * @param TemplateID
     */
    private void GetPhotoTemplate(final int TemplateID) {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetPhotoTemplate(TemplateID, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                Template template = response.body().getContent(Template.class);

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
            Template template = MDGroundApplication.sInstance.getChoosedTemplate();
            template.setPrice(propertyListBean.getPrice());
            template.setMaterialTypeString(propertyListBean.getMaterialName());
            template.setSelectMaterial(propertyListBean.getMaterialName());
            MDGroundApplication.sInstance.setChoosedTemplate(template);
            mDataBinding.tvPrice.setText(StringUtil.toYuanWithUnit(MDGroundApplication.sInstance.getChoosedTemplate().getPrice()));
        }
        mDataBinding.rgMaterial.addView(view);
    }

    /**
     * 获取模板
     *
     * @param templateID
     */
    private void getPhotoTemplateAttachListRequest(final int templateID) {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetPhotoTemplateAttachList(templateID, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                SelectImageUtils.sTemplateImage.clear();
                SelectImageUtils.sTemplateImage = response.body().getContent(new TypeToken<ArrayList<MDImage>>() {
                });

                ViewUtils.dismiss();

                Intent intent = new Intent(PhoneShellStartActivity.this, PhotoTypeActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }
    //endregion

}
