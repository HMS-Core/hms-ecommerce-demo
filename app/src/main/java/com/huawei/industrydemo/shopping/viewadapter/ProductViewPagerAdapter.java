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

package com.huawei.industrydemo.shopping.viewadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionAnalyzer;
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionAnalyzerFactory;
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionAnalyzerSetting;
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionResult;
import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hms.videokit.player.common.PlayerConstants;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.constants.Constants;
import com.huawei.industrydemo.shopping.page.ProductActivity;
import com.huawei.industrydemo.shopping.utils.TimeUtil;

/**
 * ProductViewPagerAdapter
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/22]
 * @see com.huawei.industrydemo.shopping.page.ProductActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class ProductViewPagerAdapter extends PagerAdapter implements SurfaceHolder.Callback, SeekBar.OnSeekBarChangeListener {
    private String[] imgs;

    private String videoUrl;

    private Context context;

    private boolean isHasVideo;

    private InitVideoInterface initVideoInterface;

    private int videoPosition;
    private WisePlayer wisePlayer;
    private SurfaceView surfaceView;
    private SeekBar seekBar;
    private TextView currentTimeTv;
    private TextView totalTimeTv;
    private boolean isUserTrackingTouch = false;
    private ImageView playImg;
    private boolean isPlaying = false;
    private boolean isSuspend = false;

    public ProductViewPagerAdapter(String[] imgs, String videoUrl, Context context, WisePlayer wisePlayer) {
        this.imgs = imgs;
        this.videoUrl = videoUrl;
        this.context = context;
        isHasVideo = !(videoUrl == null || "".equals(videoUrl));
        videoPosition = isHasVideo ? imgs.length : -1;
        this.wisePlayer = wisePlayer;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_view_pager, container, false);
        initView(view, position);
        container.addView(view);
        return view;
    }

    private void initView(View view, int position) {
        ImageView imageView = view.findViewById(R.id.iv_product);
        RelativeLayout productRl = view.findViewById(R.id.rl_product);
        surfaceView = view.findViewById(R.id.sf_video);
        RelativeLayout rlVideo = view.findViewById(R.id.rl_video);
        TextView notice = view.findViewById(R.id.video_notice);
        initPlayView(view);
        if (position < imgs.length) { // image
            surfaceView.setVisibility(View.GONE);
            rlVideo.setVisibility(View.GONE);
            productRl.setVisibility(View.VISIBLE);
            imageView.setImageResource(
                    context.getResources().getIdentifier(imgs[position], "mipmap", context.getPackageName()));
            if (imgs.length >= 1) {
                //imageView.setOnClickListener(v -> initImgSuper(imageView));
            }
        } else { // video
            if (wisePlayer != null) {
                surfaceView.setVisibility(View.VISIBLE);
                rlVideo.setVisibility(View.VISIBLE);
                notice.setVisibility(View.GONE);
                SurfaceHolder surfaceHolder = surfaceView.getHolder();
                surfaceHolder.addCallback(this);
                productRl.setVisibility(View.GONE);
                if (initVideoInterface != null) {
                    initVideoInterface.initVideo(videoUrl, surfaceView);
                }
            }else {
                notice.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initPlayView(View view) {
        surfaceView = view.findViewById(R.id.sf_video);
        currentTimeTv = view.findViewById(R.id.current_time_tv);
        totalTimeTv = view.findViewById(R.id.total_time_tv);
        playImg = view.findViewById(R.id.play_btn);
        seekBar = view.findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        playImg.setOnClickListener(v -> changePlayState());

    }

    @Override
    public int getCount() {
        return imgs == null ? 0 : (imgs.length + (isHasVideo ? 1 : 0));
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // super.destroyItem(container, position, object);
    }

    public interface InitVideoInterface {
        /**
         * This function is used to initial the video kit.
         *
         * @param videoUrl    The video link url.
         * @param surfaceView The interface which is used to play the video.
         */
        void initVideo(String videoUrl, SurfaceView surfaceView);
    }

    public void setInitVideoInterface(InitVideoInterface initVideoInterface) {
        this.initVideoInterface = initVideoInterface;
    }

    public boolean isHasVideo() {
        return isHasVideo;
    }

    public int getVideoPosition() {
        return videoPosition;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (wisePlayer != null) {
            if (surfaceView != null) {
                wisePlayer.setView(surfaceView);
            }
            if (isSuspend) {
                isSuspend = false;
                wisePlayer.resume(PlayerConstants.ResumeType.KEEP);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSuspend = true;
        isPlaying = false;
        playImg.setImageResource(R.drawable.ic_play);
        if (wisePlayer != null) {
            wisePlayer.suspend();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isUserTrackingTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isUserTrackingTouch = false;
        wisePlayer.seek(seekBar.getProgress());
        updateViewHandler.sendEmptyMessage(Constants.PLAYING);
    }

    private Handler updateViewHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == Constants.PLAYING) {
                if (!isUserTrackingTouch) {
                    updatePlayProgressView(wisePlayer.getCurrentTime(), wisePlayer.getBufferTime());
                    updateViewHandler.sendEmptyMessageDelayed(Constants.PLAYING, Constants.DELAY_MILLIS_500);
                }
            }
            return false;
        }
    });

    public void updatePlayView(WisePlayer wisePlayer) {
        if (wisePlayer != null) {
            isPlaying = true;
            int totalTime = wisePlayer.getDuration();
            seekBar.setMax(totalTime);
            totalTimeTv.setText(TimeUtil.formatLongToTimeStr(totalTime));
            currentTimeTv.setText(TimeUtil.formatLongToTimeStr(0));
            updateViewHandler.sendEmptyMessageDelayed(Constants.PLAYING, Constants.DELAY_MILLIS_500);
            seekBar.setProgress(0);
            playImg.setImageResource(R.drawable.ic_pause);
        }
    }

    public void updatePlayProgressView(int progress, int bufferPosition) {
        seekBar.setProgress(progress);
        seekBar.setSecondaryProgress(bufferPosition);
        currentTimeTv.setText(TimeUtil.formatLongToTimeStr(progress));
    }

    private void changePlayState() {
        if (isPlaying) {
            wisePlayer.pause();
            isPlaying = false;
            updateViewHandler.removeCallbacksAndMessages(null);
            playImg.setImageResource(R.drawable.ic_play);
        } else {
            wisePlayer.start();
            isPlaying = true;
            updateViewHandler.sendEmptyMessage(Constants.PLAYING);
            playImg.setImageResource(R.drawable.ic_pause);
        }
    }

    public void updatePlayCompleteView() {
        playImg.setImageResource(R.drawable.ic_play);
        isPlaying = false;
        wisePlayer.reset();
        wisePlayer.setPlayUrl(videoUrl);
        seekBar.setProgress(0);
        updateViewHandler.removeCallbacksAndMessages(null);
    }

    public void removeUpdateViewHandler() {
        if (updateViewHandler != null) {
            updateViewHandler.removeCallbacksAndMessages(null);
            updateViewHandler = null;
        }
    }

    private void initImgSuper(ImageView imageView) {
        if (null == imageView) {
            return;
        }
        Bitmap srcBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        if (srcBitmap != null) {
            MLImageSuperResolutionAnalyzerSetting setting = new MLImageSuperResolutionAnalyzerSetting.Factory()
                    .setScale(MLImageSuperResolutionAnalyzerSetting.ISR_SCALE_3X)
                    .create();
            MLImageSuperResolutionAnalyzer analyzer = MLImageSuperResolutionAnalyzerFactory.getInstance()
                    .getImageSuperResolutionAnalyzer(setting);

            MLFrame frame = MLFrame.fromBitmap(srcBitmap);
            Task<MLImageSuperResolutionResult> task = analyzer.asyncAnalyseFrame(frame);
            task.addOnSuccessListener(new OnSuccessListener<MLImageSuperResolutionResult>() {
                public void onSuccess(MLImageSuperResolutionResult result) {
                    // Recognition success.
                    initImgView(result);
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception e) {
                    // Recognition failure.
                    Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void initImgView(MLImageSuperResolutionResult result) {
        FrameLayout frameLayout = ((ProductActivity) context).findViewById(R.id.fl_img_super);
        frameLayout.setVisibility(View.VISIBLE);
        final View view = LayoutInflater.from(context).inflate(R.layout.view_img, null);
        ((ImageView) view.findViewById(R.id.view_img)).setImageBitmap(result.getBitmap());
        view.setOnClickListener(v -> frameLayout.setVisibility(View.GONE));
        frameLayout.addView(view);
    }
}
