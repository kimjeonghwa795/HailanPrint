package com.hailan.HaiLanPrint.activity.personalcenter;

import android.content.Intent;
import android.view.View;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.activity.login.ForgetPasswordActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityPersonalInformationBinding;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.greendao.Location;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.User;
import com.hailan.HaiLanPrint.restfuls.FileRestful;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.DateUtils;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.PreferenceUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.views.dialog.RegionPickerDialog;
import com.hailan.HaiLanPrint.views.dialog.SelectSingleImageDialog;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import me.nereo.multi_image_selector.MultiImageSelector;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/30/16.
 */

public class PersonalInformationActivity extends ToolbarActivity<ActivityPersonalInformationBinding> {

    public static final int FRO_PERSON = 1;

    private User mUser;
    private SelectSingleImageDialog mSelectSingleImageDialog;
    private ArrayList<String> mUploadImageLocalPathList = new ArrayList<>();
    private RegionPickerDialog mRegionPickerDialog;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_personal_information;
    }

    @Override
    protected void initData() {
        mRegionPickerDialog = new RegionPickerDialog(this);
        mSelectSingleImageDialog = new SelectSingleImageDialog(PersonalInformationActivity.this, R.style.customDialogStyle);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mUser = MDGroundApplication.sInstance.getLoginUser();
        // 用户头像
        MDImage mdImage = new MDImage();
        mdImage.setPhotoID(mUser.getPhotoID());
        mdImage.setPhotoSID(mUser.getPhotoSID());
        GlideUtil.loadImageByMDImage(mDataBinding.civAvatar, mdImage, true);
        mDataBinding.tvPhone.setText(mUser.getPhone());
        mDataBinding.tvAccountName.setText(mUser.getUserName());

        mDataBinding.tvNickname.setText(mUser.getUserNickName());

        Location city = MDGroundApplication.sDaoSession.getLocationDao().load((long) mUser.getCityID());
        Location county = MDGroundApplication.sDaoSession.getLocationDao().load((long) mUser.getDistrictID());
        String cityStr = "", countyStr = "";
        if (city != null && county != null) {
            cityStr = city.getLocationName();
            countyStr = county.getLocationName();
        }
        cityStr = PreferenceUtils.getPrefString(Constants.KEY_CITY, cityStr);
        countyStr = PreferenceUtils.getPrefString(Constants.KEY_COUNTY, countyStr);
        mDataBinding.tvLocality.setText(cityStr + " " + countyStr);
    }

    @Override
    protected void setListener() {
        //修改地址
        mRegionPickerDialog.setOnRegionSelectListener(new RegionPickerDialog.OnRegionSelectListener() {
            @Override
            public void onRegionSelect(final Location province, final Location city, final Location county) {

                mUser.setProvinceID(Integer.parseInt(String.valueOf(province.getLocationID())));
                mUser.setCityID(Integer.parseInt(String.valueOf(city.getLocationID())));
                mUser.setCountryID(Integer.parseInt(String.valueOf(county.getLocationID())));
                GlobalRestful.getInstance().SaveUserInfo(mUser, new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        if (ResponseCode.isSuccess(response.body())) {
                            mDataBinding.tvLocality.setText(city.getLocationName() + " " + county.getLocationName());
                            MDGroundApplication.sInstance.setLoginUser(mUser);

                            PreferenceUtils.setPrefString(Constants.KEY_PROVINCE, province.getLocationName());
                            PreferenceUtils.setPrefString(Constants.KEY_CITY, city.getLocationName());
                            PreferenceUtils.setPrefString(Constants.KEY_COUNTY, county.getLocationName());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectSingleImageDialog.PHOTO_REQUEST_GALLERY) {// 从相册返回的数据
//                Uri uri = data.getData();
//                String pro[] = {MediaStore.Images.Media.DATA};
//                Cursor cursor = managedQuery(uri, pro, null, null, null);
//                int Column_index = cursor.getColumnIndexOrThrow(pro[0]);
//                cursor.moveToFirst();
//                String Picturepath = cursor.getString(Column_index);
//                KLog.e("picturePath" + Picturepath);
//                uploadAvatar(Picturepath);

                ArrayList<String> list = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                uploadAvatar(list.get(0));

            } else if (requestCode == SelectSingleImageDialog.PHOTO_REQUEST_CAREMA) {// 从相机返回的数据
                KLog.e("相机返回数据");

//                Uri uri = data.getData();
//                String picturePath = FileUtils.getAbsoluteImagePath(PersonalInformationActivity.this, uri);
                String picturePath = SelectSingleImageDialog.sCaptureImageURL;
                uploadAvatar(picturePath);
            }

        }

    }

    //region SERVER
    private void uploadImageRequest(final int upload_image_index) {
        if (upload_image_index < mUploadImageLocalPathList.size()) {

            String imagePath = mUploadImageLocalPathList.get(upload_image_index);

            File file = new File(imagePath);

            KLog.e("localMedia.getImageLocalPath() : " + imagePath);

            FileRestful.getInstance().UploadCloudPhoto(false, file, null, new Callback<ResponseData>() {
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
        }
    }

    public void uploadAvatar(String Picturepath) {
        if (Picturepath != null) {
            File file = new File(Picturepath);
            int userID = mUser.getUserID();
            Date date = new Date(System.currentTimeMillis());
            String updatedTime = DateUtils.getServerDateStringByDate(date);
            mUser.setUpdatedTime(updatedTime);
//                final String finalPicturepath = Picturepath;
            FileRestful.getInstance().SaveUserPhoto(userID, file, mUser, null, new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                    if (ResponseCode.isSuccess(response.body())) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().getContent());
                            String jsonStr = jsonObject.toString();

                            User user = StringUtil.getInstanceByJsonString(jsonStr, User.class);
                            mUser.setPhotoID(user.getPhotoID());
                            mUser.setPhotoSID(user.getPhotoSID());
                            MDGroundApplication.sInstance.setLoginUser(mUser);

                            final MDImage mdImage = new MDImage();
                            mdImage.setPhotoID(user.getPhotoID());
                            mdImage.setPhotoSID(user.getPhotoSID());
                            mDataBinding.civAvatar.post(new Runnable() {
                                @Override
                                public void run() {
                                    GlideUtil.loadImageByMDImage(mDataBinding.civAvatar, mdImage, false);
                                }
                            });
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
    }
    //endregion

    //region ACTION
    //修改昵称
    public void toChangeNameAction(View view) {
        Intent intent = new Intent(this, ChangeNameActivity.class);
        startActivity(intent);
    }

    public void selectSingleImageAction(View view) {
        mSelectSingleImageDialog.show();
    }

    //设置孩子资料
    public void setChildData(View view) {
        Intent intent = new Intent(this, ChildInformationActivity.class);
        startActivity(intent);
    }


    //修改所在地
    public void selectAddress(View view) {
        mRegionPickerDialog.show();
    }

    public void toManageAddressActivity(View view) {
        Intent intent = new Intent(this, ManageAddressActivity.class);
        startActivity(intent);
    }

    public void toSetPassword(View view) {
        Intent intent = new Intent(this, ForgetPasswordActivity.class);
        intent.putExtra(Constants.KEY_CHANGE_PASSWORD, true);
        startActivity(intent);
    }

    //endregion

}
