package com.hailan.HaiLanPrint.views;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.adapter.wheelview.LocationWheelAdapter;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ViewRegionWheelviewBinding;
import com.hailan.HaiLanPrint.greendao.Location;
import com.hailan.HaiLanPrint.greendao.LocationDao;

import java.util.List;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;

/**
 * Created by yoghourt on 5/25/16.
 */

public class RegionWheelView extends LinearLayout implements OnWheelScrollListener {

    private enum RegionType {
        PROVINCE, CITY, COUNTY
    }

    private ViewRegionWheelviewBinding mDataBinding;

    public Location mSelectProvince, mSelectCity, mSelectCounty;

    private List<Location> mProvinceLocationArrayList;  // 省
    private List<Location> mCityLocationArrayList;      // 市
    private List<Location> mCountyRegionLocationArrayList; // 县

    private boolean mIsScrolling;

    public RegionWheelView(Context context) {
        super(context);
        init(context);
    }

    public RegionWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RegionWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_region_wheelview, this, true);

        // 省
        {
            mProvinceLocationArrayList = getRegionDataByLocationID(86);  // 拿到所有省份
            mSelectProvince = mProvinceLocationArrayList.get(0);

            mDataBinding.wvProvince.setViewAdapter(new LocationWheelAdapter(context, mProvinceLocationArrayList));
            mDataBinding.wvProvince.setVisibleItems(7);
            mDataBinding.wvProvince.addScrollingListener(this);
        }

        // 市
        {
            mCityLocationArrayList = getRegionDataByLocationID(mProvinceLocationArrayList.get(0).getLocationID());
            mSelectCity = mCityLocationArrayList.get(0);

            mDataBinding.wvCity.setViewAdapter(new LocationWheelAdapter(context, mCityLocationArrayList));
            mDataBinding.wvCity.setVisibleItems(7);
            mDataBinding.wvCity.addScrollingListener(this);
        }

        // 县
        {
            mCountyRegionLocationArrayList = getRegionDataByLocationID(mCityLocationArrayList.get(0).getLocationID());
            mSelectCounty = mCountyRegionLocationArrayList.get(0);

            mDataBinding.wvCounty.setViewAdapter(new LocationWheelAdapter(context, mCountyRegionLocationArrayList));
            mDataBinding.wvCounty.setVisibleItems(7);
            mDataBinding.wvCounty.addScrollingListener(this);
        }
    }

    @Override
    public void onScrollingStarted(WheelView wheel) {
        mIsScrolling = true;
    }

    @Override
    public void onScrollingFinished(WheelView wheel) {
        mIsScrolling = false;

        int currentPosition = wheel.getCurrentItem();
        if (wheel == mDataBinding.wvProvince) {
            mSelectProvince = mProvinceLocationArrayList.get(currentPosition);

            updateCityData();
            updateCountyData();
        } else if (wheel == mDataBinding.wvCity) {
            mSelectCity = mCityLocationArrayList.get(currentPosition);

            updateCountyData();
        } else if (wheel == mDataBinding.wvCounty) {
            mSelectCounty = mCountyRegionLocationArrayList.get(currentPosition);
        }
    }

    private void updateCityData() {
        mCityLocationArrayList = getRegionDataByLocationID(mSelectProvince.getLocationID());
        mSelectCity = mCityLocationArrayList.get(0);
        mDataBinding.wvCity.setViewAdapter(new LocationWheelAdapter(getContext(), mCityLocationArrayList));
        mDataBinding.wvCity.setCurrentItem(0);
    }

    private void updateCountyData() {
        mCountyRegionLocationArrayList = getRegionDataByLocationID(mSelectCity.getLocationID());
        if (mCountyRegionLocationArrayList.size() > 0) {
            mSelectCounty = mCountyRegionLocationArrayList.get(0);
        }
        mDataBinding.wvCounty.setViewAdapter(new LocationWheelAdapter(getContext(), mCountyRegionLocationArrayList));
        mDataBinding.wvCounty.setCurrentItem(0);
    }

    private List<Location> getRegionDataByLocationID(long locationID) {
        return MDGroundApplication.sDaoSession.getLocationDao().queryBuilder().where(LocationDao.Properties.ParentID.eq(locationID)).list();
    }

    public boolean isFinishSelect() {
        return !mIsScrolling;
    }
}
