package com.hailan.HaiLanPrint.activity.login;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityImproveInformationBinding;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.User;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.dialog.BirthdayDatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImproveInformationActivity extends ToolbarActivity<ActivityImproveInformationBinding>
        implements DatePickerDialog.OnDateSetListener {

    private User mUser;
    private boolean mIsFirstKidBirth = true;

    @Override
    public int getContentLayout() {
        return R.layout.activity_improve_information;
    }

    @Override
    protected void initData() {
        mToolbar.setNavigationIcon(null);

        mUser = (User) getIntent().getSerializableExtra(Constants.KEY_NEW_USER);
    }

    @Override
    protected void setListener() {
        mDataBinding.rltOptionalTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataBinding.lltOptional.getVisibility() == View.GONE) {
                    mDataBinding.ivCollapse.setRotation(90);
                    mDataBinding.lltOptional.setVisibility(View.VISIBLE);
                } else {
                    mDataBinding.ivCollapse.setRotation(0);
                    mDataBinding.lltOptional.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (mIsFirstKidBirth) {
//            mUser.setKidDOB1(DateUtils.getServerDateStringByDate(new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0, 0).toDate()));
            mDataBinding.tvChildBirthday1.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
        } else {
//            mUser.setKidDOB2(DateUtils.getServerDateStringByDate(new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0, 0).toDate()));
            mDataBinding.tvChildBirthday2.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
        }

    }

    //region ACTION
    public void chooseFirstKidBirthdayAction(View view) {
        mIsFirstKidBirth = true;
        Calendar calendar = Calendar.getInstance();
        new BirthdayDatePickerDialog(this, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void chooseSecondKidBirthdayAction(View view) {
        mIsFirstKidBirth = false;
        Calendar calendar = Calendar.getInstance();
        new BirthdayDatePickerDialog(this, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void finishAction(View view) {
        String name = mDataBinding.cetName.getText().toString();
        if (StringUtil.isEmpty(name)) {
            Toast.makeText(this, R.string.input_register_name, Toast.LENGTH_SHORT).show();
            return;
        }

        mUser.setUserName(name);

        String childName1 = mDataBinding.cetChildName1.getText().toString();
        mUser.setKidName1(childName1);

        String childName2 = mDataBinding.cetChildName2.getText().toString();
        mUser.setKidName2(childName2);

        String childSchool1 = mDataBinding.cetChildSchool1.getText().toString();
        mUser.setKidSchool1(childSchool1);

        String childSchool2 = mDataBinding.cetChildSchool2.getText().toString();
        mUser.setKidSchool2(childSchool2);

        String childClass1 = mDataBinding.cetChildClass1.getText().toString();
        mUser.setKidClass1(childClass1);

        String chilidClass2 = mDataBinding.cetChildClass2.getText().toString();
        mUser.setKidClass2(chilidClass2);

        String childBirth1 = mDataBinding.tvChildBirthday1.getText().toString();
        String childBirth2 = mDataBinding.tvChildBirthday2.getText().toString();
        SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if ("".equals(childBirth1)) {
            childBirth1 = null;
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date1 = simpleDateFormat.parse(childBirth1);
                childBirth1 = formats.format(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        if ("".equals(childBirth2)) {
            childBirth2 = null;
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date1 = simpleDateFormat.parse(childBirth2);
                childBirth2 = formats.format(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        mUser.setKidDOB1(childBirth1);
        mUser.setKidDOB2(childBirth2);

        registerUserRequest(mUser);
    }
    //endregion

    //region SERVER
    private void registerUserRequest(final User user) {
        GlobalRestful.getInstance().RegisterUser(mUser, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.body().getCode() == ResponseCode.Normal.getValue()) {
                    loginUserRequest(user.getPhone(), user.getPassword());
                } else {
                    ViewUtils.toast(response.body().getMessage());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void loginUserRequest(String phone, String password) {
        ViewUtils.loading(ImproveInformationActivity.this);

        GlobalRestful.getInstance()
                .LoginUser(phone, password, new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        ViewUtils.dismiss();
                        if (ResponseCode.isSuccess(response.body())) {
                            User user = response.body().getContent(User.class);
                            NavUtils.toMainActivity(ImproveInformationActivity.this);
                        } else {
                            Toast.makeText(ImproveInformationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                    }
                });
    }
    //endregion
}

