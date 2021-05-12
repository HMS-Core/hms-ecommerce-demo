/*
 *     Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.industrydemo.shopping.page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.fragment.ordercenter.AllOrderFragment;
import com.huawei.industrydemo.shopping.fragment.ordercenter.CompleteOrderFragment;
import com.huawei.industrydemo.shopping.fragment.ordercenter.PendingPaymentFragment;
import com.huawei.industrydemo.shopping.fragment.ordercenter.ExpressingFragment;
import com.huawei.industrydemo.shopping.inteface.OnOrderListRefresh;
import com.huawei.industrydemo.shopping.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static com.huawei.industrydemo.shopping.constants.Constants.ALL_ORDER_INDEX;
import static com.huawei.industrydemo.shopping.constants.Constants.COMPLETED_ORDER_INDEX;
import static com.huawei.industrydemo.shopping.constants.Constants.PENDING_PAYMENT_INDEX;
import static com.huawei.industrydemo.shopping.constants.Constants.EXPRESSING_INDEX;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.PAGE_INDEX;

/**
 * OrderCenter Activity
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/25]
 * @see com.huawei.industrydemo.shopping.MainActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class OrderCenterActivity extends BaseActivity implements View.OnClickListener {
    /**
     * PaymentSelectActivity request code
     */
    public static final int REQUEST_PAYMENT_SELECT = 6666;

    private ViewPager mViewPager;

    private FragmentPagerAdapter mAdapter;

    private List<Fragment> mFragments;

    private TextView textAllOrder;

    private View viewAllOrder;

    private TextView textPendingPayment;

    private View viewPendingPayment;

    private TextView textExpressing;

    private View viewExpressing;

    private TextView textCompleteOrder;

    private View viewCompleteOrder;

    private int startPageFromPush = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int startPage = getIntent().getIntExtra(PAGE_INDEX, ALL_ORDER_INDEX);
        setContentView(R.layout.activity_order_center);
        addTipView(new String[] {PUSH_NOTIFY, NETWORK_CONNECT});
        getNotificationData();
        initViews();
        initEvents();
        initData(startPage);
    }

    private void initData(int startPage) {
        User user = new UserRepository().getCurrentUser();
        mFragments = new ArrayList<>();
        mFragments.add(new AllOrderFragment(this, user));
        mFragments.add(new PendingPaymentFragment(this, user));
        mFragments.add(new ExpressingFragment(this, user));
        mFragments.add(new CompleteOrderFragment(this, user));

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {// 从集合中获取对应位置的Fragment
                return mFragments.get(position);
            }

            @Override
            public int getCount() {// 获取集合中Fragment的总数
                return mFragments.size();
            }

        };
        mViewPager.setAdapter(mAdapter);
        if (startPageFromPush == -1) {
            selectTab(startPage);
        } else {
            selectTab(startPageFromPush);
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position);
                resetColor();
                selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * Update the data list of each fragment when the order status changes.
     */
    public void onDataRefresh() {
        for (Fragment fragment : mFragments) {
            ((OnOrderListRefresh) fragment).onDataRefresh();
        }
    }

    private void initEvents() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        textAllOrder.setOnClickListener(this);
        textPendingPayment.setOnClickListener(this);
        textExpressing.setOnClickListener(this);
        textCompleteOrder.setOnClickListener(this);
    }

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager_order_center);

        ImageView shoppingCartIcon = findViewById(R.id.iv_icon);
        shoppingCartIcon.setVisibility(View.VISIBLE);
        shoppingCartIcon.setOnClickListener(this);
        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.order_center_title);
        textAllOrder = findViewById(R.id.tab_all_order);
        viewAllOrder = findViewById(R.id.view_all_order);
        textPendingPayment = findViewById(R.id.tab_pending_payment);
        viewPendingPayment = findViewById(R.id.view_pending_payment);
        textExpressing = findViewById(R.id.tab_address_img);
        viewExpressing = findViewById(R.id.view_to_be_received);
        textCompleteOrder = findViewById(R.id.tab_complete_order);
        viewCompleteOrder = findViewById(R.id.view_complete_order);
    }

    private void getNotificationData() {
        Intent intent = getIntent();
        if (null != intent) {
            int status = intent.getIntExtra("status", -1);
            Log.d(TAG, "order status:" + status);
            if (Constants.NOT_PAID == status) {
                startPageFromPush = PENDING_PAYMENT_INDEX;
            } else if (Constants.HAVE_PAID == status) {
                startPageFromPush = EXPRESSING_INDEX;
            } else if (Constants.COMPLETED == status) {
                startPageFromPush = COMPLETED_ORDER_INDEX;
            }

        }
    }

    @Override
    public void onClick(View v) {
        resetColor();
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_icon:
                Intent intentIcon = new Intent(this, BagActivity.class);
                startActivity(intentIcon);
                break;
            case R.id.tab_all_order:
                selectTab(ALL_ORDER_INDEX);
                break;
            case R.id.tab_pending_payment:
                selectTab(PENDING_PAYMENT_INDEX);
                break;
            case R.id.tab_address_img:
                selectTab(EXPRESSING_INDEX);
                break;
            case R.id.tab_complete_order:
                selectTab(COMPLETED_ORDER_INDEX);
                break;
            default:
                break;
        }
    }

    private void selectTab(int i) {
        switch (i) {
            case ALL_ORDER_INDEX:
                textAllOrder.setTextColor(getResources().getColor(R.color.red));
                viewAllOrder.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            case PENDING_PAYMENT_INDEX:
                textPendingPayment.setTextColor(getResources().getColor(R.color.red));
                viewPendingPayment.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            case EXPRESSING_INDEX:
                textExpressing.setTextColor(getResources().getColor(R.color.red));
                viewExpressing.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            case COMPLETED_ORDER_INDEX:
                textCompleteOrder.setTextColor(getResources().getColor(R.color.red));
                viewCompleteOrder.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            default:
                break;
        }
        mViewPager.setCurrentItem(i);
    }

    /**
     * reset text color to black
     */
    private void resetColor() {
        textAllOrder.setTextColor(getResources().getColor(R.color.black));
        viewAllOrder.setBackgroundColor(getResources().getColor(R.color.white));
        textPendingPayment.setTextColor(getResources().getColor(R.color.black));
        viewPendingPayment.setBackgroundColor(getResources().getColor(R.color.white));
        textExpressing.setTextColor(getResources().getColor(R.color.black));
        viewExpressing.setBackgroundColor(getResources().getColor(R.color.white));
        textCompleteOrder.setTextColor(getResources().getColor(R.color.black));
        viewCompleteOrder.setBackgroundColor(getResources().getColor(R.color.white));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAYMENT_SELECT && resultCode == RESULT_OK) {
            // Processing payment result
            onDataRefresh();
            selectTab(EXPRESSING_INDEX);
        }
    }
}