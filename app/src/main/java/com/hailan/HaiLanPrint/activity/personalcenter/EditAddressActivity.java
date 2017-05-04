package com.hailan.HaiLanPrint.activity.personalcenter;

import android.content.Intent;
import android.view.View;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ActivityEditorAddressBinding;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
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
 * Created by PC on 2016-06-14.
 */
public class EditAddressActivity extends ToolbarActivity<ActivityEditorAddressBinding> {
    DeliveryAddress mAddresss;
    private RegionPickerDialog mRegionPickerDialog;
    boolean mIsUpdate = false;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_editor_address;
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mRegionPickerDialog = new RegionPickerDialog(this);
        int fromWhere = intent.getIntExtra(ManageAddressActivity.FROM_HERE, 0);
        if (fromWhere == (ManageAddressActivity.FOR_UPATE)) {
            mIsUpdate = true;
            mAddresss = intent.getParcelableExtra(ManageAddressActivity.ADDRESS);
            mDataBinding.etContacts.setText(mAddresss.getPhone());
            mDataBinding.etReceiverName.setText(mAddresss.getReceiver());
            mDataBinding.etAddress.setText(mAddresss.getStreet());
            mDataBinding.etReceiverName.setSelection(mAddresss.getReceiver().length());
            Location province = MDGroundApplication.sDaoSession.getLocationDao().load((long) mAddresss.getProvinceID());
            Location city = MDGroundApplication.sDaoSession.getLocationDao().load((long) mAddresss.getCityID());
            Location country = MDGroundApplication.sDaoSession.getLocationDao().load((long) mAddresss.getCountryID());
            mDataBinding.etRegio.setText(province.getLocationName() + " " + city.getLocationName() + "" + country.getLocationName());
        } else {
            mIsUpdate = false;
            tvTitle.setText(getString(R.string.add_address));
            mAddresss = new DeliveryAddress();
        }

    }

    @Override
    protected void setListener() {
        mDataBinding.tvAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddresss.setReceiver(mDataBinding.etReceiverName.getText().toString());
                mAddresss.setUserID(MDGroundApplication.sInstance.getLoginUser().getUserID());
                if (StringUtil.isEmpty(mDataBinding.etContacts.getText().toString())) {
                    ViewUtils.toast(R.string.input_phone_number);
                    return;
                }
                if (mDataBinding.etContacts.getText().toString().length() != 11) {
                    ViewUtils.toast(R.string.input_corrent_phone);
                    return;
                }
                mAddresss.setPhone(mDataBinding.etContacts.getText().toString());
                if (StringUtil.isEmpty(mDataBinding.etAddress.getText().toString())) {
                    ViewUtils.toast(R.string.input_detailed_address);
                    return;
                }
                mAddresss.setStreet(mDataBinding.etAddress.getText().toString());
                if (StringUtil.isEmpty(mDataBinding.etRegio.getText().toString())) {
                    ViewUtils.toast(R.string.input_local_region);
                    return;
                }
                addOrdeleteAddress(mAddresss);
            }
        });

        mRegionPickerDialog.setOnRegionSelectListener(new RegionPickerDialog.OnRegionSelectListener() {

            @Override
            public void onRegionSelect(Location province, final Location city, final Location county) {
                mAddresss.setProvinceID(Integer.parseInt(String.valueOf(province.getLocationID())));
                mAddresss.setCityID(Integer.parseInt(String.valueOf(city.getLocationID())));
                mAddresss.setCountryID(Integer.parseInt(String.valueOf(county.getLocationID())));
                mDataBinding.etRegio.setText(province.getLocationName() + "" + city.getLocationName() + " " + county.getLocationName());
            }

        });

    }

    //region SERVER

    public void addOrdeleteAddress(final DeliveryAddress address) {
        GlobalRestful.getInstance().SaveUserAddress(address, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (ResponseCode.isSuccess(response.body())) {
                    if (mIsUpdate) {
                        ViewUtils.toast(getString(R.string.update_success));
                    } else {
                        ViewUtils.toast(R.string.add_success);
                    }
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });

    }

    //endregion

    //region ACTION
    public void selectRegio(View view) {
        mRegionPickerDialog.show();
    }
    //endregion

}
