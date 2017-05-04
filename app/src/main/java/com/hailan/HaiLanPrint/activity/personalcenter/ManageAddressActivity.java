package com.hailan.HaiLanPrint.activity.personalcenter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ActivityManageAddressBinding;
import com.hailan.HaiLanPrint.databinding.ItemManageAddressBinding;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.greendao.Location;
import com.hailan.HaiLanPrint.models.DeliveryAddress;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.ViewUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by PC on 2016-06-14.
 */
public class ManageAddressActivity extends ToolbarActivity<ActivityManageAddressBinding> {

    public static final String FROM_HERE = "FROM_HERE";
    public static final String ADDRESS = "ADDRESS";
    public static final int FOR_UPATE = 1;
    public static final int FOR_SAVE = 2;
    private ArrayList<DeliveryAddress> mUserAddressList = new ArrayList<>();
    private DeliveryAddress mUserAddress;
    private ManageAddressAdatper mAdapter;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_manage_address;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserAddressListRequest();
    }

    @Override
    protected void initData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataBinding.recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new ManageAddressAdatper();
        mDataBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        mDataBinding.tvAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageAddressActivity.this, EditAddressActivity.class);
                intent.putExtra(FROM_HERE, FOR_SAVE);
                startActivity(intent);
            }
        });
    }

    //region ACTION
    //编辑地址
    public void updateAddress(DeliveryAddress address) {
        Intent intent = new Intent(this, EditAddressActivity.class);
        intent.putExtra(FROM_HERE, FOR_UPATE);
        intent.putExtra(ADDRESS, address);
        startActivity(intent);
    }


    //Item编辑的方法
    public void onEidtorItem(View view) {
        int position = mDataBinding.recyclerView.getChildAdapterPosition(view);
        DeliveryAddress deliveryAddress = mUserAddressList.get(position);
        // KLog.e("第几个" + position);
        updateAddress(deliveryAddress);
    }

    //删除事件
    public void onDeleteItem(View view) {

        int position = mDataBinding.recyclerView.getChildAdapterPosition(view);
        DeliveryAddress address = mUserAddressList.get(position);
        int AutoID = address.getAutoID();
        deleteAddressRequest(AutoID, position, view);
    }

    //endregion

    //region SERVER
    private void getUserAddressListRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetUserAddressList(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                mUserAddressList.clear();
                ArrayList<DeliveryAddress> tempList = response.body().getContent(new com.google.gson.reflect.TypeToken<ArrayList<DeliveryAddress>>() {
                });
                mUserAddressList.addAll(tempList);
                mAdapter.notifyDataSetChanged();
                ViewUtils.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }

    //删除地址
    public void deleteAddressRequest(int AutoID, final int postion, final View view) {
        GlobalRestful.getInstance().DeleteUserAddress(AutoID, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (ResponseCode.isSuccess(response.body())) {
                    getUserAddressListRequest();
                }
                ;
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }
    //endregion

    //region ADAPTER
    public class ManageAddressAdatper extends RecyclerView.Adapter<ManageAddressAdatper.MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ManageAddressActivity.this).inflate(R.layout.item_manage_address, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            DeliveryAddress address = mUserAddressList.get(position);
            Location province = MDGroundApplication.sDaoSession.getLocationDao().load(address.getProvinceID());
            Location city = MDGroundApplication.sDaoSession.getLocationDao().load(address.getCityID());
            Location county = MDGroundApplication.sDaoSession.getLocationDao().load(address.getCountryID());
            holder.viewItemBinding.tvAddress.setText(province.getLocationName() + " " + city.getLocationName() + " " + county.getLocationName() + " " + address.getStreet());
            holder.viewItemBinding.tvName.setText(address.getReceiver());
            holder.viewItemBinding.tvNume.setText(address.getPhone());
        }

        @Override
        public int getItemCount() {
            return mUserAddressList.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            
            public ItemManageAddressBinding viewItemBinding;

            public MyViewHolder(final View itemView) {
                super(itemView);
                viewItemBinding = DataBindingUtil.bind(itemView);
                viewItemBinding.tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDeleteItem(itemView);
                    }
                });
                viewItemBinding.tvEditor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onEidtorItem(itemView);
                    }
                });
            }
        }
    }
    //endregion
}
