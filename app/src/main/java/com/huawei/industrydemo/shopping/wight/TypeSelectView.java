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
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.industrydemo.shopping.constants.LogConfig;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/4/13]
 * @see com.huawei.industrydemo.shopping.page.CameraSelectActivity
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class TypeSelectView extends RecyclerView {
    /**
     * Number of items displayed on a screen. The number must be an odd number.
     */
    private int itemCount = 5;
    /**
     * Initially selected position
     */
    private int initPos = 0;

    private int deltaX;
    private WrapperAdapter wrapAdapter;
    private Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean isInit;
    private OnSelectedPositionChangedListener listener;

    /**
     * Indicates whether to trigger position change monitoring upon initial initialization.
     */
    private boolean isFirstPosChanged = true;

    /**
     * Record last selected location
     */
    private int oldSelectedPos = initPos;

    /**
     * Current Selected Location
     */
    private int selectPos = initPos;

    private Scroller mScroller;

    /**
     * Before calling the moveToPosition() method, record how many positions have been moved.
     */
    private int oldMoveX;

    private boolean isMoveFinished = true;

    public TypeSelectView(Context context) {
        super(context);
    }

    public TypeSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TypeSelectView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        mScroller = new Scroller(getContext());
        getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        () -> {
                            if (isInit) {
                                if (initPos >= adapter.getItemCount()) {
                                    initPos = adapter.getItemCount() - 1;
                                }
                                if (isFirstPosChanged && listener != null) {
                                    listener.selectedPositionChanged(initPos);
                                }
                                linearLayoutManager.scrollToPositionWithOffset(
                                        0, -initPos * (wrapAdapter.getItemWidth()));
                                isInit = false;
                            }
                        });
    }

    /**
     * Set the selected position during initialization. This method must be invoked before
     * {@link TypeSelectView#setAdapter(androidx.recyclerview.widget.RecyclerView.Adapter) }
     *
     * @param initPos Initial position. If the position exceeds the number of items, the last item is selected by default.
     */
    public void setInitPos(int initPos) {
        if (adapter != null) {
            Log.e(LogConfig.TAG, "This method should be called before setAdapter()!");
            return;
        }
        this.initPos = initPos;
        selectPos = initPos;
        oldSelectedPos = initPos;
    }

    /**
     * Sets the number of items to be displayed each time. This method must be invoked before
     * {@link TypeSelectView#setAdapter(androidx.recyclerview.widget.RecyclerView.Adapter) }
     *
     * @param itemCount The value must be an odd number. Otherwise, the value is set to a value smaller than the maximum odd number.
     */
    public void setItemCount(int itemCount) {
        if (adapter != null) {
            Log.e(LogConfig.TAG, "This method should be called before setAdapter()!");
            return;
        }
        if (itemCount % 2 == 0) {
            this.itemCount = itemCount - 1;
        } else {
            this.itemCount = itemCount;
        }
    }

    /**
     * After the item is deleted, the offset distance may need to be recalculated to ensure that the value of selectPos is correct.
     *
     * @param adapter adapte
     */
    private void correctDeltax(Adapter adapter) {
        if (adapter.getItemCount() <= selectPos) {
            deltaX -= wrapAdapter.getItemWidth() * (selectPos - adapter.getItemCount() + 1);
        }
        calculateSelectedPos();
    }

    /**
     * The selected data is changed during deletion. The method needs to be called back again.
     *
     * @param startPos startPo
     */
    private void reCallListenerWhenRemove(int startPos) {
        if (startPos <= selectPos && listener != null) {
            correctDeltax(adapter);
            listener.selectedPositionChanged(selectPos);
        } else {
            correctDeltax(adapter);
        }
    }

    /**
     * The selected data is changed when adding data. The method needs to be called back again.
     *
     * @param startPos startPos
     */
    private void reCallListenerWhenAdd(int startPos) {
        if (startPos <= selectPos && listener != null) {
            listener.selectedPositionChanged(selectPos);
        }
    }

    /**
     * Recall Method When Using Whole Refresh
     */
    private void reCallListenerWhenChanged() {
        if (listener != null) {
            listener.selectedPositionChanged(selectPos);
        }
    }

    @Override
    public void setAdapter(final Adapter adapter) {
        this.adapter = adapter;
        this.wrapAdapter = new WrapperAdapter(adapter, getContext(), itemCount);
        adapter.registerAdapterDataObserver(
                new AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        wrapAdapter.notifyDataSetChanged();
                        reCallListenerWhenChanged();
                    }

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        wrapAdapter.notifyDataSetChanged();
                        reCallListenerWhenAdd(positionStart);
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        wrapAdapter.notifyDataSetChanged();
                        reCallListenerWhenRemove(positionStart);
                    }
                });
        deltaX = 0;
        if (linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(getContext());
        }
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        super.setLayoutManager(linearLayoutManager);
        super.setAdapter(this.wrapAdapter);
        isInit = true;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (!(layout instanceof LinearLayoutManager)) {
            throw new IllegalStateException("The LayoutManager here must be LinearLayoutManager!");
        }
        this.linearLayoutManager = (LinearLayoutManager) layout;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        if (state == SCROLL_STATE_IDLE) {
            if (wrapAdapter == null) {
                return;
            }
            int itemWidth = wrapAdapter.getItemWidth();
            int headerFooterWidth = wrapAdapter.getHeaderFooterWidth();
            if (itemWidth == 0 || headerFooterWidth == 0) {
                // In this case, the adapter is not ready. Ignore this invoking.
                return;
            }
            // Exceeding the position of the previous item
            int overLastPosOffset = deltaX % itemWidth;
            if (overLastPosOffset == 0) {
                // It is in the selected position of an item and does not need to be corrected by sliding the offset.
            } else if (Math.abs(overLastPosOffset) <= itemWidth / 2) {
                scrollBy(-overLastPosOffset, 0);
            } else if (overLastPosOffset > 0) {
                scrollBy((itemWidth - overLastPosOffset), 0);
            } else {
                scrollBy(-(itemWidth + overLastPosOffset), 0);
            }
            calculateSelectedPos();
            // The notification refresh here is to redraw the previously selected position and the position just
            // selected.
            wrapAdapter.notifyItemChanged(oldSelectedPos + 1);
            wrapAdapter.notifyItemChanged(selectPos + 1);
            oldSelectedPos = selectPos;
            if (listener != null) {
                listener.selectedPositionChanged(selectPos);
            }
        }
    }

    /**
     * moveToPosition
     *
     * @param position move position
     */
    public void moveToPosition(int position) {
        if (position < 0 || position > adapter.getItemCount() - 1) {
            throw new IllegalArgumentException("Your position should be from 0 to " + (adapter.getItemCount() - 1));
        }
        oldMoveX = 0;
        isMoveFinished = false;
        int itemWidth = wrapAdapter.getItemWidth();
        if (position != selectPos) {
            int deltx = (position - selectPos) * itemWidth;
            mScroller.startScroll(getScrollX(), getScrollY(), deltx, 0);
            postInvalidate();
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            int currentX = mScroller.getCurrX() - oldMoveX;
            oldMoveX += currentX;
            scrollBy(currentX, 0);
        } else if (mScroller.isFinished()) {
            // The notification refresh here is to redraw the previously selected position and the position just
            // selected.
            if (isMoveFinished) {
                return;
            }
            wrapAdapter.notifyItemChanged(oldSelectedPos + 1);
            wrapAdapter.notifyItemChanged(selectPos + 1);
            oldSelectedPos = selectPos;
            if (listener != null) {
                listener.selectedPositionChanged(selectPos);
            }
            isMoveFinished = true;
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        deltaX += dx;
        calculateSelectedPos();
    }

    private void calculateSelectedPos() {
        int itemWidth = wrapAdapter.getItemWidth();
        if (deltaX > 0) {
            selectPos = (deltaX) / itemWidth + initPos;
        } else {
            selectPos = initPos + (deltaX) / itemWidth;
        }
    }

    class WrapperAdapter extends RecyclerView.Adapter {
        private static final int HEADER_FOOTER_TYPE = -1;
        private Context context;
        private RecyclerView.Adapter adapter;
        private int itemCount;
        private View itemView;
        /**
         * Width of head or tail
         */
        private int headerFooterWidth;

        /**
         * Width of each item
         */
        private int itemWidth;

        public WrapperAdapter(Adapter adapter, Context context, int itemCount) {
            this.adapter = adapter;
            this.context = context;
            this.itemCount = itemCount;
            if (adapter instanceof IAutoLocateHorizontalView) {
                itemView = ((IAutoLocateHorizontalView) adapter).getItemView();
            } else {
                throw new ClassCastException(
                        adapter.getClass().getSimpleName()
                                + " should implements AutoLocateHorizontalView.IAutoLocateHorizontalView !");
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HEADER_FOOTER_TYPE) {
                View view = new View(context);
                headerFooterWidth = parent.getMeasuredWidth() / 2 - (parent.getMeasuredWidth() / itemCount) / 2;
                RecyclerView.LayoutParams params =
                        new LayoutParams(headerFooterWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                view.setLayoutParams(params);
                return new HeaderFooterViewHolder(view);
            }
            ViewHolder holder = adapter.onCreateViewHolder(parent, viewType);
            itemView = ((IAutoLocateHorizontalView) adapter).getItemView();
            int width = parent.getMeasuredWidth() / itemCount;
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            if (params != null) {
                params.width = width;
                itemWidth = width;
                itemView.setLayoutParams(params);
            }
            return holder;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (!isHeaderOrFooter(position)) {
                adapter.onBindViewHolder(holder, position - 1);
                if (selectPos == position - 1) {
                    ((IAutoLocateHorizontalView) adapter).onViewSelected(true, position - 1, holder, itemWidth);
                } else {
                    ((IAutoLocateHorizontalView) adapter).onViewSelected(false, position - 1, holder, itemWidth);
                }
            }
        }

        @Override
        public int getItemCount() {
            return adapter.getItemCount() + 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == getItemCount() - 1) {
                return HEADER_FOOTER_TYPE;
            }
            return adapter.getItemViewType(position - 1);
        }

        private boolean isHeaderOrFooter(int pos) {
            if (pos == 0 || pos == getItemCount() - 1) {
                return true;
            }
            return false;
        }

        public int getHeaderFooterWidth() {
            return headerFooterWidth;
        }

        public int getItemWidth() {
            return itemWidth;
        }

        class HeaderFooterViewHolder extends RecyclerView.ViewHolder {
            HeaderFooterViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    /**
     * IAutoLocateHorizontalView
     */
    public interface IAutoLocateHorizontalView {
        /**
         * Obtains the root layout of an item.
         *
         * @return ItemView
         */
        View getItemView();

        /**
         * This callback is triggered when an item is selected. You can modify the style when the item is selected.
         *
         * @param isSelected Whether selected
         * @param pos        Position of the current view
         * @param holder     holder
         * @param itemWidth  Width of the current item.
         */
        void onViewSelected(boolean isSelected, int pos, ViewHolder holder, int itemWidth);
    }

    /***
     * Monitor when the selected location changes
     */
    public interface OnSelectedPositionChangedListener {
        /**
         * selectedPositionChanged
         *
         * @param pos position
         */
        void selectedPositionChanged(int pos);
    }

    /**
     * setOnSelectedPositionChangedListener
     *
     * @param listener listener
     */
    public void setOnSelectedPositionChangedListener(OnSelectedPositionChangedListener listener) {
        this.listener = listener;
    }
}
