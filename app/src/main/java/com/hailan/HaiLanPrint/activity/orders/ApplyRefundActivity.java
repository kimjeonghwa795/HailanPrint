package com.hailan.HaiLanPrint.activity.orders;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityApplyRefundBinding;
import com.hailan.HaiLanPrint.databinding.ItemApplyRefundBinding;
import com.hailan.HaiLanPrint.enumobject.UploadType;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.OrderInfo;
import com.hailan.HaiLanPrint.restfuls.FileRestful;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.dialog.SelectSingleImageDialog;
import com.hailan.HaiLanPrint.views.itemdecoration.GridSpacingItemDecoration;
import com.socks.library.KLog;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelector;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/30/16.
 */

public class ApplyRefundActivity extends ToolbarActivity<ActivityApplyRefundBinding> {

    private ApplyRefundAdapter mAdapter;

    private SelectSingleImageDialog mSelectSingleImageDialog;

    private ArrayList<MDImage> mUploadImageArrayList = new ArrayList<>();

    private OrderInfo mOrderInfo;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_apply_refund;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.KEY_SELECT_IMAGE, mUploadImageArrayList);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constants.KEY_SELECT_IMAGE)) {
                mUploadImageArrayList = savedInstanceState.getParcelableArrayList(Constants.KEY_SELECT_IMAGE);
            }
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void initData() {
        mOrderInfo = getIntent().getParcelableExtra(Constants.KEY_ORDER_INFO);

        mDataBinding.cetRefundFee.setHint(getString(R.string.at_most_yuan, StringUtil.toYuanWithoutUnit(mOrderInfo.getTotalFeeReal())));
        mDataBinding.cetRefundFee.setmMaxInputFee(mOrderInfo.getTotalFeeReal() / 100f);

        mSelectSingleImageDialog = new SelectSingleImageDialog(this);

        mDataBinding.recyclerView.setHasFixedSize(true);
        mDataBinding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, ViewUtils.dp2px(2), false));
        mDataBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        mAdapter = new ApplyRefundAdapter();
        mDataBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectSingleImageDialog.PHOTO_REQUEST_GALLERY) {// 从相册返回的数据
//                Uri uri = data.getData();
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                Cursor cursor = managedQuery(uri, filePathColumn, null, null, null);
//                int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
//                cursor.moveToFirst();
//
//                String picturePath = cursor.getString(columnIndex);
//                KLog.e("picturePath : " + picturePath);

                ArrayList<String> list = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);

                MDImage mdImage = new MDImage();
                mdImage.setImageLocalPath(list.get(0));

                mUploadImageArrayList.add(mdImage);
                mAdapter.notifyDataSetChanged();

            } else if (requestCode == SelectSingleImageDialog.PHOTO_REQUEST_CAREMA) {// 从相机返回的数据
                KLog.e("相机返回数据");

//                Uri uri = data.getData();
//                String picturePath = FileUtils.getAbsoluteImagePath(ApplyRefundActivity.this, uri);
                String picturePath = SelectSingleImageDialog.sCaptureImageURL;
                KLog.e("picturePath : " + picturePath);

                MDImage mdImage = new MDImage();
                mdImage.setImageLocalPath(picturePath);

                mUploadImageArrayList.add(mdImage);

                mAdapter.notifyDataSetChanged();
            }
        }
    }

    //region ACTION
    public void sumitAction(View view) {
        if (StringUtil.isEmpty(mDataBinding.cetRefundFee.getText().toString())) {
            ViewUtils.toast(R.string.input_refund_amount);
            return;
        }
        UploadPhotoRequest();
    }
    //endregion

    //region SERVER
    private void UploadPhotoRequest() {
        ViewUtils.loading(this);
        FileRestful.getInstance().UploadPhoto(UploadType.Order, mUploadImageArrayList, new FileRestful.OnUploadSuccessListener() {
            @Override
            public void onUploadSuccess(ArrayList<MDImage> mdImageArrayList) {
                for (int i = 0; i < mdImageArrayList.size(); i++) {
                    MDImage mdImage = mdImageArrayList.get(i);
                    int photoID = mdImage.getPhotoID();
                    int photoSID = mdImage.getPhotoSID();
                    switch (i) {
                        case 0:
                            mOrderInfo.setRefundPhoto1ID(photoID);
                            mOrderInfo.setRefundPhoto1SID(photoSID);
                            break;
                        case 1:
                            mOrderInfo.setRefundPhoto2ID(photoID);
                            mOrderInfo.setRefundPhoto2SID(photoSID);
                            break;
                        case 2:
                            mOrderInfo.setRefundPhoto3ID(photoID);
                            mOrderInfo.setRefundPhoto3SID(photoSID);
                            break;
                    }
                }

                mOrderInfo.setRefundFee(mDataBinding.cetRefundFee.getInputTextToInteger());
                mOrderInfo.setRefundReason(mDataBinding.etRefundReason.getText().toString());

                GlobalRestful.getInstance().UpdateOrderRefunding(mOrderInfo, new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        ViewUtils.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {

                    }
                });
            }
        });
    }
    //endregion

    //region ADAPTER
    public class ApplyRefundAdapter extends RecyclerView.Adapter<ApplyRefundAdapter.ViewHolder> {

        @Override
        public ApplyRefundAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_apply_refund, parent, false);
            ApplyRefundAdapter.ViewHolder viewHolder = new ViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ApplyRefundAdapter.ViewHolder holder, int position) {
            if (mUploadImageArrayList.size() == 0 || (position >= mUploadImageArrayList.size() && mUploadImageArrayList.size() != 3)) {
                holder.viewDataBinding.ivImage.setImageResource(R.drawable.layerlist_camara_placeholder);
            } else {
                holder.viewDataBinding.setImage(mUploadImageArrayList.get(position));
                GlideUtil.loadImageByMDImage(holder.viewDataBinding.ivImage, mUploadImageArrayList.get(position), true);
            }
            holder.viewDataBinding.setHandlers(holder);
        }

        @Override
        public int getItemCount() {
            if (mUploadImageArrayList.size() < 3) {
                return mUploadImageArrayList.size() + 1;
            }
            return 3;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemApplyRefundBinding viewDataBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }

            public void onImageLayoutClickAction(View view) {
                int position = getAdapterPosition();
                if (mUploadImageArrayList.size() != 3 && position == getItemCount() - 1) {
                    mSelectSingleImageDialog.show();
                }
            }
        }
    }
    //endregion
}
