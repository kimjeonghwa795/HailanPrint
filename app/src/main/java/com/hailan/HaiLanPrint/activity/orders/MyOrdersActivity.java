package com.hailan.HaiLanPrint.activity.orders;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityMyOrdersBinding;
import com.hailan.HaiLanPrint.databinding.ItemMyOrderBinding;
import com.hailan.HaiLanPrint.enumobject.OrderStatus;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.models.Order;
import com.hailan.HaiLanPrint.models.OrderInfo;
import com.hailan.HaiLanPrint.models.OrderWork;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.DateUtils;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.malinskiy.superrecyclerview.OnMoreListener;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hailan.HaiLanPrint.enumobject.OrderStatus.Paid;

/**
 * Created by yoghourt on 5/30/16.
 */

public class MyOrdersActivity extends ToolbarActivity<ActivityMyOrdersBinding> {

    private MyOrdersAdapter mAdapter;

    private ArrayList<OrderWork> mOrderWorkArrayList = new ArrayList<>();

    private HashMap<Integer, OrderInfo> mOrderInfoHashMap = new HashMap<>();

    private OrderStatus mOrderStatus = OrderStatus.All;

    private boolean mIsBackToMainActivity = false;

    private int mPageIndex;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_my_orders;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshOrderList();
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            mIsBackToMainActivity = getIntent().getBooleanExtra(Constants.KEY_FROM_PAYMENT_SUCCESS, false);
        }

        String[] titles = getResources().getStringArray(R.array.order_status_array);

        for (String title : titles) {
            mDataBinding.tabLayout.addTab(mDataBinding.tabLayout.newTab().setText(title));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataBinding.recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyOrdersAdapter();
        mDataBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBackToMainActivity) {
                    NavUtils.toMainActivity(MyOrdersActivity.this);
                } else {
                    finish();
                }
            }
        });

        mDataBinding.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int currentSelectedTabIndex = tab.getPosition();

                switch (currentSelectedTabIndex) {
                    case 0:
                        mOrderStatus = OrderStatus.All;
                        break;
                    case 1:
                        mOrderStatus = Paid;
                        break;
                    case 2:
                        mOrderStatus = OrderStatus.Delivered;
                        break;
                    case 3:
                        mOrderStatus = OrderStatus.Finished;
                        break;
                    case 4:
                        mOrderStatus = OrderStatus.AllRefund;
                        break;

                }

                refreshOrderList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        mDataBinding.recyclerView.setupMoreListener(new OnMoreListener() {
//            @Override
//            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
//                getUserOrderListRequest();
//            }
//        }, Constants.ITEM_LEFT_TO_LOAD_MORE);
    }

    private void setRecyclerViewLoadMoreListener() {
        mDataBinding.recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                getUserOrderListRequest();
            }
        }, Constants.ITEM_LEFT_TO_LOAD_MORE);
    }

    private void refreshOrderList() {
        setRecyclerViewLoadMoreListener();
        mDataBinding.recyclerView.setLoadingMore(true);
        mOrderInfoHashMap.clear();
        mOrderWorkArrayList.clear();
        mPageIndex = 0;
        getUserOrderListRequest();
    }

    //region SERVER
    private void getUserOrderListRequest() {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetUserOrderList(mPageIndex, mOrderStatus, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                mPageIndex++;

                if (StringUtil.isEmpty(response.body().getContent())) {
                    mDataBinding.recyclerView.setLoadingMore(false);
                    mDataBinding.recyclerView.setupMoreListener(null, 0);
                } else {
                    ArrayList<Order> ordersArrayList = response.body().getContent(new TypeToken<ArrayList<Order>>() {
                    });

                    for (Order order : ordersArrayList) {
                        mOrderInfoHashMap.put(order.getOrderInfo().getOrderID(), order.getOrderInfo());
                        mOrderWorkArrayList.addAll(order.getOrderWorkList());
                    }
                }
                if (mOrderWorkArrayList.size() > 0) {
                    mDataBinding.tvEmptyTips.setVisibility(View.GONE);
                    mDataBinding.recyclerView.setVisibility(View.VISIBLE);
                } else {
                    mDataBinding.tvEmptyTips.setVisibility(View.VISIBLE);
                    mDataBinding.recyclerView.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();

                mDataBinding.recyclerView.hideMoreProgress();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }

    // 确认收货
    private void updateOrderFinishedRequest(int orderID) {
        GlobalRestful.getInstance().UpdateOrderFinished(orderID, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                mOrderInfoHashMap.clear();
                mOrderWorkArrayList.clear();
                mPageIndex = 0;
                getUserOrderListRequest();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }
    //endregion

    //region ADAPTER
    public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order, parent, false);
            ViewHolder holder = new ViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyOrdersAdapter.ViewHolder holder, int position) {
            OrderWork orderWork = mOrderWorkArrayList.get(position);
            OrderInfo orderInfo = mOrderInfoHashMap.get(orderWork.getOrderID());

            holder.viewDataBinding.setHandlers(holder);
            holder.viewDataBinding.setOrderInfo(orderInfo);
            holder.viewDataBinding.setOrderWork(orderWork);
            holder.viewDataBinding.setShowHeader(isShowHeader(position));
            holder.viewDataBinding.setShowFooter(isShowFooter(position));
            GlideUtil.loadImageByPhotoSID(holder.viewDataBinding.ivImage, orderWork.getPhotoCover(), true);

            if ("".equals(orderWork.getWorkMaterial()) || "null".equals(orderWork.getWorkMaterial())) {
                holder.viewDataBinding.tvformat.setText(orderWork.getWorkFormat());
            } else {
                holder.viewDataBinding.tvformat.setText(orderWork.getWorkMaterial() + " " + orderWork.getWorkFormat());
            }

            if (orderWork.getTypeID() == ProductType.PrintPhoto.value()) {
                holder.viewDataBinding.tvNum.setText(getString(R.string.num_with_colon, orderWork.getPhotoCount()));
            } else {
                holder.viewDataBinding.tvNum.setText(getString(R.string.num_with_colon, orderWork.getOrderCount()));
            }

            OrderStatus orderStatus = OrderStatus.fromValue(orderInfo.getOrderStatus());

            holder.viewDataBinding.btnOperation.setVisibility(View.VISIBLE);
            holder.viewDataBinding.btnOperation.setBackgroundResource(R.drawable.shape_rb_normal);
            holder.viewDataBinding.btnOperation.setTextColor(getResources().getColor(R.color.color_333333));
            holder.viewDataBinding.tvOrderStatus.setText(OrderStatus.getOrderStatus(getApplicationContext(), orderStatus));
            switch (orderStatus) {
                case Paid:      // 已付款
                    if (DateUtils.isAfter12Hours(orderInfo.getCreatedTime())) {
                        holder.viewDataBinding.btnOperation.setVisibility(View.GONE);
                    } else {
                        holder.viewDataBinding.btnOperation.setText(R.string.apply_refund);
                    }
                    holder.viewDataBinding.btnOperation.setVisibility(View.GONE);
                    break;
                case Delivered: // 已发货
                    holder.viewDataBinding.btnOperation.setText(R.string.confirm_receive);
                    holder.viewDataBinding.btnOperation.setBackgroundResource(R.drawable.shape_rb_highlight);
                    holder.viewDataBinding.btnOperation.setTextColor(getResources().getColor(R.color.color_f88e11));
                    break;
                case Finished:  // 已完成
                    holder.viewDataBinding.btnOperation.setVisibility(View.GONE);
                    break;
                case Refunding: // 退款中
                    holder.viewDataBinding.btnOperation.setVisibility(View.GONE);
                    break;
                case Refunded:  // 已退款
                    holder.viewDataBinding.btnOperation.setVisibility(View.GONE);
                    break;
                case RefundFail: // 退款失败
                    holder.viewDataBinding.tvOrderStatus.setText(R.string.refundFail);
                    holder.viewDataBinding.btnOperation.setVisibility(View.GONE);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mOrderWorkArrayList.size();
        }

        private boolean isShowHeader(int position) {
            if (position == 0) {
                return true;
            } else {
                OrderWork currentOrderWork = mOrderWorkArrayList.get(position);
                OrderWork previousOrderWork = mOrderWorkArrayList.get(position - 1);

                if (currentOrderWork.getOrderID() != previousOrderWork.getOrderID()) {
                    return true;
                }
            }
            return false;
        }

        private boolean isShowFooter(int position) {
            if (position == getItemCount() - 1) {
                return true;
            } else {
                OrderWork currentOrderWork = mOrderWorkArrayList.get(position);
                OrderWork previousOrderWork = mOrderWorkArrayList.get(position + 1);

                if (currentOrderWork.getOrderID() != previousOrderWork.getOrderID()) {
                    return true;
                }
            }
            return false;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ItemMyOrderBinding viewDataBinding;

            public ViewHolder(View itemView) {
                super(itemView);
                viewDataBinding = DataBindingUtil.bind(itemView);
            }

            //region ACTION
            public void btnOperationAction(View view) {
                OrderWork orderWork = mOrderWorkArrayList.get(getLayoutPosition());
                OrderInfo orderInfo = mOrderInfoHashMap.get(orderWork.getOrderID());

                OrderStatus orderStatus = OrderStatus.fromValue(orderInfo.getOrderStatus());

                switch (orderStatus) {
                    case Paid:      // 已付款
                        Intent intent = new Intent(MyOrdersActivity.this, ApplyRefundActivity.class);
                        intent.putExtra(Constants.KEY_ORDER_INFO, orderInfo);
                        startActivity(intent);
                        break;
                    case Delivered: // 已发货
                        updateOrderFinishedRequest(orderInfo.getOrderID());
                        break;
                    case Finished:  // 已完成
                        break;
                    case Refunding: // 退款中
                        break;
                    case Refunded:  // 已退款
                        break;
                    case RefundFail: // 退款失败
                        break;
                }
            }

            public void itemClickToOrderDetailAction(View view) {
                OrderWork selectedOrderWork = mOrderWorkArrayList.get(getLayoutPosition());
                OrderInfo selectedOrderInfo = mOrderInfoHashMap.get(selectedOrderWork.getOrderID());

                ArrayList<OrderWork> passOrderWorkArrayList = new ArrayList<>();
                for (OrderWork orderWork : mOrderWorkArrayList) {
                    if (orderWork.getOrderID() == selectedOrderWork.getOrderID()) {
                        passOrderWorkArrayList.add(orderWork);
                    }
                }

                Intent intent = new Intent(MyOrdersActivity.this, OrderDetailActivity.class);
                intent.putExtra(Constants.KEY_ORDER_INFO, selectedOrderInfo);
                intent.putExtra(Constants.KEY_ORDER_WORK_LIST, passOrderWorkArrayList);
                startActivity(intent);
            }
            //endregion
        }
    }
    //endregion
}
