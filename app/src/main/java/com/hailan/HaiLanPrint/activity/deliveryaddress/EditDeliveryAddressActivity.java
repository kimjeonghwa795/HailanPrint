package com.hailan.HaiLanPrint.activity.deliveryaddress;

import android.content.Intent;
import android.view.View;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityEditDeliveryAddressBinding;
import com.hailan.HaiLanPrint.greendao.Location;
import com.hailan.HaiLanPrint.models.DeliveryAddress;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.dialog.RegionPickerDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/24/16.
 */

public class EditDeliveryAddressActivity extends ToolbarActivity<ActivityEditDeliveryAddressBinding> {

    private DeliveryAddress mDeliveryAddress;

    private RegionPickerDialog mRegionPickerDialog;

    private boolean mIsAddAddress;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_edit_delivery_address;
    }

    @Override
    protected void initData() {
        mRegionPickerDialog = new RegionPickerDialog(this);
        mDeliveryAddress = getIntent().getParcelableExtra(Constants.KEY_DELIVERY_ADDRESS);
        if (mDeliveryAddress != null) {
            mDataBinding.cetConsignee.setText(mDeliveryAddress.getReceiver());
            mDataBinding.mfetContactNumber.setText(mDeliveryAddress.getPhone());
            mDataBinding.etDetailedAddress.setText(mDeliveryAddress.getStreet());
            Location province = MDGroundApplication.sDaoSession.getLocationDao().load(mDeliveryAddress.getProvinceID());
            Location city = MDGroundApplication.sDaoSession.getLocationDao().load(mDeliveryAddress.getCityID());
            Location county = MDGroundApplication.sDaoSession.getLocationDao().load(mDeliveryAddress.getDistrictID());
            mDataBinding.tvRegion.setText(province.getLocationName() + city.getLocationName() + county.getLocationName());
        } else {
            mIsAddAddress = true;

            mDeliveryAddress = new DeliveryAddress();
            mDeliveryAddress.setCountryID(86);
            mDeliveryAddress.setProvinceID(110000);
            mDeliveryAddress.setCityID(110100);
            mDeliveryAddress.setDistrictID(110101);
            mDeliveryAddress.setUserID(MDGroundApplication.sInstance.getLoginUser().getUserID());
            mDataBinding.tvRegion.setText(R.string.defalut_address);
            tvTitle.setText(R.string.add_address);
        }
    }

    @Override
    protected void setListener() {
        mRegionPickerDialog.setOnRegionSelectListener(new RegionPickerDialog.OnRegionSelectListener() {
            @Override
            public void onRegionSelect(Location province, Location city, Location county) {
                mDeliveryAddress.setProvinceID(province.getLocationID());
                mDeliveryAddress.setCityID(city.getLocationID());
                mDeliveryAddress.setDistrictID(county.getLocationID());
                mDataBinding.tvRegion.setText(province.getLocationName() + city.getLocationName() + county.getLocationName());
            }
        });
    }

    //region ACTION
    public void chooseRegionAction(View view) {
        mRegionPickerDialog.show();
    }

    public void saveAction(View view) {
        String consignee = mDataBinding.cetConsignee.getText().toString();
        if (StringUtil.isEmpty(consignee)) {
            ViewUtils.toast(R.string.input_consignee);
            return;
        }
        mDeliveryAddress.setReceiver(consignee);

        String phone = mDataBinding.mfetContactNumber.getText().toString();
        if (StringUtil.isEmpty(phone)) {
            ViewUtils.toast(R.string.input_phone_number);
            return;
        }
        if (phone.length() != 11) {
            ViewUtils.toast(R.string.input_corrent_phone);
            return;
        }
        mDeliveryAddress.setPhone(phone);

        String detailedAddress = mDataBinding.etDetailedAddress.getText().toString();
        if (StringUtil.isEmpty(detailedAddress)) {
            ViewUtils.toast(R.string.input_detailed_address);
            return;
        }
        mDeliveryAddress.setStreet(detailedAddress);

        saveUserAddressRequest();
    }
    //endregion

    private void saveUserAddressRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().SaveUserAddress(mDeliveryAddress, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();

                DeliveryAddress newDeliveryAddress = response.body().getContent(DeliveryAddress.class);

                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_DELIVERY_ADDRESS, newDeliveryAddress);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }
}
