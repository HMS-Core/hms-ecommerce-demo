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

package com.huawei.industrydemo.shopping.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseFragment;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.fragment.home.CatalogueImageInfo;
import com.huawei.industrydemo.shopping.fragment.home.HomeImageHandler;
import com.huawei.industrydemo.shopping.page.SearchActivity;
import com.huawei.industrydemo.shopping.utils.ProductBase;
import com.huawei.industrydemo.shopping.viewadapter.CatalogueTypeGridAdapter;
import com.huawei.industrydemo.shopping.viewadapter.HomeViewPagerAdapter;
import com.huawei.industrydemo.shopping.viewadapter.ProductHomeAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Home page
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @see com.huawei.industrydemo.shopping.MainActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {


    /**
     * Dot below the rotation images
     */
    private List<View> dots;

    private HomeImageHandler handler;

    private ViewPager viewPager;

    public HomeFragment() {

        setKits(new String[]{SCAN, ML, ANALYTICS});
        handler = new HomeImageHandler(new WeakReference<>(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addTipView();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_scan: // scan
                HomeFragment.this.requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.CAMERA_REQ_CODE);
                break;
            case R.id.bar_search: // search
            case R.id.image_search: // click search image
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.image_take_photo: // take photo
                HomeFragment.this.requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.CAMERA_TAKE_PHOTO);
                break;
            default:
                break;
        }
    }

    private void initView(View view) {
        view.findViewById(R.id.image_scan).setOnClickListener(this);
        view.findViewById(R.id.image_take_photo).setOnClickListener(this);
        view.findViewById(R.id.image_search).setOnClickListener(this);
        view.findViewById(R.id.bar_search).setOnClickListener(this);

        initViewPager(view);
        initCatalogueView(view.findViewById(R.id.recycler_catalogue_type));
        initProductView(view.findViewById(R.id.recycler_product));
    }


    private void initViewPager(View view) {
        viewPager = view.findViewById(R.id.pager_home);

        dots = new ArrayList<>();
        LinearLayout layout = view.findViewById(R.id.layout_dot);
        Integer[] urls = new Integer[]{R.drawable.banner1, R.drawable.banner2};
        for (int i = 0; i < urls.length; i++) {
            // Initializing dots
            ImageView dot = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.dot_image_view, layout, false);
            layout.addView(dot);
            dots.add(dot);
        }
        viewPager.setAdapter(new HomeViewPagerAdapter(urls, getContext()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        //Start the NVOD effect.
        handler.sendEmptyMessageDelayed(HomeImageHandler.UPDATE, HomeImageHandler.TIME_DELAY);
    }

    private void initCatalogueView(RecyclerView recyclerView) {
        List<CatalogueImageInfo> list = new ArrayList<>();
        String[] name = getResources().getStringArray(R.array.catalogue_type);

        int[] imageIds = new int[]{R.mipmap.catalogue_phone, R.mipmap.catalogue_lady_coat, R.mipmap.catalogue_man_coat,
                R.mipmap.catalogue_electronics, R.mipmap.catalogue_shoe_bag, R.mipmap.catalogue_home};
        for (int i = 0; i < name.length; i++) {
            list.add(new CatalogueImageInfo(i, name[i], imageIds[i]));
        }
        CatalogueTypeGridAdapter adapter = new CatalogueTypeGridAdapter(list, getContext());
        adapter.setOnItemClickListener(position -> ((MainActivity) Objects.requireNonNull(getActivity())).showCatalogueFragment(position));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }

    private void initProductView(RecyclerView recyclerView) {
        List<Product> list = ProductBase.getInstance().queryAll();
        ProductHomeAdapter adapter = new ProductHomeAdapter(list, getContext(), false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Show Dots
     *
     * @param position Position of the dot to be displayed
     */
    private void showPoint(int position) {
        int size = dots.size();
        for (int i = 0; i < size; i++) {
            dots.get(i).setBackgroundResource(R.drawable.dot_no_selected);
            dots.get(position % size).setBackgroundResource(R.drawable.dot_selected);
        }
    }

    public HomeImageHandler getHandler() {
        return handler;
    }

    public void setCurrentPosition(int currentPosition) {
        viewPager.setCurrentItem(currentPosition, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (requestCode == Constants.CAMERA_REQ_CODE) {
            ScanUtil.startScan(getActivity(), Constants.REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
        }

        if (requestCode == Constants.CAMERA_TAKE_PHOTO) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Objects.requireNonNull(getActivity()).startActivityForResult(intent, Constants.TAKE_PHOTO_WITH_DATA);
        }
    }
}
