package com.hailan.HaiLanPrint.activity.personalcenter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityPersonalCreditBinding;
import com.hailan.HaiLanPrint.databinding.ItemCreditQueryBinding;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.Message;
import com.hailan.HaiLanPrint.models.SystemSetting;
import com.hailan.HaiLanPrint.models.UserIntegral;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.JsonUtil;
import com.hailan.HaiLanPrint.utils.PreferenceUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.itemdecoration.DividerItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by PC on 2016-06-08.
 */
public class MyCreditActivity extends ToolbarActivity<ActivityPersonalCreditBinding> {

    public String mTotalAmount;
    public ArrayList<UserIntegral> mUserCreditList = new ArrayList<>();

    MyCreditadapter mAdapter;
    private SystemSetting setting;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_personal_credit;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        getSpecificationRequest();
    }

    @Override
    protected void onDestroy() {
        List<Message> messages = JsonUtil.fromJson(PreferenceUtils.getPrefString(Constants.KEY_PUSH_MESSAGE, ""), new TypeToken<List<Message>>() {
        });
        if (messages == null) {
            messages = new ArrayList<>();
        }
        List<Message> deleteList = new ArrayList<>();
        for (Message message : messages) {
            if (message.isCreditType()) {
                deleteList.add(message);
            }
        }
        messages.removeAll(deleteList);
        PreferenceUtils.setPrefString(Constants.KEY_PUSH_MESSAGE, JsonUtil.toJson(messages));

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.ACTION_PUSH));

        super.onDestroy();
    }

    public class BindHandler {
        public void ClicEvent(View v) {
            int position = mDataBinding.recyclerView.getChildAdapterPosition(v);
        }
    }

    @Override
    protected void setListener() {

    }

    //region SERVER
    public void getSpecificationRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetUserIntegralInfo(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (ResponseCode.isSuccess(response.body())) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().getContent());
                        mTotalAmount = jsonObject.getString("TotalAmount");
                        String UserIntegralList = jsonObject.getString("UserIntegralList");
                        mUserCreditList = StringUtil.getInstanceByJsonString(UserIntegralList, new TypeToken<ArrayList<UserIntegral>>() {
                        });

                        mDataBinding.tvCredit.setText(mTotalAmount);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyCreditActivity.this);
                        mDataBinding.recyclerView.setLayoutManager(linearLayoutManager);
                        mDataBinding.recyclerView.addItemDecoration(new DividerItemDecoration(12));
                        mAdapter = new MyCreditadapter(MyCreditActivity.this);
                        mDataBinding.recyclerView.setAdapter(mAdapter);
                        ViewUtils.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }
    //endregion

    //region ADAPTER
    public class MyCreditadapter extends RecyclerView.Adapter<MyCreditadapter.ViewHolder> {

        Context context;

        MyCreditadapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_credit_query, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BindHandler handler = new BindHandler();
            holder.itemIntegralQueryBinding.setHandler(handler);

            holder.itemIntegralQueryBinding.setCreditInfo(mUserCreditList.get(position));
            if (Double.valueOf(mUserCreditList.get(position).getAmount()) < 0) {
//                holder.itemIntegralQueryBinding.tvTitle.setText(R.string.credits_deduction);
                holder.itemIntegralQueryBinding.tvIntegral.setTextColor(getResources().getColor(R.color.color_0ec100));
                holder.itemIntegralQueryBinding.tvIntegral.setText(mUserCreditList.get(position).getAmount());

            } else {
//                 String plus=("+ "+mUserCreditList.get(position).getAmount());
//                holder.itemIntegralQueryBinding.tvTitle.setText(R.string.credits_present);
                holder.itemIntegralQueryBinding.tvIntegral.setTextColor(getResources().getColor(R.color.colorRed));
                holder.itemIntegralQueryBinding.tvIntegral.setText("+" + mUserCreditList.get(position).getAmount());
            }
            holder.itemIntegralQueryBinding.tvTitle.setText(mUserCreditList.get(position).getDescription());
        }

        @Override
        public int getItemCount() {
            return mUserCreditList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ItemCreditQueryBinding itemIntegralQueryBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                itemIntegralQueryBinding = DataBindingUtil.bind(itemView);
            }
        }
    }
    //endregion
}
