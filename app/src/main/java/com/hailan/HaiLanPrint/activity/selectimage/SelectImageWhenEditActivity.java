package com.hailan.HaiLanPrint.activity.selectimage;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.adapter.ChooseImageListAdapter;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivitySelectImageWhenEditBinding;
import com.hailan.HaiLanPrint.models.Album;
import com.hailan.HaiLanPrint.models.CloudPhotoCategory;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.itemdecoration.GridSpacingItemDecoration;
import com.malinskiy.superrecyclerview.OnMoreListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/11/16.
 */
public class SelectImageWhenEditActivity extends ToolbarActivity<ActivitySelectImageWhenEditBinding> {

    private ChooseImageListAdapter mImageAdapter;

    private int mCountPerLine = 3; // 每行显示3个

    private Album mAlbum;

    private List<MDImage> mImagesList;

    private ArrayList<CloudPhotoCategory> mCloudPhotoCategoryArrayList = new ArrayList<>();

    private boolean mIsShared;

    private int mPageIndex;

    private int mCategoryId;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_select_image_when_edit;
    }

    @Override
    protected void initData() {
        mAlbum = getIntent().getParcelableExtra(Constants.KEY_ALBUM);

        mImagesList = mAlbum.getImages();

        mDataBinding.tabLayout.setVisibility(View.GONE);
        if (mImagesList.get(0).getPhotoSID() != 0) {
            mIsShared = mImagesList.get(0).isShared();
            mImagesList.clear();

            mDataBinding.imageRecyclerView.setupMoreListener(new OnMoreListener() {
                @Override
                public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                    getCloudPhotoRequest();
                }
            }, Constants.ITEM_LEFT_TO_LOAD_MORE);

            if (mIsShared) {
                mDataBinding.tabLayout.setVisibility(View.VISIBLE);
                getCloudPhotoCategoryListRequest();

            } else {
                getCloudPhotoRequest();
            }

        } else {
            mDataBinding.imageRecyclerView.setLoadingMore(false);
        }

//        if (mImagesList.get(0).getImageLocalPath() == null
//                && mImagesList.get(0).getPhotoID() == 0
//                && mImagesList.get(0).getPhotoCount() == 0) {
//            mImagesList.clear();
//        }

        // 相册
//        mDataBinding.imageRecyclerView.setHasFixedSize(true);
        mDataBinding.imageRecyclerView.addItemDecoration(new GridSpacingItemDecoration(mCountPerLine, ViewUtils.dp2px(2), false));
        mDataBinding.imageRecyclerView.setLayoutManager(new GridLayoutManager(this, mCountPerLine));

        mImageAdapter = new ChooseImageListAdapter(this, 1, true, false);
        mImageAdapter.bindImages(mImagesList);
        mDataBinding.imageRecyclerView.setAdapter(mImageAdapter);

    }

    @Override
    protected void setListener() {
        mImageAdapter.setOnImageSelectChangedListener(new ChooseImageListAdapter.OnImageSelectChangedListener() {

            @Override
            public void onSelectImage(MDImage selectImage, int selectNum) {
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_SELECT_IMAGE, selectImage);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onUnSelectImage(MDImage unselectImage, int selectNum) {
            }

            @Override
            public void onTakePhoto() {

            }

            @Override
            public void onPictureClick(MDImage media, int position) {

            }

            @Override
            public void onIsSelectAllImage(boolean isSelectAll) {
            }
        });
    }

    //region SERVER
    private void getCloudPhotoCategoryListRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetCloudPhotoCategoryList(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                mCloudPhotoCategoryArrayList = response.body().getContent(new TypeToken<ArrayList<CloudPhotoCategory>>() {
                });

                for (CloudPhotoCategory cloudPhotoCategory : mCloudPhotoCategoryArrayList) {
                    mDataBinding.tabLayout.addTab(mDataBinding.tabLayout.newTab().setText(cloudPhotoCategory.getCategoryName()));
                }
                mCategoryId = mCloudPhotoCategoryArrayList.get(0).getCategoryID();

                mDataBinding.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        int currentSelectedTabIndex = tab.getPosition();

                        CloudPhotoCategory cloudPhotoCategory = mCloudPhotoCategoryArrayList.get(currentSelectedTabIndex);

                        mImagesList.clear();
                        mPageIndex = 0;
                        mCategoryId = cloudPhotoCategory.getCategoryID();

                        getCloudPhotoRequest();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

                getCloudPhotoRequest();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ViewUtils.dismiss();
            }
        });
    }

    //region SERVER
    private void getCloudPhotoRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetCloudPhoto(mPageIndex, mIsShared, mCategoryId, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                mPageIndex++;

                if (StringUtil.isEmpty(response.body().getContent())) {
                    mDataBinding.imageRecyclerView.setLoadingMore(false);
                    mDataBinding.imageRecyclerView.setupMoreListener(null, 0);
                } else {
                    ArrayList<MDImage> tempImagesList = response.body().getContent(new TypeToken<ArrayList<MDImage>>() {
                    });

                    mImagesList.addAll(tempImagesList);
                    mImageAdapter.bindImages(mImagesList);
                }

                mDataBinding.imageRecyclerView.hideMoreProgress();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ViewUtils.dismiss();
            }
        });
    }
    //endregion

    //region ACTION
    public void nextStepAction(View view) {
        NavUtils.toPhotoEditActivity(view.getContext());
    }
    //endregion
}
