/*
    Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.huawei.industrydemo.shopping.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.entity.Order;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.ShoppingCart;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.inteface.MemberCheckCallback;
import com.huawei.industrydemo.shopping.inteface.OnItemModifyListener;
import com.huawei.industrydemo.shopping.page.LogInActivity;
import com.huawei.industrydemo.shopping.page.OrderSubmitActivity;
import com.huawei.industrydemo.shopping.utils.MemberUtil;
import com.huawei.industrydemo.shopping.utils.SharedPreferencesUtil;
import com.huawei.industrydemo.shopping.viewadapter.ShoppingCartAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.huawei.hms.analytics.type.HAEventType.DELPRODUCTFROMCART;
import static com.huawei.hms.analytics.type.HAParamType.CATEGORY;
import static com.huawei.hms.analytics.type.HAParamType.CURRNAME;
import static com.huawei.hms.analytics.type.HAParamType.PRICE;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTID;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTNAME;
import static com.huawei.hms.analytics.type.HAParamType.QUANTITY;
import static com.huawei.hms.analytics.type.HAParamType.REVENUE;


/**
 * ShopCar page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @see com.huawei.industrydemo.shopping.MainActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class ShopCarFragment extends BaseFragment implements View.OnClickListener, OnItemModifyListener {

    private static final String TAG = ShopCarFragment.class.getSimpleName();
    private TextView textFinish;
    private TextView textActualPrice;
    private TextView textPay;
    private TextView textDelete;
    private CheckBox checkAllSelect;
    private RecyclerView recyclerShoppingCartList;
    private ShoppingCartAdapter shoppingCartAdapter;
    private TextView textEdit;
    private LinearLayout layoutLoginFirst;
    private RelativeLayout layoutBottom;
    private List<ShoppingCart> shoppingCartList = new ArrayList<>();
    private int totalPrice = 0;
    private int totalQuantity = 0;
    private int actualPrice = 0;

    public ShopCarFragment() {
        setKits(new String[]{});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_car, container, false);
        initView(view);
        initData();
        addTipView();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
            checkAllSelect.setChecked(isAllChoosed());
            changeToFinishStatus();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        checkAllSelect.setChecked(isAllChoosed());
        changeToFinishStatus();
    }

    private void initData() {
        User user = SharedPreferencesUtil.getInstance().getUser();
        if (null == user) {
            shoppingCartList = new ArrayList<>();
            textEdit.setVisibility(View.GONE);
            layoutLoginFirst.setVisibility(View.VISIBLE);
            recyclerShoppingCartList.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
        } else {
            shoppingCartList = user.getShoppingCartList();
            textEdit.setVisibility(View.VISIBLE);
            layoutLoginFirst.setVisibility(View.GONE);
            recyclerShoppingCartList.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
        }
        Collections.sort(shoppingCartList,
                (shoppingCart1, shoppingCart2) -> shoppingCart2.getNumber() - shoppingCart1.getNumber());

        shoppingCartAdapter.setShoppingCartList(shoppingCartList);
        statistics();
    }

    private void initView(View view) {
        recyclerShoppingCartList = view.findViewById(R.id.list_shopping_cart);
        recyclerShoppingCartList.setLayoutManager(new LinearLayoutManager(getContext()));

        textEdit = view.findViewById(R.id.textView_edit);
        textEdit.setOnClickListener(this);
        textFinish = view.findViewById(R.id.textView_finish);
        textFinish.setOnClickListener(this);
        layoutLoginFirst = view.findViewById(R.id.layout_login_first);
        layoutLoginFirst.setOnClickListener(this);
        layoutBottom = view.findViewById(R.id.rl_bottom);
        checkAllSelect = view.findViewById(R.id.checkBox_all_select);
        checkAllSelect.setChecked(isAllChoosed());
        checkAllSelect.setOnClickListener(this);
        textActualPrice = view.findViewById(R.id.textView_actual_price);
        textPay = view.findViewById(R.id.textView_pay);
        textPay.setOnClickListener(this);
        textDelete = view.findViewById(R.id.textView_delete);
        textDelete.setOnClickListener(this);

        shoppingCartAdapter = new ShoppingCartAdapter(getContext());
        shoppingCartAdapter.setOnItemModifyListener(this);
        recyclerShoppingCartList.setAdapter(shoppingCartAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_edit:
                textEdit.setVisibility(View.GONE);
                textFinish.setVisibility(View.VISIBLE);
                textActualPrice.setVisibility(View.GONE);
                textPay.setVisibility(View.GONE);
                textDelete.setVisibility(View.VISIBLE);
                break;
            case R.id.textView_finish:
                changeToFinishStatus();
                break;
            case R.id.layout_login_first:
                startActivityForResult(new Intent(getActivity(), LogInActivity.class), Constants.LOGIN_REQUEST_CODE);
                break;
            case R.id.checkBox_all_select:
                if (shoppingCartList.size() != 0) {
                    if (checkAllSelect.isChecked()) {
                        for (ShoppingCart shoppingCart : shoppingCartList) {
                            shoppingCart.setChoosed(true);
                        }
                    } else {
                        for (ShoppingCart shoppingCart : shoppingCartList) {
                            shoppingCart.setChoosed(false);
                        }
                    }
                    modifyData(shoppingCartList);
                }
                break;
            case R.id.textView_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
                    Iterator<ShoppingCart> iterator = shoppingCartList.iterator();
                    HiAnalyticsInstance instance = HiAnalytics.getInstance(getActivity());
                    Bundle bundle = new Bundle();

                    while (iterator.hasNext()) {
                        ShoppingCart shoppingCart = iterator.next();
                        if (shoppingCart.isChoosed()) {
                            // Initiate Parameters
                            bundle.putLong(QUANTITY, shoppingCart.getQuantity());
                            bundle.putString(CATEGORY, shoppingCart.getProduct().getCategory().trim());
                            bundle.putString(PRODUCTNAME, shoppingCart.getProduct().getBasicInfo().getShortName().trim());
                            bundle.putString(PRODUCTID, Integer.toString(shoppingCart.getProduct().getNumber()));
                            bundle.putDouble(PRICE, shoppingCart.getProduct().getBasicInfo().getPrice());
                            bundle.putDouble(REVENUE, (shoppingCart.getProduct().getBasicInfo().getPrice()*shoppingCart.getQuantity()));
                            bundle.putString(CURRNAME, "CNY");

                            // Report a customzied Event
                            instance.onEvent(DELPRODUCTFROMCART, bundle);

                            iterator.remove();
                        }
                    }
                    modifyData(shoppingCartList);
                }).setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
                builder.setMessage(R.string.shopping_cart_confirm_delete);
                builder.show();
                break;
            case R.id.textView_pay:
                if (0 == totalQuantity) {
                    Toast.makeText(getContext(), R.string.shopping_cart_reduce_notice, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), OrderSubmitActivity.class);
                    Order order = generateOrder();
                    intent.putExtra(KeyConstants.ORDER_KEY, new Gson().toJson(order));
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    private Order generateOrder() {
        Order order = new Order();
        List<OrderItem> orderItemList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            if (shoppingCart.isChoosed()) {
                OrderItem orderItem = new OrderItem(shoppingCart.getProduct(), shoppingCart.getQuantity());
                orderItemList.add(orderItem);
            }
        }
        order.setOrderItemList(orderItemList);
        order.setTotalPrice(totalPrice);
        order.setActualPrice(totalPrice);
        order.setStatus(Constants.NOT_PAID);
        return order;
    }

    private boolean isAllChoosed() {
        if (shoppingCartList.isEmpty()) {
            return false;
        }
        for (ShoppingCart shoppingCart : shoppingCartList) {
            if (!shoppingCart.isChoosed()) {
                return false;
            }
        }
        return true;
    }

    public void statistics() {
        totalPrice = 0;
        totalQuantity = 0;
        actualPrice = 0;
        float discount = MemberUtil.getInstance().isMember(SharedPreferencesUtil.getInstance().getUser()) ? Constants.DISCOUNTED : 1f;
        if (null != shoppingCartList) {
            for (int i = 0; i < shoppingCartList.size(); i++) {
                ShoppingCart shoppingCart = shoppingCartList.get(i);
                if (shoppingCart.isChoosed()) {
                    totalQuantity += shoppingCart.getQuantity();
                    int tempPrice = shoppingCart.getProduct().getBasicInfo().getPrice() * shoppingCart.getQuantity();
                    totalPrice += tempPrice;
                    actualPrice += tempPrice * discount;
                }
            }
        }
        if (0 == totalQuantity) {
            checkAllSelect.setChecked(false);
        }
        textActualPrice.setText(getString(R.string.shopping_cart_total, totalPrice));
        textPay.setText(getString(R.string.shopping_cart_pay, totalQuantity));
    }

    @Override
    public void onItemChoose(int position, boolean isChecked) {
        shoppingCartList.get(position).setChoosed(isChecked);
        checkAllSelect.setChecked(isAllChoosed());
        modifyData(shoppingCartList);
    }

    @Override
    public void onItemQuantityAdd(int position, View quantityView) {
        ShoppingCart shoppingCart = shoppingCartList.get(position);
        int currentQuantity = shoppingCart.getQuantity();
        currentQuantity++;
        shoppingCart.setQuantity(currentQuantity);
        ((TextView) quantityView).setText(String.valueOf(currentQuantity));
        modifyData(shoppingCartList);
    }

    @Override
    public void onItemQuantityReduce(int position, View quantityView) {
        ShoppingCart shoppingCart = shoppingCartList.get(position);
        int currentQuantity = shoppingCart.getQuantity();
        if (currentQuantity == 1) {
            Toast.makeText(getContext(), R.string.shopping_cart_reduce_notice, Toast.LENGTH_SHORT).show();
            return;
        }
        currentQuantity--;
        shoppingCart.setQuantity(currentQuantity);
        ((TextView) quantityView).setText(String.valueOf(currentQuantity));
        modifyData(shoppingCartList);
    }

    private void modifyData(List<ShoppingCart> shoppingCartList) {
        User user = SharedPreferencesUtil.getInstance().getUser();
        user.setShoppingCartList(shoppingCartList);
        SharedPreferencesUtil.getInstance().setUser(user);
        shoppingCartAdapter.notifyDataSetChanged();
        statistics();
    }

    private void changeToFinishStatus() {
        statistics();
        textFinish.setVisibility(View.GONE);
        textEdit.setVisibility(View.VISIBLE);
        textDelete.setVisibility(View.GONE);
        textPay.setVisibility(View.VISIBLE);
        textActualPrice.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constants.LOGIN_REQUEST_CODE == requestCode) {
            MemberUtil.getInstance().isMember(getActivity(), new MemberCheckCallback() {
                @Override
                public void onResult(boolean isMember, boolean isAutoRenewing, String productName, String time) {
                    initData();
                }
            });
        }
    }
}
