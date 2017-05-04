package com.hailan.HaiLanPrint.activity.messagecenter;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityMessageDetailBinding;
import com.hailan.HaiLanPrint.models.Message;
import com.hailan.HaiLanPrint.utils.JsonUtil;
import com.hailan.HaiLanPrint.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class MessageDetailActivity extends ToolbarActivity<ActivityMessageDetailBinding> {

    private Message message;

    @Override
    public int getContentLayout() {
        return R.layout.activity_message_detail;
    }

    @Override
    protected void initData() {
        message = getIntent().getParcelableExtra(Constants.KEY_MESSAGE);

        mDataBinding.tvTitle.setText(message.getTitle());
        mDataBinding.tvMessage.setText(message.getMessage());

        List<Message> messages = JsonUtil.fromJson(PreferenceUtils.getPrefString(Constants.KEY_PUSH_MESSAGE, ""), new TypeToken<List<Message>>() {
        });
        if (messages == null) {
            messages = new ArrayList<>();
        }
        List<Message> deleteList = new ArrayList<>();
        for (Message msg : messages) {
            if (message.getTitle().equals(msg.getTitle())) {
                deleteList.add(msg);
            }
        }
        messages.removeAll(deleteList);
        PreferenceUtils.setPrefString(Constants.KEY_PUSH_MESSAGE, JsonUtil.toJson(messages));

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.ACTION_PUSH));
    }

    @Override
    protected void setListener() {

    }

}

