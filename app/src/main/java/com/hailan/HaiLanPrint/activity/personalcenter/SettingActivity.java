package com.hailan.HaiLanPrint.activity.personalcenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivitySettingBinding;
import com.hailan.HaiLanPrint.enumobject.SettingType;
import com.hailan.HaiLanPrint.models.SystemSetting;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.DeviceUtil;
import com.hailan.HaiLanPrint.utils.FileUtils;
import com.hailan.HaiLanPrint.utils.PreferenceUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.dialog.NotifyDialog;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/30/16.
 */

public class SettingActivity extends ToolbarActivity<ActivitySettingBinding> {

    private NotifyDialog mNotifyDialog;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {
        boolean isOnlyWifiUpdate = PreferenceUtils.getPrefBoolean(Constants.KEY_ONLY_WIFI_UPLOAD, false);
        mDataBinding.cbOnlyWifiUpload.setChecked(isOnlyWifiUpdate);

        mDataBinding.tvVersion.setText("V " + StringUtil.getVersion());
        mDataBinding.tvCache.setText(getCacheSize());
    }

    @Override
    protected void setListener() {
        mDataBinding.cbOnlyWifiUpload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.setPrefBoolean(Constants.KEY_ONLY_WIFI_UPLOAD, isChecked);
            }
        });
    }

    private String getCacheSize() {
        String applicationDirectory = getApplicationInfo().dataDir;
        String glideCacheDirectory = applicationDirectory + File.separator + "cache" + File.separator + Constants.GLIDE_DISK_CACHE_FILE_NAME;
        File file = new File(glideCacheDirectory);
        if (file != null && file.exists()) {
            long fileSizeInBytes = FileUtils.getFileSize(file);
            long fileSizeInKB = fileSizeInBytes / 1024;
            long fileSizeInMB = fileSizeInKB / 1024;
            DecimalFormat form = new DecimalFormat("0.00");
            return form.format(fileSizeInMB) + "M";
        } else {
            return "0M";
        }
    }

    //region ACTION
    public void clearCacheAction(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_clear_cache);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(SettingActivity.this).clearDiskCache();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDataBinding.tvCache.setText(getCacheSize());
                            }
                        });
                    }
                }).start();
            }
        });
        builder.show();

//        if (mNotifyDialog == null) {
//            mNotifyDialog = new NotifyDialog(this);
//            mNotifyDialog.setOnSureClickListener(new NotifyDialog.OnSureClickListener() {
//                @Override
//                public void onSureClick() {
//                    mNotifyDialog.dismiss();
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Glide.get(SettingActivity.this).clearDiskCache();
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mDataBinding.tvCache.setText(getCacheSize());
//                                }
//                            });
//                        }
//                    }).start();
//                }
//            });
//            mNotifyDialog.show();
//
//            mNotifyDialog.setTvContent(getString(R.string.confirm_clear_cache));
//        }
    }

    public void toAboutUsActivityAction(View view) {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    public void logoutAction(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_log_out);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeviceUtil.logoutUser(SettingActivity.this);
            }
        });
        builder.show();
    }

    /**
     * 检查更新
     *
     * @param view
     */
    public void checkUpdate(View view) {
        GlobalRestful.getInstance().GetSystemSetting(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ArrayList<SystemSetting> systemSettingArrayList = response.body().getContent(new TypeToken<ArrayList<SystemSetting>>() {
                });
                for (SystemSetting systemSetting : systemSettingArrayList) {
                    SettingType settingType = SettingType.fromValue(systemSetting.getSettingType());
                    if (settingType == SettingType.ANDROIDVersion) {
                        if (systemSetting.getValue() > StringUtil.getVersionCode()) {
                            showUpdateDialog(systemSetting.getValueDesc());

                        } else {
                            ViewUtils.toast("已是最新版本");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }

    private void showUpdateDialog(final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("检测到新版本，是否下载更新？");
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        builder.show();
    }
    //endregion
}
