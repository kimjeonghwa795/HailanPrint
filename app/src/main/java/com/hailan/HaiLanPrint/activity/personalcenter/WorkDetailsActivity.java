package com.hailan.HaiLanPrint.activity.personalcenter;

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
import com.hailan.HaiLanPrint.models.OrderInfo;
import com.hailan.HaiLanPrint.models.OrderWork;
import com.hailan.HaiLanPrint.models.WorkInfo;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.DateUtils;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.OrderUtils;
import com.hailan.HaiLanPrint.utils.ShareUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.dialog.ShareDialog;
import com.socks.library.KLog;

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
        KLog.e("mWorkInfoID" + mWorkInfo.getWorkID());
        GlideUtil.loadImageByPhotoSID(mDataBinding.ivImage, mWorkInfo.getPhotoCover(), true);
        mDataBinding.tvWorksname.setText(String.valueOf(mWorkInfo.getTypeName()));
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
        mDataBinding.tvPage.setText(getString(R.string.page_num_with_colon) + " " + mWorkInfo.getPhotoCount() + "页");
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

    //region ACTION
    //编辑作品
    public void onEditorWorks(View view) {

    }

    //购买作品
    public void buyWorksAction(View view) {
        List<Integer> workIDList = new ArrayList<>();
        workIDList.add(mWorkInfo.getWorkID());

        saveOrderByWorkRequest(workIDList);
    }
    //endregion
}
