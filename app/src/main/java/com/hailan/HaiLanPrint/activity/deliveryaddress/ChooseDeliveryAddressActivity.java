package com.hailan.HaiLanPrint.activity.deliveryaddress;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityChooseDeliveryAddressBinding;
import com.hailan.HaiLanPrint.databinding.ItemDeliveryAddressBinding;
import com.hailan.HaiLanPrint.models.DeliveryAddress;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.StringUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/24/16.
 */

public class ChooseDeliveryAddressActivity extends ToolbarActivity<ActivityChooseDeliveryAddressBinding> {

    private DeliveryAddressAdapter mAdapter;

    private ArrayList<DeliveryAddress> mAddressArrayList = new ArrayList<>();

    private DeliveryAddress mDeliveryAddress;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_choose_delivery_address;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserAddressListRequest();
    }

    @Override
    protected void initData() {
        mDeliveryAddress = getIntent().getParcelableExtra(Constants.KEY_DELIVERY_ADDRESS);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataBinding.recyclerView.setLayoutManager(layoutManager);
//        mDataBinding.recyclerView.addItemDecoration(new DividerItemDecoration(0));
        mAdapter = new DeliveryAddressAdapter();
        mDataBinding.recyclerView.setAdapter(mAdapter);
        getUserAddressListRequest();
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    //region ACTION
    public void toAddDeliveryAddressActivityAction(View view) {
        if (Constants.MAX_DELIVERY_ADDRESS - mAddressArrayList.size() > 0) {
            Intent intent = new Intent(this, EditDeliveryAddressActivity.class);
            startActivityForResult(intent, 0);
        }
    }
    //endregion

    //region SERVER
    private void getUserAddressListRequest() {
        GlobalRestful.getInstance().GetUserAddressList(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                mAddressArrayList = response.body().getContent(new TypeToken<ArrayList<DeliveryAddress>>() {
                });

                mDataBinding.tvCanAddNum.setText(getString(R.string.still_can_add_address, Constants.MAX_DELIVERY_ADDRESS - mAddressArrayList.size()));

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }
    //endregion

    //region ADAPTER
    public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.ViewHolder> {

        @Override
        public DeliveryAddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_delivery_address, parent, false);
            DeliveryAddressAdapter.ViewHolder holder = new DeliveryAddressAdapter.ViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(DeliveryAddressAdapter.ViewHolder holder, int position) {
            DeliveryAddress deliveryAddress = mAddressArrayList.get(position);

            holder.viewDataBinding.setDeliveryAddress(deliveryAddress);
            holder.viewDataBinding.setHandlers(holder);

            holder.viewDataBinding.tvAddress.setText(StringUtil.getCompleteAddress(deliveryAddress));

            if (mDeliveryAddress != null && mDeliveryAddress.getAutoID() == deliveryAddress.getAutoID()) {
                holder.viewDataBinding.ivLocationLogo.setVisibility(View.VISIBLE);
                holder.viewDataBinding.ivLine.setVisibility(View.VISIBLE);
                holder.viewDataBinding.viewDivider.setVisibility(View.GONE);
            } else {
                holder.viewDataBinding.ivLocationLogo.setVisibility(View.INVISIBLE);
                holder.viewDataBinding.ivLine.setVisibility(View.GONE);
                holder.viewDataBinding.viewDivider.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mAddressArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ItemDeliveryAddressBinding viewDataBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }

            public void toSelectDeliveryAddressAction(View view) {
                DeliveryAddress deliveryAddress = mAddressArrayList.get(getAdapterPosition());

                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_DELIVERY_ADDRESS, deliveryAddress);
                setResult(RESULT_OK, intent);
                finish();
            }

        }
    }
    //endregion

}
