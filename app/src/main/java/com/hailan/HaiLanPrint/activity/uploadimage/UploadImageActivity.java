package com.hailan.HaiLanPrint.activity.uploadimage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.CompoundButton;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.adapter.ChooseImageListAdapter;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityUploadImageBinding;
import com.hailan.HaiLanPrint.models.Album;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.restfuls.FileRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.LocalMediaLoader;
import com.hailan.HaiLanPrint.utils.PreferenceUtils;
import com.hailan.HaiLanPrint.utils.ToolNetwork;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.dialog.NotifyDialog;
import com.hailan.HaiLanPrint.views.itemdecoration.GridSpacingItemDecoration;
import com.socks.library.KLog;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/12/16.
 */
public class UploadImageActivity extends ToolbarActivity<ActivityUploadImageBinding> {

    private boolean mIsShared;

    private int mCountPerLine = 3; // 每行显示3个

    private ChooseImageListAdapter mImageAdapter;

    private NotifyDialog mNotifyDialog;

    private boolean mIsManualChangeState;

    private boolean mAlreadyUploadedImage;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_upload_image;
    }

    @Override
    protected void initData() {
        mDataBinding.recyclerView.setHasFixedSize(true);
        mDataBinding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(mCountPerLine, ViewUtils.dp2px(2), false));
        mDataBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, mCountPerLine));

        mImageAdapter = new ChooseImageListAdapter(this, Integer.MAX_VALUE, true, false);
        mDataBinding.recyclerView.setAdapter(mImageAdapter);

        new LocalMediaLoader(this, LocalMediaLoader.TYPE_IMAGE).loadAllImage(new LocalMediaLoader.LocalMediaLoadListener() {

            @Override
            public void loadComplete(List<Album> albums) {
                mImageAdapter.bindImages(albums.get(0).getImages());
            }
        });
    }

    @Override
    protected void setListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlreadyUploadedImage) {
                    setResult(RESULT_OK);
                }
                finish();
            }
        });

        mDataBinding.cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!mIsManualChangeState) {
                    mImageAdapter.selectAllImage(isChecked);
                }
            }
        });

        mImageAdapter.setOnImageSelectChangedListener(new ChooseImageListAdapter.OnImageSelectChangedListener() {

            @Override
            public void onSelectImage(MDImage selectImage, int selectNum) {
                changeTextNum(selectNum);
            }

            @Override
            public void onUnSelectImage(MDImage unselectImage, int selectNum) {
                changeTextNum(selectNum);
            }

            @Override
            public void onTakePhoto() {

            }

            @Override
            public void onPictureClick(MDImage media, int position) {

            }

            @Override
            public void onIsSelectAllImage(boolean isSelectAll) {
                mIsManualChangeState = true;
                mDataBinding.cbSelectAll.setChecked(isSelectAll);
                mIsManualChangeState = false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mAlreadyUploadedImage) {
            setResult(RESULT_OK);
        }
        finish();
    }

    private void changeTextNum(int selectNum) {
        if (selectNum != 0) {
            mDataBinding.btnUpload.setText(getString(R.string.upload_num, selectNum));
        } else {
            mDataBinding.btnUpload.setText(getString(R.string.upload));
        }
    }

    //region ACTION
    public void uploadAction(View view) {
        List<MDImage> selectImages = mImageAdapter.getSelectedImages();

        if (selectImages == null || selectImages.size() == 0) {
            ViewUtils.toast(R.string.choose_photo);
            return;
        }

        if (ToolNetwork.getInstance().isConnected()) {
            if (ToolNetwork.isWIFIConnected(this)) {
                uploadImage();
            } else {
                boolean isOnlyWifiUpdate = PreferenceUtils.getPrefBoolean(Constants.KEY_ONLY_WIFI_UPLOAD, false);

                if (isOnlyWifiUpdate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadImageActivity.this);
                    builder.setTitle(R.string.tips);
                    builder.setMessage(R.string.current_cellular_network);
                    builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                } else {
                    uploadImage();
                }
//                showUseCelluarNetworkTips();
            }
        } else {
            ViewUtils.toast(R.string.network_unavailable);
        }
    }
    //endregion

    private void showUseCelluarNetworkTips() {
        if (mNotifyDialog == null) {
            mNotifyDialog = new NotifyDialog(this);
            mNotifyDialog.setOnSureClickListener(new NotifyDialog.OnSureClickListener() {
                @Override
                public void onSureClick() {
                    mNotifyDialog.dismiss();
                    uploadImage();
                }
            });
        }
        mNotifyDialog.show();
    }

    private void uploadImage() {
        ViewUtils.loading(this);
        mDataBinding.btnUpload.setEnabled(false);
        uploadImageRequest(0);
    }

    private void uploadImageRequest(final int upload_image_index) {
        if (upload_image_index < mImageAdapter.getSelectedImages().size()) {
            int nextUploadIndex = upload_image_index + 1;
            ViewUtils.setLoadingMessage("正在上传原图" + nextUploadIndex + "/" + mImageAdapter.getSelectedImages().size());

            MDImage localMedia = mImageAdapter.getSelectedImages().get(upload_image_index);
            File file = new File(localMedia.getImageLocalPath());
            KLog.e("localMedia.getImageLocalPath() : " + localMedia.getImageLocalPath());

            FileRestful.getInstance().UploadCloudPhoto(mIsShared, file, null, new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                    int nextUploadIndex = upload_image_index + 1;
                    uploadImageRequest(nextUploadIndex);
                }

                @Override
                public void onFailure(Call<ResponseData> call, Throwable t) {
                }
            });
        } else {
            ViewUtils.toast("上传图片成功");
            ViewUtils.dismiss();
            mAlreadyUploadedImage = true;
            changeTextNum(0);
            mImageAdapter.getSelectedImages().clear();
            mImageAdapter.selectAllImage(false);
            mDataBinding.btnUpload.setEnabled(true);
        }
    }

}
