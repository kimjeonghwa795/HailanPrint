package com.hailan.HaiLanPrint.activity.personalcenter;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.databinding.ActivityAboutUsBinding;
import com.hailan.HaiLanPrint.utils.StringUtil;

/**
 * Created by PC on 2016-06-15.
 */

public class AboutUsActivity extends ToolbarActivity<ActivityAboutUsBinding> {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void initData() {
        mDataBinding.tvVersion.setText(getString(R.string.version_with_colon, "V " + StringUtil.getVersion()));
    }

    @Override
    protected void setListener() {
    }

    //region ACTION
    //拨打电话
    public void toCallPhone(View view) {
        Uri uri = Uri.parse("tel:" + getString(R.string.hotline_num));
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);
    }

    //打开网站
    public void toUsWeb(View view) {
        Uri uri = Uri.parse("http://" + getString(R.string.official_website_url));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    //服务条款
    public void toRegisterProtocol(View view) {
        Intent intent = new Intent(this, TermServiceActivity.class);
        startActivity(intent);
    }

    //意见反馈
    public void toFeedBack(View view) {
        Intent intent = new Intent(this, FeedBackActivity.class);
        startActivity(intent);
    }
    //endregion
}
