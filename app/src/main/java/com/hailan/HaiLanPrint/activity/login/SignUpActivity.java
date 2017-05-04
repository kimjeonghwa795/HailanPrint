package com.hailan.HaiLanPrint.activity.login;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivitySignUpBinding;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.User;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.MD5Util;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends ToolbarActivity<ActivitySignUpBinding> {

    private String checkCode;

    @Override
    public int getContentLayout() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initData() {
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

    private void toImproveInformationActivity() {
        String phone = mDataBinding.cetAccount.getText().toString();
        String password = mDataBinding.cetPassword.getText().toString();
        String invitationCode = mDataBinding.cetInviteCode.getText().toString();

        User newUser = new User();

        newUser.setPhone(phone);
        newUser.setPassword(MD5Util.MD5(password));
        newUser.setInvitationCode(invitationCode);

        Intent intent = new Intent(this, ImproveInformationActivity.class);
        intent.putExtra(Constants.KEY_NEW_USER, newUser);
        startActivity(intent);

        finish();
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

    public void nextStepAction(View view) {
        final String phone = mDataBinding.cetAccount.getText().toString();

        if (StringUtil.isEmpty(phone)) {
            Toast.makeText(this, R.string.input_phone_number, Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() != 11) {
            Toast.makeText(SignUpActivity.this, R.string.input_corrent_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        final String captcha = mDataBinding.cetCaptcha.getText().toString();
        if (StringUtil.isEmpty(captcha)) {
            Toast.makeText(SignUpActivity.this, R.string.input_captcha, Toast.LENGTH_SHORT).show();
            return;
        }

        String password = mDataBinding.cetPassword.getText().toString();

        if (StringUtil.isEmpty(password)) {
            Toast.makeText(this, R.string.input_password, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6 || password.length() > 16) {
            Toast.makeText(SignUpActivity.this, R.string.input_corrent_password, Toast.LENGTH_SHORT).show();
            return;
        }

        GlobalRestful.getInstance().CheckUserPhone(phone, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (ResponseCode.isSuccess(response.body())) {
                    ViewUtils.toast(R.string.phone_already_signup);
                } else {

                    if (StringUtil.checkEquels(captcha, checkCode)) {
                        toImproveInformationActivity();
                    } else {
                        ViewUtils.toast("验证码错误");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }

    public void protocolAction(View view) {
        Intent intent = new Intent(this, ProtocolActivity.class);
        startActivity(intent);
    }

    public void loginAction(View view) {
        finish();
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

