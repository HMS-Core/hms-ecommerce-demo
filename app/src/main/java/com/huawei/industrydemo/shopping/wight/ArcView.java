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

package com.huawei.industrydemo.shopping.wight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.huawei.industrydemo.shopping.R;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/27]
 * @see []
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class ArcView extends View {
    private int mWidth;

    private int mHeight;

    private Paint mPaint;  // Painter

    private Path path = new Path(); // Used to draw curve

    public ArcView(Context context) {
        this(context, null);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setColor(getContext().getResources().getColor(R.color.arc_color));
        path.moveTo(0, 0);
        path.quadTo(mWidth >> 1, mHeight * 2, mWidth, 0);
        canvas.drawPath(path, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        }
        setMeasuredDimension(mWidth, mHeight);
    }
}
