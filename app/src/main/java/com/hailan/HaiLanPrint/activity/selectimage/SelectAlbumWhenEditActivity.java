package com.hailan.HaiLanPrint.activity.selectimage;

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
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivitySelectAlbumWhenEditBinding;
import com.hailan.HaiLanPrint.databinding.ItemSelectAlbumWhenEditBinding;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.Album;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.LocalMediaLoader;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.itemdecoration.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/11/16.
 */
public class SelectAlbumWhenEditActivity extends ToolbarActivity<ActivitySelectAlbumWhenEditBinding> {

    private AlbumAdapter mAlbumAdapter;

    private List<Album> mAlbumsList = new ArrayList<>();

    @Override
    protected int getContentLayout() {
        return R.layout.activity_select_album_when_edit;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        // 相册
        LinearLayoutManager albumLayoutManager = new LinearLayoutManager(this);
        albumLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataBinding.albumRecyclerView.setLayoutManager(albumLayoutManager);
        mDataBinding.albumRecyclerView.addItemDecoration(new DividerItemDecoration(0));
        mAlbumAdapter = new AlbumAdapter();
        mDataBinding.albumRecyclerView.setAdapter(mAlbumAdapter);

        new LocalMediaLoader(SelectAlbumWhenEditActivity.this, LocalMediaLoader.TYPE_IMAGE).loadAllImage(new LocalMediaLoader.LocalMediaLoadListener() {

            @Override
            public void loadComplete(List<Album> albums) {
                mAlbumsList.addAll(albums);

                getPhotoCountRequest();
            }
        });
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void getPhotoCountRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetCloudPhotoCount(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                if (ResponseCode.isSuccess(response.body())) {

                    ArrayList<MDImage> tempImagesList = response.body().getContent(new TypeToken<ArrayList<MDImage>>() {
                    });

                    // 个人相册
                    Album personalAlbum = new Album();
                    personalAlbum.setName(getString(R.string.personal_album));
                    mAlbumsList.add(personalAlbum);

                    // 共享相册
                    Album shareAlbum = new Album();
                    shareAlbum.setName(getString(R.string.share_album));
                    mAlbumsList.add(shareAlbum);

                    for (MDImage mdImage : tempImagesList) {
                        List<MDImage> images = new ArrayList<MDImage>();
                        images.add(mdImage);

                        Album album = null;

                        if (mdImage.isShared()) {
                            album = mAlbumsList.get(mAlbumsList.size() - 1);
                        } else {
                            album = mAlbumsList.get(mAlbumsList.size() - 2);
                        }

                        album.setImages(images);
                        album.setImageNum(mdImage.getPhotoCount());
                    }

                    mAlbumAdapter.bindAlbum(mAlbumsList);
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ViewUtils.dismiss();
            }
        });
    }

    public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

        private List<Album> mAlbumsList = new ArrayList<>();

        public void bindAlbum(List<Album> albumList) {
            this.mAlbumsList.clear();
            this.mAlbumsList.addAll(albumList);
            notifyDataSetChanged();
        }

        @Override
        public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_album_when_edit, parent, false);
            AlbumAdapter.ViewHolder holder = new AlbumAdapter.ViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(AlbumAdapter.ViewHolder holder, final int position) {
            final Album album = mAlbumsList.get(position);

            holder.viewDataBinding.setAlbum(album);
            holder.viewDataBinding.setViewHolder(holder);
            GlideUtil.loadImageByMDImage(holder.viewDataBinding.firstImage, album.getImages().get(0), true);
        }

        @Override
        public int getItemCount() {
            return mAlbumsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemSelectAlbumWhenEditBinding viewDataBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }

            public void toSelectImageActivityAction(View view) {
                Album album = mAlbumsList.get(getAdapterPosition());

                Intent intent = new Intent(SelectAlbumWhenEditActivity.this, SelectImageWhenEditActivity.class);
                intent.putExtra(Constants.KEY_ALBUM, album);
                startActivityForResult(intent, 0);
            }
        }
    }

}
