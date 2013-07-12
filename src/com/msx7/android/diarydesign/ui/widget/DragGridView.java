package com.msx7.android.diarydesign.ui.widget;

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
    DragAdapter mAdapter;
    RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    protected Rect mRect = new Rect();

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

    public void setAdapter(DragAdapter adapter) {
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
            if (Math.abs(deltaX) < 10 || Math.abs(deltaY) < 10)
                break;
            if (mDragIndex != INVALID) {
                Rect _rect = new Rect();
                View view = getChildAt(mDragIndex);
                view.getHitRect(_rect);
                getChildAt(mDragIndex).layout(_rect.left - deltaX, _rect.top - deltaY, _rect.right - deltaX, _rect.bottom - deltaY);
                mTargetIndex = getTargetFromCoor((int) ev.getX(), (int) ev.getY());
                if (mTargetIndex != INVALID && mTargetIndex != mDragIndex) {
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
                Animation animation = generateAniamtion(rect.left, mRect.left, rect.top, mRect.top);
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
            mAdapter.insertPostion(getPositionForView(getChildAt(mDragIndex)), getPositionForView(getChildAt(mTargetIndex)));
            mAdapter.notifyDataSetChanged();
            mTargetIndex = INVALID;
            mDragIndex = INVALID;
            mDragState = INVALID;
        }
    };

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mDragIndex = getIndexFromCoor();
        if (mDragIndex != INVALID) {
            mDragState = DRAG_START;
            getChildAt(mDragIndex).getHitRect(mRect);
            Log.d("MSG", "mDragIndex-------->" + mDragIndex);
            return true;
        }
        return false;
    }

    public int getIndexFromCoor() {
        int _length = getChildCount();
        Rect _rect = new Rect();
        for (int i = 0; i < _length; i++) {
            getChildAt(i).getHitRect(_rect);
            if (_rect.contains(mLastX, mLastY))
                return i;
        }
        return INVALID;
    }

    public int getTargetFromCoor(int x, int y) {
        int _length = getChildCount();
        Rect _rect = new Rect();
        for (int i = 0; i < _length; i++) {
            getChildAt(i).getHitRect(_rect);
            if (_rect.contains(x, y))
                return i;
        }
        return INVALID;
    }

    private void grapAnimation(int target) {
        Rect tmp = new Rect();
        getChildAt(target).getHitRect(tmp);
        if (target > mDragIndex) {
            for (int i = target; i > mDragIndex; i--) {
                Rect _rect = new Rect();
                Rect rect = new Rect();
                getChildAt(i).getHitRect(rect);
                if (i - 1 == mDragIndex) {
                    _rect=new Rect(mRect);
                } else
                    getChildAt(i-1).getHitRect(_rect);
                Log.d("MSG", "_rect-------->" + _rect.toString());
                Log.d("MSG", "rect-------->" + rect.toString());
                Animation animation = generateAniamtion(rect.left, _rect.left, rect.top, _rect.top);
                getChildAt(i).startAnimation(animation);
            }
        } else {
            for (int i = target; i < mDragIndex; i++) {
                Rect _rect = new Rect();
                Rect rect = new Rect();
                getChildAt(i).getHitRect(rect);
                if (i + 1 == mDragIndex) {
                    _rect=new Rect(mRect);
                } else
                    getChildAt(1+i).getHitRect(_rect);
                Animation animation = generateAniamtion(rect.left, _rect.left, rect.top, _rect.top);
                getChildAt(i).startAnimation(animation);
            }
        }
        mRect = new Rect(tmp);
        // Rect rect=new Rect();
        // getChildAt(mDragIndex).getHitRect(rect);
        // getChildAt(target).getHitRect(_rect);
        // Animation animation=generateAniamtion(rect.left, _rect.left,
        // rect.top, _rect.top);
        // getChildAt(mDragIndex).startAnimation(animation);
    }

    private TranslateAnimation generateAniamtion(float fromXValue, float toXValue, float fromYValue, float toYValue) {
        TranslateAnimation mAnimation = new TranslateAnimation(Animation.ABSOLUTE, fromXValue, Animation.ABSOLUTE, toXValue, Animation.ABSOLUTE, fromYValue,
                Animation.ABSOLUTE, toYValue);
        mAnimation.setDuration(100);
        mAnimation.setFillAfter(true);
        return mAnimation;
    }

    public static interface OnDragLisenter {
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

    public static abstract class DragAdapter extends BaseAdapter implements OnDragLisenter {

    }

}
