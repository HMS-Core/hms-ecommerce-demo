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

package com.huawei.industrydemo.shopping.fragment.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragmentViewModel;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.entity.ScanHistory;
import com.huawei.industrydemo.shopping.fragment.HomeFragment;
import com.huawei.industrydemo.shopping.page.viewmodel.MainActivityViewModel;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;
import com.huawei.industrydemo.shopping.utils.RemoteConfigUtil;
import com.huawei.industrydemo.shopping.viewadapter.HomeViewPagerAdapter;
import com.huawei.industrydemo.shopping.viewadapter.ProductHomeAdapter;
import com.huawei.industrydemo.shopping.viewadapter.ScanHistoryAdapter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.huawei.industrydemo.shopping.MainApplication.getContext;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.TOURIST_USERID;
import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/18]
 * @see [ com.huawei.industrydemo.shopping.fragment.HomeFragment]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class HomeFragmentViewModel extends BaseFragmentViewModel<HomeFragment> {
    private static final String ARTICLE_KEY_ONE = "Article_Link_1";
    private static final String ARTICLE_KEY_ONE_CN = "Article_Link_1_cn";

    private static final String ARTICLE_KEY_TWO = "Article_Link_2";
    private static final String ARTICLE_KEY_TWO_CN = "Article_Link_2_cn";

    private RecyclerView rvRecommendation;

    private RecyclerView recyclerView;

    private static final int VIEW_PAGER_HEIGHT = 846;

    /**
     * Dot below the rotation images
     */
    private List<View> dots;

    private final HomeImageHandler handler;

    private ViewPager viewPager;

    private LinearLayout lvDot;

    String article1Link = ARTICLE_KEY_ONE;
    String article2Link = ARTICLE_KEY_TWO;

    public HomeFragmentViewModel(HomeFragment homeFragment) {
        super(homeFragment);
        handler = new HomeImageHandler(new WeakReference<>(this));
    }

    @Override
    public void initView(View view) {
        view.findViewById(R.id.card_new).setOnClickListener(mFragment);
        view.findViewById(R.id.card_article1).setOnClickListener(mFragment);
        view.findViewById(R.id.card_article2).setOnClickListener(mFragment);

        recyclerView = view.findViewById(R.id.recycler_product);
        rvRecommendation = view.findViewById(R.id.recycler_recommendation);

        lvDot = view.findViewById(R.id.layout_dot);
        viewPager = view.findViewById(R.id.pager_home);
        setViewPagerHeight();

        String language = Locale.getDefault().getLanguage();
        if (Constants.LANGUAGE_ZH.equals(language)) {
            article1Link = ARTICLE_KEY_ONE_CN;
            article2Link = ARTICLE_KEY_TWO_CN;
        }
    }

    /**
     * initScanHistoryView
     */
    public void initScanHistoryView() {
        List<ScanHistory> list;
        UserRepository userRepository = new UserRepository();
        if (userRepository.getCurrentUser() == null) {
            list = DatabaseUtil.getDatabase().scanHistoryDao().getScanData(TOURIST_USERID);
        } else {
            list = DatabaseUtil.getDatabase().scanHistoryDao().getScanData(userRepository.getCurrentUser().getOpenId());
        }
        Collections.reverse(list);
        ScanHistoryAdapter adapter = new ScanHistoryAdapter(list, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

        ProductRepository productRepository = new ProductRepository();
        List<Product> recommendations = productRepository.queryAll();
        ProductHomeAdapter productHomeAdapter = new ProductHomeAdapter(recommendations, getContext(), false);
        GridLayoutManager recommendationLayoutManager = new GridLayoutManager(getContext(), 2);
        rvRecommendation.setLayoutManager(recommendationLayoutManager);
        rvRecommendation.setAdapter(productHomeAdapter);
        rvRecommendation.setNestedScrollingEnabled(false);
    }

    /**
     * initViewPager
     */
    public void initViewPager() {
        dots = new ArrayList<>();

        Integer[] images = new Integer[]{R.mipmap.banner1, R.mipmap.article1,R.mipmap.article2};
        Integer[] urls = new Integer[]{R.string.banner_url_1, R.string.banner_url_1, R.string.banner_url_1};

        for (int i = 0; i < images.length; i++) {
            // Initializing dots
            ImageView dot =
                    (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.dot_image_view, lvDot, false);
            lvDot.addView(dot);
            dots.add(dot);
        }

        viewPager.setAdapter(new HomeViewPagerAdapter(images, getContext(), position -> {
            switch (position){
                case 1:
                    showArticle(article1Link);
                    break;
                case 2:
                    showArticle(article2Link);
                    break;
                case 0:
                default:
                    Uri uri = Uri.parse(mFragment.getString(urls[position]));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mFragment.startActivity(intent);
                    break;
            }
        }));

        viewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        showPoint(position);
                        handler.sendMessage(Message.obtain(handler, HomeImageHandler.CHANGED, position, 0));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        switch (state) {
                            case ViewPager.SCROLL_STATE_DRAGGING:
                                handler.sendEmptyMessage(HomeImageHandler.PAUSE);
                                break;
                            case ViewPager.SCROLL_STATE_IDLE:
                                handler.sendEmptyMessageDelayed(HomeImageHandler.UPDATE, HomeImageHandler.TIME_DELAY);
                                break;
                            default:
                                break;
                        }
                    }
                });

        viewPager.setCurrentItem(Integer.MAX_VALUE / 2);
        viewPager.setCurrentItem(0);

        // Start the NVOD effect.
        handler.sendEmptyMessageDelayed(HomeImageHandler.UPDATE, HomeImageHandler.TIME_DELAY);
    }

    private void setViewPagerHeight() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewPager.getLayoutParams();
        layoutParams.height = VIEW_PAGER_HEIGHT;
        viewPager.setLayoutParams(layoutParams);
    }

    /**
     * Show Dots
     *
     * @param position Position of the dot to be displayed
     */
    private void showPoint(int position) {
        if (dots == null) {
            return;
        }
        int size = dots.size();
        for (int i = 0; i < size; i++) {
            dots.get(i).setBackgroundResource(R.drawable.dot_no_selected);
        }
        dots.get(position % size).setBackgroundResource(R.drawable.dot_selected);
    }

    /**
     * ViewPager set current position
     *
     * @param currentPosition currentPosition
     */
    public void setCurrentPosition(int currentPosition) {
        setViewPageScrollTime(viewPager);
        viewPager.setCurrentItem(currentPosition, true);
    }

    private void setViewPageScrollTime(ViewPager mViewPager){
        Field field = null;
        try {
            field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(),
                    new AccelerateInterpolator());
            field.set(mViewPager, scroller);
            scroller.setmDuration(1000);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * HomeImageHandler
     *
     * @return getHandler
     */
    public HomeImageHandler getHandler() {
        return handler;
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.card_new:
                Activity activity = mFragment.getActivity();
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).getMainActivityViewModel().setCurrentPage(R.id.tab_new_in);
                }
                break;
            case R.id.card_article1:
                showArticle(article1Link);
                break;
            case R.id.card_article2:
                showArticle(article2Link);
                break;
            default:
                break;
        }
    }

    private void showArticle(String articleKey) {
        Activity activity = mFragment.getActivity();
        if (activity == null){
            return;
        }
        MainActivityViewModel mainActivityViewModel = ((MainActivity) activity).getMainActivityViewModel();
        mainActivityViewModel.showLoadView();
        Map<String, Object> results = AGConnectConfig.getInstance().getMergedAll();
        if (results.containsKey(articleKey)) {
            Object url = results.get(articleKey);
            try {
                activity.runOnUiThread(() -> {
                    Uri uri = Uri.parse(url.toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mFragment.startActivity(intent);
                });
            } catch (Exception e) {
                mainActivityViewModel.hideLoadView();
                Toast.makeText(mFragment.getContext(), R.string.home_uri_error_tip, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "error:" + e.toString());
            }
        } else {
            Log.e(TAG, articleKey + " is null!");
            Toast.makeText(mFragment.getContext(), R.string.home_uri_error_tip, Toast.LENGTH_SHORT).show();
            mainActivityViewModel.hideLoadView();
            RemoteConfigUtil.fetch();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
    }

    private static class HomeImageHandler extends Handler {
        /**
         * Time interval
         */
        public static final long TIME_DELAY = 3000;

        /**
         * Request to update the displayed viewPager
         */
        public static final int UPDATE = 1;

        /**
         * Request to pause
         */
        public static final int PAUSE = 2;

        /**
         * Request to restart
         */
        public static final int RESTART = 3;

        /**
         * Record the latest page number.
         */
        public static final int CHANGED = 4;

        /**
         * Weak reference is used to prevent handler leakage.
         */
        private final WeakReference<HomeFragmentViewModel> wk;

        private int currentItem = Integer.MAX_VALUE / 2;

        public HomeImageHandler(WeakReference<HomeFragmentViewModel> wk) {
            this.wk = wk;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            HomeFragmentViewModel viewModel = wk.get();
            if (viewModel == null) {
                return;
            }

            // Check the message queue and remove unsent messages to avoid duplicate messages in complex environments.
            // This part will eat the first auto-rotation event, so you can add a condition, The event is cleared only
            // when the value is Position!=Max/2.
            // Because the first position must be equal to Max/2.
            if ((viewModel.getHandler().hasMessages(UPDATE)) && (currentItem != Integer.MAX_VALUE / 2)) {
                viewModel.getHandler().removeMessages(UPDATE);
            }
            switch (msg.what) {
                case UPDATE:
                    currentItem++;
                    viewModel.setCurrentPosition(currentItem);
                    viewModel.getHandler().sendEmptyMessageDelayed(UPDATE, TIME_DELAY);
                    Log.d(TAG, "UPDATE");
                    break;
                case PAUSE:
                    Log.d(TAG, "PAUSE");
                    break;
                case RESTART:
                    viewModel.getHandler().sendEmptyMessageDelayed(UPDATE, TIME_DELAY);
                    Log.d(TAG, "RESTART");
                    break;
                case CHANGED:
                    currentItem = msg.arg1;
                    Log.d(TAG, "CHANGED");
                    break;
                default:
                    break;
            }
        }
    }

    public static class FixedSpeedScroller extends Scroller {
        private int mDuration = 1000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        /**
         * setmDuration
         * @param time ms
         */
        public void setmDuration(int time) {
            mDuration = time;
        }

    }

}
