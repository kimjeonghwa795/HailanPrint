package com.hailan.HaiLanPrint.activity.personalcenter;

import android.app.DatePickerDialog;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ActivityChildInformationBinding;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.User;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.DateUtils;
import com.hailan.HaiLanPrint.views.dialog.BirthdayDatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by PC on 2016-06-13.
 */

public class ChildInformationActivity extends ToolbarActivity<ActivityChildInformationBinding>
        implements DatePickerDialog.OnDateSetListener, OnFocusChangeListener {
    private boolean mIsFirstKidDOB = true;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_child_information;
    }

    @Override
    protected void initData() {
        tvRight.setText(R.string.save);
        tvRight.setVisibility(View.VISIBLE);
        User user = MDGroundApplication.sInstance.getLoginUser();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if ((!"".equals(user.getKidDOB1()) && (user.getKidDOB1() != null))) {

            Date kidDOBdate1 = null;
            try {
                kidDOBdate1 = format.parse(user.getKidDOB1());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String DOB1 = DateUtils.getDateStringBySpecificFormat(kidDOBdate1, new SimpleDateFormat("yyyy-MM-dd"));
            user.setKidDOB1(DOB1);
        }
        if ((!"".equals(user.getKidDOB2()) && (user.getKidDOB2() != null))) {
            Date kidDOBdate2 = null;
            try {
                kidDOBdate2 = format.parse(user.getKidDOB2());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String DOB2 = DateUtils.getDateStringBySpecificFormat(kidDOBdate2, new SimpleDateFormat("yyyy-MM-dd"));
            user.setKidDOB2(DOB2);
        }
        mDataBinding.setUser(user);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (mIsFirstKidDOB) {
            mDataBinding.etFirstKidBirth.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
        } else {
            mDataBinding.etSecondKidBirth.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText editText = (EditText) v;
        if (!hasFocus) {// 失去焦点
            editText.setHint(editText.getTag().toString());
        } else {
            String hint = editText.getHint().toString();
            editText.setTag(hint);
            editText.setHint("");
        }
    }

    @Override
    protected void setListener() {

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstKidName = mDataBinding.etFirstKidName.getText().toString();
                String firstKidBirth = mDataBinding.etFirstKidBirth.getText().toString();
                String firstKidSchool = mDataBinding.etFirstKidSchool.getText().toString();
                String firstKidClass = mDataBinding.etFirstKidClass.getText().toString();
                String secondKidName = mDataBinding.etSecondKidName.getText().toString();
                String secondKidBirth = mDataBinding.etSecondKidBirth.getText().toString();
                String secondKidSchool = mDataBinding.etSecondKidSchool.getText().toString();
                String secondKidClass = mDataBinding.etSecondKidClass.getText().toString();
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String updateDate = DateUtils.getServerDateStringByDate(date);

                if ("".equals(firstKidBirth)) {
                    firstKidBirth = null;
                } else {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date1 = simpleDateFormat.parse(firstKidBirth);
                        firstKidBirth = formats.format(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                if ("".equals(secondKidBirth)) {
                    secondKidBirth = null;
                } else {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date1 = simpleDateFormat.parse(secondKidBirth);
                        secondKidBirth = formats.format(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                final User user = MDGroundApplication.sInstance.getLoginUser();
                user.setUpdatedTime(updateDate);
                user.setKidName1(firstKidName);
                user.setKidDOB1(firstKidBirth);
                user.setKidClass1(firstKidClass);
                user.setKidSchool1(firstKidSchool);

                user.setKidName2(secondKidName);
                user.setKidDOB2(secondKidBirth);
                user.setKidClass2(secondKidClass);
                user.setKidSchool2(secondKidSchool);

                GlobalRestful.getInstance().SaveUserInfo(user, new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        if (ResponseCode.isSuccess(response.body())) {
                            MDGroundApplication.sInstance.setLoginUser(user);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                    }
                });
            }
        });

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

    //region ACTION
    public void choseFirstChildBirthDay(View view) {
        mIsFirstKidDOB = true;
        Calendar calendar = Calendar.getInstance();
        new BirthdayDatePickerDialog(this, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void choseSecondChildBirthDay(View view) {
        mIsFirstKidDOB = false;
        Calendar calendar = Calendar.getInstance();
        new BirthdayDatePickerDialog(this, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    //endregion
}
