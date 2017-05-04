package com.hailan.HaiLanPrint.activity.myworks;

import android.content.Intent;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityWorksDetailsBinding;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.OrderInfo;
import com.hailan.HaiLanPrint.models.OrderWork;
import com.hailan.HaiLanPrint.models.Template;
import com.hailan.HaiLanPrint.models.WorkInfo;
import com.hailan.HaiLanPrint.models.WorkPhoto;
import com.hailan.HaiLanPrint.models.WorkPhotoEdit;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.DateUtils;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.OrderUtils;
import com.hailan.HaiLanPrint.utils.SelectImageUtils;
import com.hailan.HaiLanPrint.utils.ShareUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.TemplateUtils;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.dialog.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by PC on 2016-06-19.
 */

public class WorkDetailsActivity extends ToolbarActivity<ActivityWorksDetailsBinding> {

    private WorkInfo mWorkInfo;
    private ShareDialog mShareDialog;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_works_details;
    }

    @Override
    protected void initData() {
        Intent intent = this.getIntent();
        mWorkInfo = (WorkInfo) intent.getSerializableExtra(Constants.KEY_WORKS_DETAILS);

        ProductType productType = ProductType.fromValue(mWorkInfo.getTypeID());
        MDGroundApplication.sInstance.setChoosedProductType(productType);

        getPhotoTemplateRequest();

        GlideUtil.loadImageByPhotoSID(mDataBinding.ivImage, mWorkInfo.getPhotoCover(), true);
        mDataBinding.tvWorksname.setText(StringUtil.getProductName(mWorkInfo));
        mDataBinding.tvWorksPice.setText(getString(R.string.rmb) + String.valueOf(StringUtil.toYuanWithoutUnit(mWorkInfo.getPrice())));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(mWorkInfo.getCreatedTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String recentlyDate = DateUtils.getDateStringBySpecificFormat(date, new SimpleDateFormat("yyyy-MM-dd"));
        mDataBinding.tvRecentlyEdited.setText(getString(R.string.recently_edit) + " " + recentlyDate);
        mDataBinding.tvPage.setText(getString(R.string.page_num, mWorkInfo.getPhotoCount()));
        mDataBinding.tvTemplate.setText(getString(R.string.template_name_) + " " + mWorkInfo.getTypeName());
    }

    @Override
    protected void setListener() {
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setTextSize(0);
        tvRight.setBackgroundResource(R.drawable.icon_share_mywork);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int workId = mWorkInfo.getWorkID();
                int userId = mWorkInfo.getUserID();
                String shareUrl = ShareUtils.createShareURL(String.valueOf(workId), String.valueOf(userId));
                if (mShareDialog == null) {
                    mShareDialog = new ShareDialog(WorkDetailsActivity.this);
                }
                mShareDialog.initURLShareParams(shareUrl);
                mShareDialog.show();
            }
        });
    }

    //region SERVER
    private void getPhotoTemplateRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetPhotoTemplate(mWorkInfo.getTemplateID(), new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                Template template = response.body().getContent(Template.class);
                if (template == null) {
                    ViewUtils.dismiss();
                    return;
                }

                MDGroundApplication.sInstance.setChoosedTemplate(template);

                getPhotoTemplateAttachListRequest(template.getTemplateID());
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ViewUtils.dismiss();
            }
        });
    }

    private void getPhotoTemplateAttachListRequest(int templateID) {
        GlobalRestful.getInstance().GetPhotoTemplateAttachList(templateID, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                SelectImageUtils.sTemplateImage.clear();

                SelectImageUtils.sTemplateImage = response.body().getContent(new TypeToken<ArrayList<MDImage>>() {
                });

                getUserWorkRequest();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void getUserWorkRequest() {
        GlobalRestful.getInstance().GetUserWork(mWorkInfo.getWorkID(), new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();

                SelectImageUtils.sAlreadySelectImage.clear();
//                SelectImageUtils.sTemplateImage.clear();

                try {
                    JSONObject jsonObject = new JSONObject(response.body().getContent());

                    String workPhotoListString = jsonObject.getString("WorkPhotoList");

                    ArrayList<WorkPhoto> workPhotoArrayList = StringUtil.getInstanceByJsonString(workPhotoListString,
                            new TypeToken<ArrayList<WorkPhoto>>() {
                            });

                    for (WorkPhoto workPhoto : workPhotoArrayList) {
                        // 之前选中的模版图片
//                        {
//                            MDImage templateMdImage = new MDImage();
//
//                            templateMdImage.setPhotoID(workPhoto.getTemplatePID());
//                            templateMdImage.setPhotoSID(workPhoto.getTemplatePSID());
//
//                            SelectImageUtils.sTemplateImage.add(templateMdImage);
//                        }

                        // 之前选中的图片
                        {
                            if (TemplateUtils.isTemplateHasModules()) {
                                List<WorkPhotoEdit> workPhotoEditList = workPhoto.getWorkPhotoEditList();

                                for (WorkPhotoEdit workPhotoEdit : workPhotoEditList) {
                                    MDImage selectMdImage = new MDImage();

                                    selectMdImage.setPhotoID(workPhotoEdit.getPhotoID());
                                    selectMdImage.setPhotoSID(workPhotoEdit.getPhotoSID());

                                    SelectImageUtils.sAlreadySelectImage.add(selectMdImage);
                                }
                            } else {
                                MDImage selectMdImage = new MDImage();

                                selectMdImage.setPhotoID(workPhoto.getPhoto1ID());
                                selectMdImage.setPhotoSID(workPhoto.getPhoto1SID());
                                selectMdImage.setWorkPhoto(workPhoto);

                                SelectImageUtils.sAlreadySelectImage.add(selectMdImage);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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

                        NavUtils.toPaymentPreviewActivity(WorkDetailsActivity.this);
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

    //region ACTION
    //编辑作品
    public void toEditActivityAction(View view) {
        NavUtils.toPhotoEditActivity(this);
    }

    //购买作品
    public void buyWorksAction(View view) {
        List<Integer> workIDList = new ArrayList<>();
        workIDList.add(mWorkInfo.getWorkID());

        saveOrderByWorkRequest(workIDList);
    }
    //endregion
}
