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

package com.huawei.industrydemo.shopping.page.viewmodel;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.huawei.agconnect.applinking.AGConnectAppLinking;
import com.huawei.caas.caasservice.HwCaasHandler;
import com.huawei.caas.caasservice.HwCaasServiceCallBack;
import com.huawei.caas.caasservice.HwCaasServiceManager;
import com.huawei.caas.caasservice.HwCaasUtils;
import com.huawei.caas.caasservice.HwCallStateCallBack;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.industrydemo.shopping.MainApplication;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivityViewModel;
import com.huawei.industrydemo.shopping.base.BaseDialog;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.entity.Bag;
import com.huawei.industrydemo.shopping.entity.Collection;
import com.huawei.industrydemo.shopping.entity.Order;
import com.huawei.industrydemo.shopping.entity.OrderItem;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.entity.ScanHistory;
import com.huawei.industrydemo.shopping.entity.User;
import com.huawei.industrydemo.shopping.page.BagActivity;
import com.huawei.industrydemo.shopping.page.EvaluationListActivity;
import com.huawei.industrydemo.shopping.page.FaceViewActivity;
import com.huawei.industrydemo.shopping.page.OrderSubmitActivity;
import com.huawei.industrydemo.shopping.page.ProductActivity;
import com.huawei.industrydemo.shopping.page.SceneViewActivity;
import com.huawei.industrydemo.shopping.repository.BagRepository;
import com.huawei.industrydemo.shopping.repository.OrderRepository;
import com.huawei.industrydemo.shopping.repository.ProductRepository;
import com.huawei.industrydemo.shopping.repository.UserRepository;
import com.huawei.industrydemo.shopping.utils.AnalyticsUtil;
import com.huawei.industrydemo.shopping.utils.AppLinkUtils;
import com.huawei.industrydemo.shopping.utils.DatabaseUtil;
import com.huawei.industrydemo.shopping.utils.MessagingUtil;
import com.huawei.industrydemo.shopping.viewadapter.ProductViewPagerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.huawei.caas.caasservice.HwCaasUtils.HICALL_NOT_ENABLE;
import static com.huawei.hms.analytics.type.HAEventType.ADDPRODUCT2CART;
import static com.huawei.hms.analytics.type.HAEventType.VIEWCONTENT;
import static com.huawei.hms.analytics.type.HAEventType.VIEWPRODUCT;
import static com.huawei.hms.analytics.type.HAParamType.CATEGORY;
import static com.huawei.hms.analytics.type.HAParamType.CONTENTTYPE;
import static com.huawei.hms.analytics.type.HAParamType.CURRNAME;
import static com.huawei.hms.analytics.type.HAParamType.PRICE;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTID;
import static com.huawei.hms.analytics.type.HAParamType.PRODUCTNAME;
import static com.huawei.hms.analytics.type.HAParamType.QUANTITY;
import static com.huawei.hms.analytics.type.HAParamType.REVENUE;
import static com.huawei.industrydemo.shopping.constants.Constants.CNY;
import static com.huawei.industrydemo.shopping.constants.Constants.EMPTY;
import static com.huawei.industrydemo.shopping.constants.Constants.LOGIN_REQUEST_CODE;
import static com.huawei.industrydemo.shopping.constants.KeyConstants.TOURIST_USERID;
import static com.huawei.industrydemo.shopping.constants.KitConstants.ACCOUNT_LOGIN;
import static com.huawei.industrydemo.shopping.constants.KitConstants.AR_ENGINE_REALITY;
import static com.huawei.industrydemo.shopping.constants.KitConstants.CAAS_SHARE;
import static com.huawei.industrydemo.shopping.constants.KitConstants.PUSH_BAG;
import static com.huawei.industrydemo.shopping.constants.KitConstants.PUSH_SUB;
import static com.huawei.industrydemo.shopping.constants.KitConstants.SCENE_3D;
import static com.huawei.industrydemo.shopping.constants.LogConfig.TAG;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/23]
 * @see [com.huawei.industrydemo.shopping.page.ProductActivity]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class ProductActivityViewModel extends BaseActivityViewModel<ProductActivity> {
    private static final int CACHE_PAGE_COUNT = 3;

    private static final int FACE_VIEW_REQUEST_CODE = 2;

    private static final int CAAS_APP_REQUEST_CODE = 1;

    private static final int SHOW_CAAS_REQUEST_CODE = 5;

    private static HwCaasServiceManager mHwCaasServiceManager = null;

    // Add for Caas Service
    private static boolean mIsCaasKitInit = false;

    private static HwCaasHandler mHwCaasHandler;

    private static HwCaasUtils.CallState callState = HwCaasUtils.CallState.NO_CALL;

    private Product product;

    private TextView textViewCount;

    private TextView textSend;

    private ImageView iv3D;

    private final int POSITION_3D = 0;

    private int productCount = 1;

    private static long lastClickTime;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private LocationCallback mLocationCallback;

    private LocationRequest mLocationRequest;

    private SettingsClient mSettingsClient;

    private ProductViewPagerAdapter adapter;

    private WisePlayer wisePlayer;

    private boolean mIsHasCaaSContacts = false;

    private final int CONTACT_POSITION_X = 50;

    private final int CONTACT_POSITION_Y = 50;

    private User mUser;

    private final UserRepository mUserRepository;

    private boolean hasCollected = false;


    private TextView delButton;

    private TextView addButton;

    /**
     * constructor
     *
     * @param productActivity Activity object
     */
    public ProductActivityViewModel(ProductActivity productActivity) {
        super(productActivity);
        mUserRepository = new UserRepository();
        mUser = mUserRepository.getCurrentUser();
    }

    /**
     * init
     */
    public void init() {
        Intent intent = mActivity.getIntent();
        if (intent == null) {
            return;
        }

        if (intent.hasExtra(KeyConstants.PRODUCT_KEY)) {
            int productNumber = intent.getIntExtra(KeyConstants.PRODUCT_KEY, 1);
            product = new ProductRepository().queryByNumber(productNumber);
            updateView(product);
        } else {
            getDeepLink();
        }

    }

    private void updateView(Product product) {
        if (product != null) {
            initView();
            reportWatchEvent();
            requestLocationPermission();
        } else {
            Toast.makeText(mActivity, R.string.no_product_tip, Toast.LENGTH_SHORT).show();
            mActivity.finish();
        }
        initCollectionStatus();
    }

    private void getDeepLink() {
        AGConnectAppLinking connectAppLinking = AGConnectAppLinking.getInstance();
        if (connectAppLinking == null) {
            Log.e(TAG, "AGConnectAppLinking == null ");
            Toast.makeText(mActivity, R.string.no_product_tip, Toast.LENGTH_SHORT).show();
            mActivity.finish();
            return;
        }
        connectAppLinking.getAppLinking(mActivity, mActivity.getIntent())
                .addOnSuccessListener(resolvedLinkData -> {
                    Uri deepLink = null;
                    if (resolvedLinkData != null) {
                        deepLink = resolvedLinkData.getDeepLink();
                        String s = deepLink.toString();
                        Log.d(TAG, "getDeepLink: " + s);
                        int num = getNumFromDeep(s);
                        Log.d(TAG, "getDeepLink:num " + num);
                        product = new ProductRepository().queryByNumber(num);
                        if (product != null) {
                            Log.d(TAG, "getDeepLink: " + product.toString());
                            initView();
                            reportWatchEvent();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.i("AppLinking", "getAppLinking:onFailure", e);
                });
    }

    /**
     * @param deepLink http://...?num=1
     * @return 1
     */
    private int getNumFromDeep(String deepLink) {
        int num = 1;
        if (deepLink.contains("?")) {
            String[] arr = deepLink.split("\\?");
            String paras = arr[1];// x=1&y=2
            if (paras.contains("&")) {
                String[] paraArr = paras.split("&");
                for (String p : paraArr) {
                    if (p.contains("=")) {
                        String[] kv = p.split("=");
                        String k = kv[0];
                        if ("num".equals(k)) {
                            num = Integer.parseInt(kv[1]);
                            return num;
                        }
                    }
                }
            }
        }
        return num;
    }

    @Override
    public void initView() {
        iv3D = mActivity.findViewById(R.id.iv_3d);

        storeUserBrowsingRecords();
        initViewPager();
        TextView dPTextView = mActivity.findViewById(R.id.text_display_price);
        dPTextView.setText(mActivity.getString(R.string.product_price, product.getBasicInfo().getDisplayPrice()));
        dPTextView.setPaintFlags(dPTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // Set strikethrough
        ((TextView) mActivity.findViewById(R.id.text_price))
                .setText(mActivity.getString(R.string.product_price, product.getBasicInfo().getPrice()));
        ((TextView) mActivity.findViewById(R.id.text_name)).setText(product.getBasicInfo().getName());
        ((TextView) mActivity.findViewById(R.id.text_color))
                .setText(product.getBasicInfo().getConfiguration().getColor());
        ((TextView) mActivity.findViewById(R.id.text_capacity))
                .setText(product.getBasicInfo().getConfiguration().getCapacity());
        ((TextView) mActivity.findViewById(R.id.text_version))
                .setText(product.getBasicInfo().getConfiguration().getVersion());

        addButton = mActivity.findViewById(R.id.btn_add);
        addButton.setOnClickListener(mActivity);
        addButton.setTextColor(mActivity.getResources().getColor(R.color.red));
        addButton.setBackgroundResource(R.drawable.circle_add_delete_type_red);
        delButton = mActivity.findViewById(R.id.btn_delete);
        delButton.setOnClickListener(mActivity);

        mActivity.findViewById(R.id.add_bag).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.buy_now).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.layout_evaluate).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.layout_favorites).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.layout_bag).setOnClickListener(mActivity);

        // Caas Function
        mActivity.findViewById(R.id.caasView).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.share).setOnClickListener(mActivity);

        textViewCount = mActivity.findViewById(R.id.text_count);
        if (product.getAr() == null || EMPTY.equals(product.getAr())) {
            mActivity.findViewById(R.id.iv_ar).setVisibility(View.GONE);
        } else {
            mActivity.findViewById(R.id.iv_ar).setOnClickListener(mActivity);
        }

        if (product.getThreeDimensional() == null || EMPTY.equals(product.getThreeDimensional())) {
            iv3D.setVisibility(View.GONE);
        } else {
            iv3D.setOnClickListener(mActivity);
        }

        textSend = mActivity.findViewById(R.id.text_send);
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.iv_3d:
                long curClickTime1 = System.currentTimeMillis();
                if ((curClickTime1 - lastClickTime) >= Constants.MIN_CLICK_DELAY_TIME) {
                    mActivity.addTipView(new String[]{SCENE_3D}, () -> show3Dmodel(curClickTime1));
                }
                break;
            case R.id.iv_ar:
                if (ContextCompat.checkSelfPermission(mActivity,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA},
                            FACE_VIEW_REQUEST_CODE);
                } else {
                    mActivity.addTipView(new String[]{AR_ENGINE_REALITY}, this::showARView);
                }
                break;
            case R.id.btn_add:
                ++productCount;
                textViewCount.setText(String.valueOf(productCount));
                delButton.setTextColor(mActivity.getResources().getColor(R.color.red));
                delButton.setBackgroundResource(R.drawable.circle_add_delete_type_red);
                break;
            case R.id.btn_delete:
                if (productCount > 1) {
                    --productCount;
                    textViewCount.setText(String.valueOf(productCount));
                    if (productCount == 1) {
                        delButton.setTextColor(mActivity.getResources().getColor(R.color.black));
                        delButton.setBackgroundResource(R.drawable.circle_add_delete_type);
                    }
                }
                break;
            case R.id.layout_bag:// View Bag
                mActivity.startActivity(new Intent(mActivity, BagActivity.class));
                break;
            case R.id.add_bag:
                if (mUser == null) {
                    // no login
                    mActivity.signIn();
                } else {
                    // has login
                    mActivity.addTipView(new String[]{PUSH_BAG}, this::addToShoppingCart);
                }
                break;
            case R.id.buy_now:// buy now
                if (mUser == null) {
                    // no login
                    mActivity.addTipView(new String[]{ACCOUNT_LOGIN}, () -> mActivity.signIn());
                } else {
                    // has login
                    Intent intent = new Intent(mActivity, OrderSubmitActivity.class);
                    int orderNumber = initOrder();
                    intent.putExtra(KeyConstants.ORDER_KEY, orderNumber);
                    mActivity.startActivity(intent);
                }
                break;
            case R.id.layout_evaluate:
                Intent intent2 = new Intent(mActivity, EvaluationListActivity.class);
                intent2.putExtra(KeyConstants.PRODUCT_KEY, product.getNumber());
                mActivity.startActivity(intent2);
                break;
            case R.id.caasView:  // Add for caas Service
                initCaas();
                break;
            case R.id.share:
                int num = product.getNumber();
                Log.d(TAG, "onClickEvent: " + num);
                String suffix = "num=" + String.valueOf(num) + "&";
                new AppLinkUtils(mActivity).createAppLinkingAndShare(suffix);
                break;
            case R.id.layout_favorites:
                if (mUser == null) {
                    // Start the Huawei Account Login
                    mActivity.signIn();
                } else {
                    changeCollectionStatus();
                }
                break;

            default:
                break;
        }
    }

    private void show3Dmodel(long curClickTime1) {
        Intent intent3d = new Intent(mActivity, SceneViewActivity.class);
        intent3d.putExtra(KeyConstants.THREE_DIMENSIONAL_DATA, product.getThreeDimensional());
        mActivity.startActivity(intent3d);
        lastClickTime = curClickTime1;
        reportProductViewEvent("3D View");
    }

    private void showARView() {
        Intent intentAr = new Intent(mActivity, FaceViewActivity.class);
        intentAr.putExtra(KeyConstants.THREE_DIMENSIONAL_DATA, product.getAr());
        mActivity.startActivity(intentAr);
        reportProductViewEvent("AR View");
    }

    private void changeCollectionStatus() {
        ImageView imageView = mActivity.findViewById(R.id.collect_product);
        Collection collection = new Collection();
        collection.setUserId(mUser.getOpenId());
        collection.setProductNumber(product.getNumber());
        if (hasCollected) {
            imageView.setImageResource(R.mipmap.product_unsaved);
            DatabaseUtil.getDatabase().collectionDao().delete(collection);
            hasCollected = false;
        } else {
            imageView.setImageResource(R.mipmap.product_saved);
            DatabaseUtil.getDatabase().collectionDao().setCollectionData(collection);
            hasCollected = true;
            mActivity.addTipView(new String[]{PUSH_SUB}, () -> MessagingUtil
                    .saveNotificationMessage(mActivity, product.getBasicInfo().getName()));
        }
    }

    /* add for caas service begin */
    private void initCaas() {
        Log.d(TAG, "caasKitInit. " + mIsCaasKitInit);
        if (mIsCaasKitInit) {
            if (callState == HwCaasUtils.CallState.ACTIVE_CALL) {
                Toast.makeText(mActivity, R.string.caas_exist, Toast.LENGTH_SHORT).show();
            } else {
                caasStartcall();
            }
            return;
        }
        if (mHwCaasServiceManager == null) {
            // initialize mHwCaasServiceManager instance.
            mHwCaasServiceManager = HwCaasServiceManager.init();
        }
        // initialize HwCaasHandler instance through handlerType.
        mHwCaasServiceManager.initHandler(mActivity, HwCaasUtils.SCREEN_SHARING_TYPE, initCallBack());


    }

    private HwCaasServiceCallBack initCallBack() {
        return new HwCaasServiceCallBack() {
            @Override
            public void initSuccess(HwCaasHandler handler) {
                Log.d(TAG, "CaasServiceinitSuccess: ");
                mIsCaasKitInit = true;
                // callback after successful initialization of HwCaasHandler.
                mHwCaasHandler = handler;

                if (mHwCaasHandler != null) {
                    // The application name shown to peer side
                    mHwCaasHandler.setCallerAppName("Sparkle Store");
                    // Contact list view
                    mHwCaasHandler.setContactViewStyle(HwCaasUtils.ContactsViewStyle.FLOAT_VIEW);
                    mHwCaasHandler.setFloatViewLocation(HwCaasUtils.CONTACTVIEW, HwCaasUtils.POINT_RIGHTANDDOWN,
                            CONTACT_POSITION_X, CONTACT_POSITION_Y);

                    // Monitor the calling status
                    mHwCaasHandler.setCallStateCallBack(mCallStateCallBack);
                }
                caasStartcall();
            }

            @Override
            public void initFail(int retCode) {
                Log.d(TAG, "initFail: " + retCode);

                String initFailText;
                // callback if init Handler fail.
                if (retCode == HICALL_NOT_ENABLE) {
                    initFailText = mActivity.getString(R.string.caas_no_active);
                } else {
                    initFailText = mActivity.getString(R.string.caas_fail, retCode);
                }
                Log.i(TAG, "ScreenSharing service initialization retCode: " + retCode);

                mActivity.runOnUiThread(() -> {
                    Bundle data = new Bundle();

                    data.putString("ConfirmButton", mActivity.getString(R.string.confirm));
                    data.putString("CancelButton", mActivity.getString(R.string.cancel));
                    data.putString("Content", initFailText);

                    BaseDialog dialog = new BaseDialog(mActivity, data, true);
                    dialog.setConfirmListener(v -> dialog.dismiss());
                    dialog.setCancelListener(v -> dialog.dismiss());
                    dialog.show();
                    caasKitRelease();
                });
            }

            @Override
            public void releaseSuccess() {
                Log.i(TAG, "releaseSuccess");
                // callback after successful release of mHwCaasServiceManager.
                mHwCaasHandler = null;
                mIsCaasKitInit = false;
            }
        };
    }

    public void caasStartcall() {
        Log.d(TAG, "caasStartcall: ");

        if (mHwCaasHandler != null) {
            // If Contact exist in ths list
            mIsHasCaaSContacts = mHwCaasHandler.hasCaaSContacts(HwCaasUtils.ContactsType.SCREEN_SHARING_CONTACTS);
            if (!mIsHasCaaSContacts) {
                mActivity.runOnUiThread(() -> {
                    Bundle data = new Bundle();
                    data.putString("ConfirmButton", mActivity.getString(R.string.caas_now));
                    data.putString("CancelButton", mActivity.getString(R.string.caas_later));
                    data.putString("Content", mActivity.getString(R.string.no_contacts));

                    BaseDialog dialog = new BaseDialog(mActivity, data, true);
                    dialog.setConfirmListener(v -> {
                        PackageManager manager = mActivity.getPackageManager();
                        Intent newintent = new Intent(Intent.ACTION_MAIN);
                        newintent.addCategory(Intent.CATEGORY_LAUNCHER);
                        newintent.setPackage("com.huawei.meetime");
                        List<ResolveInfo> apps = manager.queryIntentActivities(newintent, 0);
                        if (apps.size() > 0) {
                            ResolveInfo ri = apps.get(0);
                            String packageName = ri.activityInfo.packageName;
                            String className = ri.activityInfo.name;
                            ComponentName cn = new ComponentName(packageName, className);
                            newintent.setComponent(cn);
                            mActivity.startActivityForResult(newintent, CAAS_APP_REQUEST_CODE);
                        } else {
                            Toast.makeText(mActivity, R.string.caas_noapp, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    });
                    dialog.setCancelListener(v -> dialog.dismiss());
                    dialog.show();
                });

                // If there is no contact. Service can be released.
                caasKitRelease();
                return;
            }
            mActivity.addTipView(new String[]{CAAS_SHARE}, this::sendShow);
        }
    }

    private final HwCallStateCallBack mCallStateCallBack = state -> {
        Log.d(TAG, "callState: " + state);
        callState = state;
    };

    public static void caasKitRelease() {
        Log.d(TAG, "caasKitRelease.");
        if (mIsCaasKitInit) {
            if (mHwCaasServiceManager != null) {
                // source release.
                mHwCaasServiceManager.release();
                mHwCaasServiceManager = null;
            }
        }
        mIsCaasKitInit = false;
    }

    private void sendShow() {
        Log.d(TAG, "Start calling.");
        if (mHwCaasHandler == null) {
            return;
        }

        Log.d(TAG, "isHasCaaSContacts: " + mIsHasCaaSContacts);

        if (!mIsHasCaaSContacts) {
            return;
        }
        boolean ret = mHwCaasHandler.sendEventToCaasService(HwCaasUtils.SHOW_CONTACTS);
        if (!ret) {
            Toast.makeText(mActivity, R.string.caas_callfail, Toast.LENGTH_LONG).show();
        } else {
            AnalyticsUtil.interfaceSharingReport();
        }
        Log.d(TAG, "ret: " + ret);
    }
    /* add for caas service end */

    private void initPlayer() {
        if (MainApplication.getWisePlayerFactory() == null) {
            return;
        }
        wisePlayer = MainApplication.getWisePlayerFactory().createWisePlayer();
    }

    public void releasePlayer() {
        if (wisePlayer != null) {
            wisePlayer.stop();
            wisePlayer.release();
        }

    }

    private void storeUserBrowsingRecords() {
        ScanHistory scanHistory = new ScanHistory();
        scanHistory.setProductNumber(product.getNumber());
        if (mUser == null) {
            scanHistory.setUserId(TOURIST_USERID);
        } else {
            scanHistory.setUserId(mUser.getOpenId());
        }
        DatabaseUtil.getDatabase().scanHistoryDao().setScanData(scanHistory);
    }

    private void initViewPager() {
        initPlayer();
        ViewPager viewPager = mActivity.findViewById(R.id.view_pager_product);
        adapter = new ProductViewPagerAdapter(product.getImages(), product.getVideoUrl(), mActivity, wisePlayer);
        boolean isHasVideo = adapter.isHasVideo();

        TextView tvTip = mActivity.findViewById(R.id.tv_tip);
        int total = adapter.getCount();
        tvTip.setText(mActivity.getString(R.string.current_position, 1, total));

        viewPager.setOffscreenPageLimit(CACHE_PAGE_COUNT);
        if (isHasVideo && wisePlayer != null) { // has video
            adapter.setInitVideoInterface((videoUrl, surfaceView) -> {
                wisePlayer.setVideoType(0);
                wisePlayer.setBookmark(10000);
                wisePlayer.setCycleMode(1);
                wisePlayer.setPlayUrl(videoUrl);
                wisePlayer.setPlayEndListener(wisePlayer -> adapter.updatePlayCompleteView());
                wisePlayer.ready();
            });
        }
        int videoPagePosition = adapter.getVideoPosition();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isHasVideo && wisePlayer != null) {
                    if (videoPagePosition == position) {// on video page
                        wisePlayer.start();
                        adapter.updatePlayView(wisePlayer);
                    } else { // on the other page
                        wisePlayer.pause();
                        adapter.updatePlayProgressView(wisePlayer.getCurrentTime(), wisePlayer.getBufferTime());
                    }
                }

                if (position == POSITION_3D) {
                    iv3D.setVisibility(View.VISIBLE);
                } else {
                    iv3D.setVisibility(View.GONE);
                }

                tvTip.setText(mActivity.getString(R.string.current_position, position + 1, total));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(adapter);
    }

    private void addToShoppingCart() {
        BagRepository bagRepository = new BagRepository();
        List<Bag> bagList = bagRepository.queryByUser(mUser);
        boolean hasSameProduct = false;
        for (Bag bag : bagList) {
            if (bag.getProductNum() == product.getNumber()) {
                int quantity = bag.getQuantity() + productCount;
                bag.setQuantity(quantity);
                bag.setChoosed(true);
                hasSameProduct = true;
                break;
            }
        }
        if (!hasSameProduct) {
            Bag bag = new Bag();
            bag.setChoosed(true);
            bag.setProductNum(product.getNumber());
            bag.setQuantity(productCount);
            bagList.add(bag);
        }
        bagRepository.insertAll(bagList, mUser);
        reportBagEvent();
        MessagingUtil.cartCheckoutReminder(mActivity, product.getBasicInfo().getName());
        Toast.makeText(mActivity, R.string.add_to_car_success, Toast.LENGTH_SHORT).show();

    }

    private void reportBagEvent() {
        Bundle bundle = new Bundle();

        bundle.putString(PRODUCTID, Integer.toString(product.getNumber()).trim());
        bundle.putString(PRODUCTNAME, product.getBasicInfo().getShortName().trim());
        bundle.putString(CATEGORY, product.getCategory().trim());
        bundle.putInt(QUANTITY, productCount);
        bundle.putDouble(PRICE, product.getBasicInfo().getPrice());
        bundle.putDouble(REVENUE, (product.getBasicInfo().getPrice() * productCount));
        bundle.putString(CURRNAME, CNY);

        AnalyticsUtil.getInstance(mActivity).onEvent(ADDPRODUCT2CART, bundle);
    }

    private void reportProductViewEvent(String contentType) {
        Bundle bundle = new Bundle();

        bundle.putString(PRODUCTID, Integer.toString(product.getNumber()).trim());
        bundle.putString(CONTENTTYPE, contentType.trim());

        AnalyticsUtil.getInstance(mActivity).onEvent(VIEWCONTENT, bundle);
    }

    private void reportWatchEvent() {
        Bundle bundle = new Bundle();

        bundle.putString(PRODUCTID, Integer.toString(product.getNumber()).trim());
        bundle.putString(PRODUCTNAME, product.getBasicInfo().getShortName().trim());
        bundle.putString(CATEGORY, product.getCategory().trim());
        bundle.putDouble(PRICE, product.getBasicInfo().getPrice());
        bundle.putDouble(REVENUE, product.getBasicInfo().getPrice());
        bundle.putString(CURRNAME, CNY);

        AnalyticsUtil.getInstance(mActivity).onEvent(VIEWPRODUCT, bundle);
    }

    private int initOrder() {
        Order order = new Order();
        List<OrderItem> list = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setCount(productCount);
        orderItem.setProductNum(product.getNumber());
        list.add(orderItem);
        order.setTotalPrice(productCount * product.getBasicInfo().getPrice());
        order.setActualPrice(productCount * product.getBasicInfo().getPrice());
        order.setStatus(Constants.NOT_PAID);
        new OrderRepository().insert(order, list, mUser);
        return order.getNumber();
    }

    private void initLocation() {
        // Creating a Location Service Client
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);
        mSettingsClient = LocationServices.getSettingsClient(mActivity);
        mLocationRequest = new LocationRequest();
        // Sets the interval for location update (unit: Millisecond)
        mLocationRequest.setInterval(5000);
        mLocationRequest.setNumUpdates(1);
        // Sets the priority
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (null == mLocationCallback) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        List<Location> locations = locationResult.getLocations();
                        if (!locations.isEmpty()) {
                            for (Location location : locations) {
                                Log.i(TAG,
                                        "onLocationResult location[Longitude,Latitude,Accuracy]:" + location.getLongitude()
                                                + "," + location.getLatitude() + "," + location.getAccuracy());
                            }

                            String addressText = transLocationToGeoCoder(locations.get(0));
                            if (!TextUtils.isEmpty(addressText)) {
                                mActivity.runOnUiThread(() -> textSend.setText(addressText));
                            }
                        }
                    }
                }
            };
        }

        getLastLocation();
    }

    private String transLocationToGeoCoder(Location location) {
        String mSendText = EMPTY;
        try {
            Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            mSendText = addressList != null && addressList.size() > 0
                    ? addressList.get(0).getLocality() + " " + addressList.get(0).getSubLocality()
                    : mActivity.getString(R.string.empty_address);
            Log.i(TAG, "geocoder  :::" + mSendText);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return mSendText;
    }

    /**
     * Obtain the last known location
     */
    private void getLastLocation() {
        try {
            Task<Location> lastLocation = mFusedLocationProviderClient.getLastLocation();

            lastLocation.addOnSuccessListener(location -> {
                if (location == null) {
                    Log.i(TAG, "getLastLocation onSuccess location is null");
                    requestLocationUpdatesWithCallback();
                    return;
                }
                Log.i(TAG, "getLastLocation onSuccess location[Longitude,Latitude]:" + location.getLongitude() + ","
                        + location.getLatitude());

                String addressText = transLocationToGeoCoder(location);
                if (!TextUtils.isEmpty(addressText)) {
                    mActivity.runOnUiThread(() -> textSend.setText(addressText));
                }
            }).addOnFailureListener(exception -> Log.e(TAG, "getLastLocation onFailure:" + exception.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, "getLastLocation exception:" + e.getMessage());
        }
    }

    /**
     *
     */
    private void requestLocationUpdatesWithCallback() {
        Log.i(TAG, "requestLocationUpdatesWithCallback");
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // Before requesting location update, invoke checkLocationSettings to check device settings.
            Task<LocationSettingsResponse> locationSettingsResponseTask =
                    mSettingsClient.checkLocationSettings(locationSettingsRequest);
            locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {
                Log.i(TAG, "check location settings success");
                mFusedLocationProviderClient
                        .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                        .addOnSuccessListener(aVoid -> Log.i(TAG, "requestLocationUpdatesWithCallback onSuccess"))
                        .addOnFailureListener(exception -> Log.e(TAG,
                                "requestLocationUpdatesWithCallback onFailure:" + exception.getMessage()));
            }).addOnFailureListener(exception -> {
                Log.e(TAG, "checkLocationSetting onFailure:" + exception.getMessage());
                int statusCode = ((ApiException) exception).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // When the startResolutionForResult is invoked, a dialog box is displayed, asking you
                            // to open the corresponding permission.
                            ResolvableApiException rae = (ResolvableApiException) exception;
                            rae.startResolutionForResult(mActivity, 0);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.e(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    default:
                        break;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "requestLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    /**
     * Removed when the location update is no longer required.
     */
    public void removeLocationUpdatesWithCallback() {
        if (adapter != null) {
            adapter.removeUpdateViewHandler();
        }

        if (mLocationCallback == null) {
            return;
        }
        try {
            Task<Void> voidTask = mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            voidTask.addOnSuccessListener(aVoid -> Log.i(TAG, "removeLocationUpdatesWithCallback onSuccess"))
                    .addOnFailureListener(e -> Log.e(TAG, "removeLocationUpdatesWithCallback onFailure:" + e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, "removeLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    private void requestLocationPermission() {
        // You must have the ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission. Otherwise, the location service
        // is unavailable.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "sdk < 28 Q");
            if (ActivityCompat.checkSelfPermission(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(mActivity, strings, 1);
                return;
            }
        }
        initLocation();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            mUser = mUserRepository.getCurrentUser();
            changeCollectionStatus();
            return;
        }
        if (requestCode == SHOW_CAAS_REQUEST_CODE && resultCode == RESULT_OK) {
            PackageManager manager = mActivity.getPackageManager();
            Intent newintent = new Intent(Intent.ACTION_MAIN);
            newintent.addCategory(Intent.CATEGORY_LAUNCHER);
            newintent.setPackage("com.huawei.meetime");
            List<ResolveInfo> apps = manager.queryIntentActivities(newintent, 0);
            if (apps.size() > 0) {
                ResolveInfo ri = apps.get(0);
                String packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                ComponentName cn = new ComponentName(packageName, className);
                newintent.setComponent(cn);
                mActivity.startActivityForResult(newintent, 1);
            } else {
                Toast.makeText(mActivity, R.string.caas_noapp, Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful");
                initLocation();
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed");
            }
        } else if (requestCode == FACE_VIEW_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent3d = new Intent(mActivity, FaceViewActivity.class);
                intent3d.putExtra(KeyConstants.THREE_DIMENSIONAL_DATA, product.getAr());
                mActivity.startActivity(intent3d);
            }
        }
    }

    /**
     * pausePlay
     */
    public void pausePlay() {
        if (wisePlayer != null) {
            wisePlayer.pause();
        }
    }

    private void initCollectionStatus() {
        ImageView imageView = mActivity.findViewById(R.id.collect_product);
        // product is collected or not
        if (mUser == null) {
            imageView.setImageResource(R.mipmap.product_unsaved);
        } else {
            List<Collection> collectionList =
                    DatabaseUtil.getDatabase().collectionDao().getCollectionData(mUser.getOpenId());
            for (Collection c : collectionList) {
                if (c.getProductNumber() == product.getNumber()) {
                    hasCollected = true;
                }
            }
            if (hasCollected) {
                imageView.setImageResource(R.mipmap.product_saved);
            } else {
                imageView.setImageResource(R.mipmap.product_unsaved);
            }

        }
    }
}
