package com.msx7.android.diarydesign.ui.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;

public class DragGridView extends GridView implements android.widget.AdapterView.OnItemLongClickListener {
    public static final int INVALID = -1;
    public static final int DRAG_START = 1;
    /**
     * 是否支持拖拽
     */
    protected boolean isDrag;
    protected int mDragState = INVALID;
    protected int mDragIndex = INVALID;
    protected int mLastX = INVALID;
    protected int mLastY = INVALID;
    protected int mTargetIndex = INVALID;
    protected int mDragPosition = INVALID;
    ArrayList<Rect> mRects = new ArrayList<Rect>();
    Rect rect = new Rect();
    DragAdapter<?> mAdapter;
    int mAnimDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragGridView(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mDragIndex != INVALID)
            getChildAt(mDragIndex).layout(rect.left, rect.top, rect.right, rect.bottom);
    }

    private boolean _init = false;

    /**
     * 加入{@linkplain #_init}因为不同版本的sdk，不同的构造方法可能套用
     */
    private void init() {
        if (_init)
            return;
        _init = true;
        setOnItemLongClickListener(this);
        setNumColumns(4);
    }

    public void setAdapter(DragAdapter<?> adapter) {
        super.setAdapter(adapter);
        mAdapter = adapter;
    }

    public boolean isDrag() {
        return isDrag;
    }

    public void setDrag(boolean isDrag) {
        this.isDrag = isDrag;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            mLastX = (int) ev.getX();
            mLastY = (int) ev.getY();
        }
        return super.onInterceptTouchEvent(ev);
    }

    long lastAnim;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isDrag() || mDragIndex == INVALID) {
            return super.onTouchEvent(ev);
        }
        int _action = ev.getAction();
        switch (_action & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            mLastX = (int) ev.getX();
            mLastY = (int) ev.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            int deltaY = mLastY - (int) ev.getY();
            int deltaX = mLastX - (int) ev.getX();
            if (Math.abs(deltaX) < 10 && Math.abs(deltaY) < 10)
                break;
            if (mDragIndex != INVALID) {
                Rect _rect = new Rect();
                View view = getChildAt(mDragIndex);
                view.getHitRect(_rect);
                getChildAt(mDragIndex).layout(_rect.left - deltaX, _rect.top - deltaY, _rect.right - deltaX, _rect.bottom - deltaY);
                getChildAt(mDragIndex).getHitRect(rect);
                int tmpTarget = getTargetFromCoor((int) ev.getX(), (int) ev.getY());
                if (tmpTarget != INVALID && tmpTarget != mDragIndex && tmpTarget != mTargetIndex
                        && System.currentTimeMillis() - lastAnim > mAnimDuration) {
                    mTargetIndex = tmpTarget;
                    lastAnim = System.currentTimeMillis();
                    grapAnimation(mTargetIndex);
                }
            }
            mLastX = (int) ev.getX();
            mLastY = (int) ev.getY();
            break;
        case MotionEvent.ACTION_UP:
            mTargetIndex = getTargetFromCoor((int) ev.getX(), (int) ev.getY());
            if (mTargetIndex != INVALID && mTargetIndex != mDragIndex) {
                grapAnimation(mTargetIndex);
            }
        case MotionEvent.ACTION_OUTSIDE:
        case MotionEvent.ACTION_CANCEL:
            if (mTargetIndex != INVALID && mTargetIndex != mDragIndex) {
                Rect rect = new Rect();
                getChildAt(mDragIndex).getHitRect(rect);
                Animation animation = generateAniamtion(rect.left, mRects.get(mTargetIndex).left, rect.top, mRects.get(mTargetIndex).top);
                animation.setAnimationListener(mAnimationListener);
                getChildAt(mDragIndex).startAnimation(animation);
            } else {
                mTargetIndex = INVALID;
                mDragIndex = INVALID;
                mDragState = INVALID;
                requestLayout();
            }
            break;
        }
        return true;
    }

    AnimationListener mAnimationListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mAdapter.insertPostion(mDragPosition, mDragPosition + mTargetIndex - mDragIndex);
            mAdapter.notifyDataSetChanged();
            mTargetIndex = INVALID;
            mDragIndex = INVALID;
            mDragPosition = mDragState = INVALID;
            invalidate();
        }
    };
    AnimationListener mTMPAnimationListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mDragPosition + mTargetIndex - mDragIndex < 0) {
                return;
            }
            mAdapter.insertPostion(mDragPosition, mDragPosition + mTargetIndex - mDragIndex);
            mAdapter.notifyDataSetChanged();
            mDragPosition = mDragPosition + mTargetIndex - mDragIndex;
            mDragIndex = mTargetIndex;
        }
    };

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mRects.clear();
        mDragIndex = getIndexFromCoor();
        if (mDragIndex != INVALID) {
            mDragState = DRAG_START;
            mDragPosition = position;
            return true;
        }
        return false;
    }

    public int getIndexFromCoor() {
        int _postion = INVALID;
        int _length = getChildCount();
        for (int i = 0; i < _length; i++) {
            Rect _rect = new Rect();
            getChildAt(i).getHitRect(_rect);
            mRects.add(_rect);
            if (_rect.contains(mLastX, mLastY))
                _postion = i;
        }
        return _postion;
    }

    public int getTargetFromCoor(int x, int y) {
        int _length = mRects.size();
        for (int i = 0; i < _length; i++) {
            if (mRects.get(i).contains(x, y))
                return i;
        }
        return INVALID;
    }

    private void grapAnimation(int target) {
        if (target > mDragIndex) {
            for (int i = target; i > mDragIndex; i--) {
                Animation animation = generateAniamtion(mRects.get(i).left, mRects.get(i - 1).left, mRects.get(i).top, mRects.get(i - 1).top);
                if (i - 1 == mDragIndex) {
                    animation.setAnimationListener(mTMPAnimationListener);
                }
                getChildAt(i).startAnimation(animation);
            }
        } else {
            for (int i = target; i < mDragIndex; i++) {
                Animation animation = generateAniamtion(mRects.get(i).left, mRects.get(i + 1).left, mRects.get(i).top, mRects.get(i + 1).top);
                if (i + 1 == mDragIndex) {
                    animation.setAnimationListener(mTMPAnimationListener);
                }
                getChildAt(i).startAnimation(animation);
            }
        }
    }

    private TranslateAnimation generateAniamtion(float fromXValue, float toXValue, float fromYValue, float toYValue) {
        TranslateAnimation mAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, toXValue - fromXValue, Animation.ABSOLUTE,
                0, Animation.ABSOLUTE, toYValue - fromYValue);
        mAnimation.setDuration(mAnimDuration);
        mAnimation.setFillEnabled(true);
        mAnimation.setFillAfter(true);
        return mAnimation;
    }

    public static interface OnDragLisenter<T> {
        public T[] getData();

        public void setData(T[] arr);

        public void enableEdit(boolean isEdit);

        /**
         * 
         * @param dragPosition
         *            拖动item的序号
         * @param insertPosition
         *            插入的位置的序号
         */
        public void insertPostion(int dragPosition, int insertPosition);
    }

    public static abstract class DragAdapter<T> extends BaseAdapter implements OnDragLisenter<T> {

        @Override
        public void insertPostion(int dragPosition, int insertPosition) {
            T[] arr = getData();
            T[] arr1 = arr.clone();
            if (dragPosition > insertPosition) {
                System.arraycopy(arr, 0, arr1, 0, insertPosition);
                System.arraycopy(arr, insertPosition, arr1, insertPosition + 1, dragPosition - insertPosition);
                if (dragPosition + 1 < arr1.length)
                    System.arraycopy(arr, dragPosition + 1, arr1, dragPosition + 1, arr1.length - 1 - dragPosition);
                arr1[insertPosition] = arr[dragPosition];
            } else {
                System.arraycopy(arr, 0, arr1, 0, dragPosition);
                System.arraycopy(arr, dragPosition + 1, arr1, dragPosition, insertPosition - dragPosition);
                if (dragPosition + 1 < arr1.length)
                    System.arraycopy(arr, insertPosition + 1, arr1, insertPosition + 1, arr1.length - 1 - insertPosition);
                arr1[insertPosition] = arr[dragPosition];
            }
            setData(arr1);
            notifyDataSetChanged();
        }

    }

}
