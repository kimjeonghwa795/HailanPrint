package com.hailan.HaiLanPrint.activity.template;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivitySelectTemplateBinding;
import com.hailan.HaiLanPrint.databinding.ItemSelectTemplateBinding;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.PhotoTemplate2;
import com.hailan.HaiLanPrint.models.Template;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.SelectImageUtils;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.itemdecoration.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/30/16.
 */

public class SelectTemplateActivity extends ToolbarActivity<ActivitySelectTemplateBinding> {

    private final int mCountPerLine = 2; // 每行显示2个

    private ArrayList<PhotoTemplate2> mAllTemplateTypeList = new ArrayList<>();

    private ArrayList<Template> mAllTemplateArrayList = new ArrayList<>();

    private ArrayList<Template> mShowTemplateArrayList = new ArrayList<>();

    private HashMap<Integer, ArrayList<MDImage>> mTemplateCoverImageListHashMap = new HashMap<>();

    private SelectTemplateAdapter mAdapter;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_select_template;
    }

    @Override
    protected void initData() {
        mDataBinding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(mCountPerLine, ViewUtils.dp2px(10), true));
        mDataBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, mCountPerLine));

        mAdapter = new SelectTemplateAdapter();
        mDataBinding.recyclerView.setAdapter(mAdapter);

        if (MDGroundApplication.sInstance.getChoosedProductType() == ProductType.PictureFrame) {
//            mDataBinding.tabLayout.setVisibility(View.GONE);
            tvTitle.setText(R.string.choose_frame);

//            getPhotoTemplateListByTypeRequest();
            getPhotoTemplateTypeListRequest();

        } else {
//            String[] titles = getResources().getStringArray(R.array.template_class_array);
//
//            for (String title : titles) {
//                mDataBinding.tabLayout.addTab(mDataBinding.tabLayout.newTab().setText(title));
//            }

            getPhotoTemplateTypeListRequest();
        }

    }

    @Override
    protected void setListener() {
        mDataBinding.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int currentSelectedTabIndex = tab.getPosition();

                mShowTemplateArrayList.clear();
                if (currentSelectedTabIndex == 0) {
                    mShowTemplateArrayList.addAll(mAllTemplateArrayList);
                } else {
//                    for (Template template : mAllTemplateArrayList) {
//                        if (template.getParentID() == currentSelectedTabIndex) {
//                            mShowTemplateArrayList.add(template);
//                        }
//                    }
                    PhotoTemplate2 photoTemplate2 = mAllTemplateTypeList.get(currentSelectedTabIndex - 1);
                    for (Template template : mAllTemplateArrayList) {
                        if (template.getParentID() == photoTemplate2.getTemplateID()) {
                            mShowTemplateArrayList.add(template);
                        }
                    }
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //region SERVER
    private void getPhotoTemplateTypeListRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetPhotoTemplateTypeList(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                mAllTemplateTypeList = response.body().getContent(new TypeToken<ArrayList<PhotoTemplate2>>() {
                });

                mDataBinding.tabLayout.addTab(mDataBinding.tabLayout.newTab().setText(R.string.all));
                for (PhotoTemplate2 photoTemplate2 : mAllTemplateTypeList) {
                    mDataBinding.tabLayout.addTab(mDataBinding.tabLayout.newTab().setText(photoTemplate2.getTemplateName()));
                }
                if (MDGroundApplication.sInstance.getChoosedProductType() == ProductType.PictureFrame) {
                    getPhotoTemplateListByTypeRequest();
                } else {
                    getPhotoTemplateListRequest();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void getPhotoTemplateListByTypeRequest() {
        GlobalRestful.getInstance().GetPhotoTemplateListByType(MDGroundApplication.sInstance.getChoosedProductType(),
                new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        ViewUtils.dismiss();
                        if (ResponseCode.isSuccess(response.body())) {
                            mAllTemplateArrayList = response.body().getContent(new TypeToken<ArrayList<Template>>() {
                            });

                            mShowTemplateArrayList.addAll(mAllTemplateArrayList);

                            mAdapter.notifyDataSetChanged();
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
                            mAllTemplateArrayList = response.body().getContent(new TypeToken<ArrayList<Template>>() {
                            });

                            mShowTemplateArrayList.addAll(mAllTemplateArrayList);

                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        ViewUtils.dismiss();
                    }
                });
    }

    private void getPhotoTemplateAttachListRequest(final int templateID) {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetPhotoTemplateAttachList(templateID, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                SelectImageUtils.sTemplateImage.clear();

                SelectImageUtils.sTemplateImage = response.body().getContent(new TypeToken<ArrayList<MDImage>>() {
                });

                Intent intent = new Intent(SelectTemplateActivity.this, TemplateStartCreateActivity.class);
                intent.putExtra(Constants.KEY_COVER_IMAGE_LIST, mTemplateCoverImageListHashMap.get(templateID));
                startActivity(intent);
                ViewUtils.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void GetPhotoTemplateCoverList(final int templateID) {
        GlobalRestful.getInstance().GetPhotoTemplateCoverList(templateID, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ArrayList<MDImage> templateImageArrayList = response.body().getContent(new TypeToken<ArrayList<MDImage>>() {
                });
                mTemplateCoverImageListHashMap.put(templateID, templateImageArrayList);
                getPhotoTemplateAttachListRequest(MDGroundApplication.sInstance.getChoosedTemplate().getTemplateID());
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    //endregion

    //region ADAPTER
    public class SelectTemplateAdapter extends RecyclerView.Adapter<SelectTemplateAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_template, parent, false);
            ViewHolder holder = new ViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Template template = mShowTemplateArrayList.get(position);
            holder.viewDataBinding.setTemplate(template);
            holder.viewDataBinding.setViewHolder(holder);

            if (MDGroundApplication.sInstance.getChoosedProductType() == ProductType.PictureFrame) {
                holder.viewDataBinding.tvPrice.setVisibility(View.INVISIBLE);
            }

            GlideUtil.loadImageByPhotoSID(holder.viewDataBinding.ivImage, template.getPhotoCover(), true);
            holder.viewDataBinding.tvPage.setText(MDGroundApplication.sInstance.getString(R.string.page_count, template.getPageCount()));
            holder.viewDataBinding.tvName.setText(template.getTemplateName());

            if (MDGroundApplication.sInstance.getChoosedProductType() == ProductType.PictureFrame
                    || MDGroundApplication.sInstance.getChoosedProductType() == ProductType.LOMOCard
                    || MDGroundApplication.sInstance.getChoosedProductType() == ProductType.MagicCup
                    || MDGroundApplication.sInstance.getChoosedProductType() == ProductType.Engraving) {
                holder.viewDataBinding.tvPageBackground.setVisibility(View.INVISIBLE);
            }

//            GlobalRestful.getInstance().GetPhotoTemplateCoverList(template.getTemplateID(), new Callback<ResponseData>() {
//                @Override
//                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
//                    ArrayList<MDImage> templateImageArrayList = response.body().getContent(new TypeToken<ArrayList<MDImage>>() {
//                    });
//                    mTemplateCoverImageListHashMap.put(template.getTemplateID(), templateImageArrayList);
//
//                    if (templateImageArrayList.size() > 0) {
//                        GlideUtil.loadImageByMDImage(holder.viewDataBinding.ivImage, templateImageArrayList.get(0), true);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseData> call, Throwable t) {
//
//                }
//            });

//            GlobalRestful.getInstance().GetPhotoTemplateAttachList(template.getTemplateID(), new Callback<ResponseData>() {
//                @Override
//                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
//                    ArrayList<MDImage> templateImageArrayList = response.body().getContent(new TypeToken<ArrayList<MDImage>>() {
//                    });
//                    mTemplateAttachListHashMap.put(template.getTemplateID(), templateImageArrayList);
//
//                    if (templateImageArrayList.size() > 0) {
//                        GlideUtil.loadImageByMDImage(holder.viewDataBinding.ivImage, templateImageArrayList.get(0), true);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseData> call, Throwable t) {
//
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return mShowTemplateArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ItemSelectTemplateBinding viewDataBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }

            public void onTemplateImageClickAction(View view) {
                MDGroundApplication.sInstance.setChoosedTemplate(mShowTemplateArrayList.get(getAdapterPosition()));

                switch (MDGroundApplication.sInstance.getChoosedProductType()) {
                    case Postcard:
                    case MagazineAlbum:
                    case ArtAlbum:
                    case Calendar:
                    case LOMOCard:
                    case Puzzle:
                    case MagicCup:
                    case Engraving:
                    case PictureFrame:
                    case Poker:
//                        getPhotoTemplateAttachListRequest(MDGroundApplication.sInstance.getChoosedTemplate().getTemplateID());
                        GetPhotoTemplateCoverList(MDGroundApplication.sInstance.getChoosedTemplate().getTemplateID());
                        break;
                    default:
                        NavUtils.toSelectAlbumActivity(view.getContext());
                }
            }
        }
    }
    //endregion
}
