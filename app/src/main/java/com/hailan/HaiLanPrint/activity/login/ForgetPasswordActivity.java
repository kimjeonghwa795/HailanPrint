package com.hailan.HaiLanPrint.activity.login;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityForgetPasswordBinding;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.DeviceUtil;
import com.hailan.HaiLanPrint.utils.MD5Util;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends ToolbarActivity<ActivityForgetPasswordBinding> {

    private boolean mIsChangePassword;

    private String checkCode;

    @Override
    public int getContentLayout() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected void initData() {
        mIsChangePassword = getIntent().getBooleanExtra(Constants.KEY_CHANGE_PASSWORD, false);
        if (mIsChangePassword) {
            tvTitle.setText(R.string.change_password);
            mDataBinding.cetAccount.setEnabled(false);
            mDataBinding.cetAccount.setText(MDGroundApplication.sInstance.getLoginUser().getPhone());
        } else {
            mDataBinding.cetAccount.append(getIntent().getStringExtra(Constants.KEY_PHONE));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setListener() {
        mDataBinding.cbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ViewUtils.isShowPassword(isChecked, mDataBinding.cetPassword);
            }
        });
    }

    //region ACTION
    public void getCaptchaAction(View view) {
        String phone = mDataBinding.cetAccount.getText().toString();

        if (StringUtil.isEmpty(phone)) {
            ViewUtils.toast(R.string.input_phone_number);
            return;
        }

        if (phone.length() != 11) {
            ViewUtils.toast(R.string.input_corrent_phone);
            return;
        }

        GetCheckCode(phone);
    }

    public void finishAction(View view) {
        String phone = mDataBinding.cetAccount.getText().toString();

        if (StringUtil.isEmpty(phone)) {
            Toast.makeText(this, R.string.input_phone_number, Toast.LENGTH_SHORT).show();
            return;

        }
        if (phone.length() != 11) {
            Toast.makeText(this, R.string.input_corrent_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        String captcha = mDataBinding.cetCaptcha.getText().toString();
        if (StringUtil.isEmpty(captcha)) {
            Toast.makeText(this, R.string.input_captcha, Toast.LENGTH_SHORT).show();
            return;
        }

        String password = mDataBinding.cetPassword.getText().toString();
        if (StringUtil.isEmpty(password)) {
            Toast.makeText(this, R.string.input_password, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6 || password.length() > 16) {
            Toast.makeText(this, R.string.input_corrent_password, Toast.LENGTH_SHORT).show();
            return;
        }

        ViewUtils.loading(this);
        if (StringUtil.checkEquels(captcha, checkCode)) {
            changePswRequest();
        } else {
            ViewUtils.toast("验证码错误");
        }
    }
    //endregion

    //region SERVER
    private void changePswRequest() {
        String phone = mDataBinding.cetAccount.getText().toString();
        String password = mDataBinding.cetPassword.getText().toString();

        GlobalRestful.getInstance().ChangeUserPassword(phone, MD5Util.MD5(password), new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.body().getCode() == ResponseCode.Normal.getValue()) {
                    ViewUtils.toast(R.string.change_password_success);

                    if (mIsChangePassword) {
                        DeviceUtil.logoutUser(ForgetPasswordActivity.this);
                    } else {
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }

    /**
     * 获取验证码
     *
     * @param Phone
     */
    private void GetCheckCode(String Phone) {
        GlobalRestful.getInstance().GetCheckCode(Phone, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                mDataBinding.tvAcquireCaptcha.setClickable(false);

                CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mDataBinding.tvAcquireCaptcha.setText(getString(R.string.after_second_acquire_again, millisUntilFinished / 1000));
                    }

                    @Override
                    public void onFinish() {
                        mDataBinding.tvAcquireCaptcha.setClickable(true);
                        mDataBinding.tvAcquireCaptcha.setText(R.string.acquire_again);
                    }
                };
                countDownTimer.start();

                checkCode = response.body().getContent();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ViewUtils.toast("获取验证码失败");
            }
        });
    }
    //endregion
}

