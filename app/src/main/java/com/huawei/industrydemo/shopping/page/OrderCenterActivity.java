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

package com.huawei.industrydemo.shopping.page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.fragment.ordercenter.AllOrderFragment;
import com.huawei.industrydemo.shopping.fragment.ordercenter.CompleteOrderFragment;
import com.huawei.industrydemo.shopping.fragment.ordercenter.PendingPaymentFragment;
import com.huawei.industrydemo.shopping.inteface.OnOrderListRefresh;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderCenter Activity
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/25]
 * @see com.huawei.industrydemo.shopping.fragment.MyFragment
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class OrderCenterActivity extends BaseActivity implements View.OnClickListener {
    
    public static final int ALL_ORDER_INDEX = 0;
    private static final int PENDING_PAYMENT_INDEX = 1;
    private static final int COMPLETED_ORDER_INDEX = 2;

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;
    private TextView textAllOrder;
    private View viewAllOrder;
    private TextView textPendingPayment;
    private View viewPendingPayment;
    private TextView textCompleteOrder;
    private View viewCompleteOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int startPage = getIntent().getIntExtra("page_index", ALL_ORDER_INDEX);
        setContentView(R.layout.activity_order_center);
        addTipView(new String[] {PUSH});
        initViews();// 初始化控件
        initEvents();// 初始化事件
        initData(startPage);// 初始化数据

        getNotificationData();
    }

    private void initData(int startPage) {
        mFragments = new ArrayList<>();
        // 将四个Fragment加入集合中
        mFragments.add(new AllOrderFragment());
        mFragments.add(new PendingPaymentFragment());
        mFragments.add(new CompleteOrderFragment());

        // 初始化适配器
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {// 从集合中获取对应位置的Fragment
                return mFragments.get(position);
            }

            @Override
            public int getCount() {// 获取集合中Fragment的总数
                return mFragments.size();
            }

        };
        // 不要忘记设置ViewPager的适配器
        mViewPager.setAdapter(mAdapter);
        selectTab(startPage);
        // 设置ViewPager的切换监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            // 页面选中事件
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected");
                // 设置position对应的集合中的Fragment
                mViewPager.setCurrentItem(position);
                resetColor();
                selectTab(position);
                ((OnOrderListRefresh) mFragments.get(position)).onDataRefresh();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initEvents() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        textAllOrder.setOnClickListener(this);
        textPendingPayment.setOnClickListener(this);
        textCompleteOrder.setOnClickListener(this);
    }

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager_order_center);

        ImageView shoppingCartIcon = findViewById(R.id.iv_icon);
        shoppingCartIcon.setVisibility(View.VISIBLE);
        shoppingCartIcon.setOnClickListener(this);
        textAllOrder = findViewById(R.id.tab_all_order);
        viewAllOrder = findViewById(R.id.view_all_order);
        textPendingPayment = findViewById(R.id.tab_pending_payment);
        viewPendingPayment = findViewById(R.id.view_pending_payment);
        textCompleteOrder = findViewById(R.id.tab_complete_order);
        viewCompleteOrder = findViewById(R.id.view_complete_order);
    }

    private void getNotificationData(){
        Intent intent = getIntent();
        if (null != intent) {
            int status = intent.getIntExtra("status", Constants.NOT_PAID);
            if(Constants.NOT_PAID == status){
            }else if(Constants.HAVE_PAID == status){
            }

        }
    }

    @Override
    public void onClick(View v) {
        resetColor();
        switch (v.getId()) {
            case R.id.iv_back:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("checkedId", R.id.tab_my);
                startActivity(intent);
                break;
            case R.id.iv_icon:
                Intent intentIcon = new Intent(this, MainActivity.class);
                intentIcon.putExtra("checkedId", R.id.tab_shop);
                startActivity(intentIcon);
                break;
            case R.id.tab_all_order:
                selectTab(ALL_ORDER_INDEX);
                break;
            case R.id.tab_pending_payment:
                selectTab(PENDING_PAYMENT_INDEX);
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
        textCompleteOrder.setTextColor(getResources().getColor(R.color.black));
        viewCompleteOrder.setBackgroundColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG, "onBackPressed: 按下了返回键");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("checkedId", R.id.tab_my);
        startActivity(intent);
    }
}