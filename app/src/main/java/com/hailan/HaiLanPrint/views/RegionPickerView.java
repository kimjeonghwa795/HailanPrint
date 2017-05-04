package com.hailan.HaiLanPrint.views;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hailan.HaiLanPrint.BR;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ItemRegionBinding;
import com.hailan.HaiLanPrint.databinding.ViewRegionPickerBinding;
import com.hailan.HaiLanPrint.greendao.Location;
import com.hailan.HaiLanPrint.greendao.LocationDao;
import com.hailan.HaiLanPrint.views.itemdecoration.DividerItemDecoration;

import java.util.List;

/**
 * Created by yoghourt on 5/25/16.
 */

public class RegionPickerView extends LinearLayout {

    private enum RegionType {
        PROVINCE, CITY, COUNTY
    }

    private ViewRegionPickerBinding mDataBinding;

    private OnRegionSelectListener onRegionSelectListener;

    private Location mSelectProvince, mSelectCity, mSelectCounty;

    private RegionAdapter mProvinceAdapter, mCityAdapter, mCountyAdapter;

    private List<Location> mProvinceLocationArrayList;  // 省
    private List<Location> mCityLocationArrayList;      // 市
    private List<Location> mCountyRegionLocationArrayList; // 县

    //region INTERFACE
    public interface OnRegionSelectListener {
        public void onRegionSelect(Location province, Location city, Location region);
    }
    //endregion

    public RegionPickerView(Context context) {
        super(context);
        init(context);
    }

    public RegionPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RegionPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_region_picker, this, true);

        // 省
        {
            mProvinceLocationArrayList = getRegionDataByLocationID(86);  // 拿到所有省份
            mSelectProvince = mProvinceLocationArrayList.get(0);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mDataBinding.rvProvince.setLayoutManager(layoutManager);
            mDataBinding.rvProvince.addItemDecoration(new DividerItemDecoration(0));

            mProvinceAdapter = new RegionAdapter(RegionType.PROVINCE, mProvinceLocationArrayList);
            mDataBinding.rvProvince.setAdapter(mProvinceAdapter);
        }

        // 市
        {
            mCityLocationArrayList = getRegionDataByLocationID(mProvinceLocationArrayList.get(0).getLocationID());
            mSelectCity = mCityLocationArrayList.get(0);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mDataBinding.rvCity.setLayoutManager(layoutManager);
            mDataBinding.rvCity.addItemDecoration(new DividerItemDecoration(0));

            mCityAdapter = new RegionAdapter(RegionType.CITY, mCityLocationArrayList);
            mDataBinding.rvCity.setAdapter(mCityAdapter);
        }

        // 县
        {
            mCountyRegionLocationArrayList = getRegionDataByLocationID(mCityLocationArrayList.get(0).getLocationID());
            mSelectCounty = mCountyRegionLocationArrayList.get(0);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mDataBinding.rvCounty.setLayoutManager(layoutManager);
            mDataBinding.rvCounty.addItemDecoration(new DividerItemDecoration(0));

            mCountyAdapter = new RegionAdapter(RegionType.COUNTY, mCountyRegionLocationArrayList);
            mDataBinding.rvCounty.setAdapter(mCountyAdapter);
        }
    }

    private List<Location> getRegionDataByLocationID(long locationID) {
        return MDGroundApplication.sDaoSession.getLocationDao().queryBuilder().where(LocationDao.Properties.ParentID.eq(locationID)).list();
    }

    public void setOnRegionSelectListener(OnRegionSelectListener onRegionSelectListener) {
        this.onRegionSelectListener = onRegionSelectListener;
    }

    public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.ViewHolder> {

        private RegionType mRegionType;
        private List<Location> mLocationArrayList;

        public RegionAdapter(RegionType regionType, List<Location> locationArrayList) {
            this.mRegionType = regionType;
            this.mLocationArrayList = locationArrayList;
        }

        public void updateData(List<Location> locationArrayList) {
            mLocationArrayList.clear();
            mLocationArrayList.addAll(locationArrayList);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_region, parent, false);
            ViewHolder holder = new ViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.viewDataBinding.setVariable(BR.location, mLocationArrayList.get(position));
            holder.viewDataBinding.setVariable(BR.handlers, holder);
        }

        @Override
        public int getItemCount() {
            return mLocationArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ItemRegionBinding viewDataBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }

            public void onRegionSelectAction(View view) {

                Location location = mLocationArrayList.get(getAdapterPosition());
                switch (mRegionType) {
                    case PROVINCE:
                        mSelectProvince = location;

                        mCityLocationArrayList = getRegionDataByLocationID(mSelectProvince.getLocationID());
                        mCityAdapter.updateData(mCityLocationArrayList);
                        mDataBinding.rvCity.scrollToPosition(0);

                        mSelectCity = mCityLocationArrayList.get(0);
                        mCountyRegionLocationArrayList = getRegionDataByLocationID(mSelectCity.getLocationID());
                        mCountyAdapter.updateData(mCountyRegionLocationArrayList);
                        mDataBinding.rvCounty.scrollToPosition(0);
                        break;
                    case CITY:
                        mSelectCity = location;

                        mCountyRegionLocationArrayList = getRegionDataByLocationID(mSelectCity.getLocationID());
                        mCountyAdapter.updateData(mCountyRegionLocationArrayList);
                        mDataBinding.rvCounty.scrollToPosition(0);
                        break;
                    case COUNTY:
                        mSelectCounty = location;

                        if (onRegionSelectListener != null) {
                            onRegionSelectListener.onRegionSelect(mSelectProvince, mSelectCity, mSelectCounty);
                        }
                        break;
                }
            }
        }
    }
}
