/*
    Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

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

package com.huawei.industrydemo.shopping.page.viewmodel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.base.BaseDialog;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.entity.Bag;
import com.huawei.industrydemo.shopping.entity.Order;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.inteface.MemberCheckCallback;
import com.huawei.industrydemo.shopping.page.BagActivity;
import com.huawei.industrydemo.shopping.page.OrderSubmitActivity;
import com.huawei.industrydemo.shopping.repository.BagRepository;
import com.huawei.industrydemo.shopping.repository.OrderRepository;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;
import com.huawei.industrydemo.shopping.utils.MemberUtil;
import com.huawei.industrydemo.shopping.viewadapter.BagAdapter;

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
import static com.huawei.industrydemo.shopping.base.BaseDialog.CANCEL_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.industrydemo.shopping.base.BaseDialog.CONTENT;
import static com.huawei.industrydemo.shopping.constants.Constants.CNY;
import static com.huawei.industrydemo.shopping.constants.Constants.LOGIN_REQUEST_CODE;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.BAG_KEY;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.ORDER_KEY;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/23]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class BagActivityViewModel extends BaseActivityViewModel<BagActivity> {
    private static final String TAG = BagActivityViewModel.class.getSimpleName();

    private ImageView imageFinish;

    // True means Text is Edit, False means Text is finish
    private boolean editFinish;

    private TextView textCurrency;

    private TextView textActualPrice;

    private TextView textShipCost;

    private TextView textPay;

    private TextView textDelete;

    private CheckBox checkAllSelect;

    private RecyclerView recyclerBagList;

    private BagAdapter bagAdapter;

    private TextView textEdit;

    private LinearLayout layoutLoginFirst;

    private RelativeLayout layoutBottom;

    private List<Bag> bagList = new ArrayList<>();

    private ArrayList<Integer> checkedBagNumList;

    private int totalPrice = 0;

    private int totalQuantity = 0;

    private int actualPrice = 0;

    private User mUser;

    private final ProductRepository mProductRepository;

    private final OrderRepository mOrderRepository;

    private final BagRepository mBagRepository;

    private final UserRepository mUserRepository;

    /**
     * constructor
     *
     * @param bagActivity Activity object
     */
    public BagActivityViewModel(BagActivity bagActivity) {
        super(bagActivity);
        this.mUserRepository = new UserRepository();
        this.mUser = mUserRepository.getCurrentUser();
        this.mProductRepository = new ProductRepository();
        this.mOrderRepository = new OrderRepository();
        this.mBagRepository = new BagRepository();
    }

    @Override
    public void initView() {
        TextView title = mActivity.findViewById(R.id.tv_title);
        title.setText(R.string.shopping_bag_title);

        recyclerBagList = mActivity.findViewById(R.id.list_shopping_cart);
        recyclerBagList.setLayoutManager(new LinearLayoutManager(mActivity));

        textEdit = mActivity.findViewById(R.id.textView_edit);
        textEdit.setOnClickListener(mActivity);
        textEdit.setVisibility(View.VISIBLE);
        editFinish = true;
        imageFinish = mActivity.findViewById(R.id.iv_back);
        imageFinish.setOnClickListener(mActivity);
        layoutLoginFirst = mActivity.findViewById(R.id.layout_login_first);
        layoutLoginFirst.setOnClickListener(mActivity);
        layoutBottom = mActivity.findViewById(R.id.rl_bottom);
        checkAllSelect = mActivity.findViewById(R.id.checkBox_all_select);
        checkAllSelect.setOnClickListener(mActivity);
        textActualPrice = mActivity.findViewById(R.id.textView_actual_price);
        textCurrency = mActivity.findViewById(R.id.textView_currency);
        // Set partial string color start
        String currency = mActivity.getString(R.string.shopping_cart_total);
        SpannableStringBuilder builder = new SpannableStringBuilder(currency);
        builder.setSpan(new ForegroundColorSpan(Color.RED), currency.length() - 1, currency.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textCurrency.setText(builder);
        // Set partial string color end
        textShipCost = mActivity.findViewById(R.id.textView_ship_cost);
        textPay = mActivity.findViewById(R.id.textView_pay);
        textPay.setOnClickListener(mActivity);
        textDelete = mActivity.findViewById(R.id.textView_delete);
        textDelete.setOnClickListener(mActivity);

        bagAdapter = new BagAdapter(mActivity);
        bagAdapter.setOnItemModifyListener(mActivity);
        recyclerBagList.setAdapter(bagAdapter);
    }

    public void initData() {
        if (null == mUser) {
            bagList = new ArrayList<>();
            textEdit.setVisibility(View.GONE);
            layoutLoginFirst.setVisibility(View.VISIBLE);
            recyclerBagList.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
        } else {
            bagList = mBagRepository.queryByUser(mUser);
            textEdit.setVisibility(View.VISIBLE);
            layoutLoginFirst.setVisibility(View.GONE);
            recyclerBagList.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
        }
        Collections.sort(bagList,
            (shoppingCart1, shoppingCart2) -> shoppingCart2.getNumber() - shoppingCart1.getNumber());

        bagAdapter.setBagList(bagList);
        checkAllSelect.setChecked(isAllChoosed());
        statistics();
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.textView_edit:
                if (editFinish) {
                    changeToEditStatus();
                } else {
                    changeToFinishStatus();
                }
                break;
            case R.id.iv_back:
                mActivity.finish();
                break;
            case R.id.layout_login_first:
                mActivity.signIn();
                break;
            case R.id.checkBox_all_select:
                if (bagList.size() != 0) {
                    if (checkAllSelect.isChecked()) {
                        for (Bag shoppingCart : bagList) {
                            shoppingCart.setChoosed(true);
                        }
                    } else {
                        for (Bag shoppingCart : bagList) {
                            shoppingCart.setChoosed(false);
                        }
                    }
                    modifyData(bagList);
                }
                break;
            case R.id.textView_delete:
                Bundle data = new Bundle();
                data.putString(CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
                data.putString(CONTENT, mActivity.getString(R.string.shopping_cart_confirm_delete));
                data.putString(CANCEL_BUTTON, mActivity.getString(R.string.cancel));

                BaseDialog dialog = new BaseDialog(mActivity, data, true);
                dialog.setConfirmListener(v -> {
                    Iterator<Bag> iterator = bagList.iterator();
                    Bundle bundle = new Bundle();

                    while (iterator.hasNext()) {
                        Bag bag = iterator.next();
                        if (bag.isChoosed()) {
                            Product product = mProductRepository.queryByBag(bag);
                            // Initiate Parameters
                            bundle.putLong(QUANTITY, bag.getQuantity());
                            bundle.putString(CATEGORY, product.getCategory().trim());
                            bundle.putString(PRODUCTNAME, product.getBasicInfo().getShortName().trim());
                            bundle.putString(PRODUCTID, Integer.toString(product.getNumber()));
                            bundle.putDouble(PRICE, product.getBasicInfo().getPrice());
                            bundle.putDouble(REVENUE, (product.getBasicInfo().getPrice() * bag.getQuantity()));
                            bundle.putString(CURRNAME, CNY);

                            // Report a customzied Event
                            AnalyticsUtil.getInstance(mActivity).onEvent(DELPRODUCTFROMCART, bundle);
                            iterator.remove();
                        }
                    }
                    modifyData(bagList);
                    dialog.dismiss();
                });
                dialog.setCancelListener(v -> {
                    dialog.dismiss();
                });
                dialog.show();
                break;
            case R.id.textView_pay:
                if (0 == totalQuantity) {
                    Toast.makeText(mActivity, R.string.shopping_cart_reduce_notice, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(mActivity, OrderSubmitActivity.class);
                    Order order = generateOrder();
                    intent.putExtra(ORDER_KEY, order.getNumber());
                    intent.putExtra(KeyConstants.MODIFY_FLAG, true);
                    intent.putIntegerArrayListExtra(BAG_KEY, checkedBagNumList);
                    mActivity.startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    private Order generateOrder() {
        Order order = new Order();
        List<OrderItem> orderItemList = new ArrayList<>();
        checkedBagNumList = new ArrayList<>();
        for (Bag bag : bagList) {
            if (bag.isChoosed()) {
                OrderItem orderItem = new OrderItem(bag.getProductNum(), bag.getQuantity());
                orderItemList.add(orderItem);
                checkedBagNumList.add(bag.getNumber());
            }
        }
        order.setTotalPrice(totalPrice);
        order.setActualPrice(totalPrice);
        order.setStatus(Constants.NOT_PAID);
        mOrderRepository.insert(order, orderItemList, mUser);
        return order;
    }

    private boolean isAllChoosed() {
        if (bagList.isEmpty()) {
            return false;
        }
        for (Bag shoppingCart : bagList) {
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
        float discount = MemberUtil.getInstance().isMember(mUser) ? Constants.DISCOUNTED : 1f;
        if (null != bagList) {
            for (int i = 0; i < bagList.size(); i++) {
                Bag bag = bagList.get(i);
                if (bag.isChoosed()) {
                    totalQuantity += bag.getQuantity();
                    Product product = mProductRepository.queryByBag(bag);
                    int tempPrice = product.getBasicInfo().getPrice() * bag.getQuantity();
                    totalPrice += tempPrice;
                    actualPrice += tempPrice * discount;
                }
            }
        }
        if (0 == totalQuantity) {
            checkAllSelect.setChecked(false);
        }
        textActualPrice.setText(String.valueOf(totalPrice));
        textPay.setText(mActivity.getString(R.string.shopping_cart_pay, totalQuantity));
    }

    public void onItemChoose(int position, boolean isChecked) {
        bagList.get(position).setChoosed(isChecked);
        checkAllSelect.setChecked(isAllChoosed());
        modifyData(bagList);
    }

    public void onItemQuantityAdd(int position, View quantityView) {
        Bag shoppingCart = bagList.get(position);
        int currentQuantity = shoppingCart.getQuantity();
        currentQuantity++;
        shoppingCart.setQuantity(currentQuantity);
        ((TextView) quantityView).setText(String.valueOf(currentQuantity));
        modifyData(bagList);
    }

    public void onItemQuantityReduce(int position, View quantityView) {
        Bag shoppingCart = bagList.get(position);
        int currentQuantity = shoppingCart.getQuantity();
        if (currentQuantity == 1) {
            Toast.makeText(mActivity, R.string.shopping_cart_reduce_notice, Toast.LENGTH_SHORT).show();
            return;
        }
        currentQuantity--;
        shoppingCart.setQuantity(currentQuantity);
        ((TextView) quantityView).setText(String.valueOf(currentQuantity));
        modifyData(bagList);
    }

    private void modifyData(List<Bag> bagList) {
        this.bagList = bagList;
        bagAdapter.setBagList(bagList);
        bagAdapter.notifyDataSetChanged();
        statistics();
        mBagRepository.insertAll(bagList, mUser);
    }

    private void changeToFinishStatus() {
        statistics();
        textEdit.setText(R.string.shopping_cart_edit);
        textDelete.setVisibility(View.GONE);
        textPay.setVisibility(View.VISIBLE);
        textCurrency.setVisibility(View.VISIBLE);
        textActualPrice.setVisibility(View.VISIBLE);
        textShipCost.setVisibility(View.VISIBLE);
        editFinish = true;
    }

    private void changeToEditStatus() {
        textEdit.setText(R.string.shopping_cart_finish);
        textCurrency.setVisibility(View.GONE);
        textActualPrice.setVisibility(View.GONE);
        textShipCost.setVisibility(View.GONE);
        textPay.setVisibility(View.GONE);
        textDelete.setVisibility(View.VISIBLE);
        editFinish = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (LOGIN_REQUEST_CODE == requestCode) {
            mUser = mUserRepository.getCurrentUser();
            if (mUser != null) {
                MemberUtil.getInstance()
                    .isMember(mActivity, mUser,
                        (MemberCheckCallback) (isMember, isAutoRenewing, productName, time) -> initData());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {

    }
}
