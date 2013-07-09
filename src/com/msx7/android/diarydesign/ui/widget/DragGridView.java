package com.msx7.android.diarydesign.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class DragGridView extends GridView {
    protected int DragState;
    /**
     * 是否支持拖拽
     */
    protected boolean isDrag;

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragGridView(Context context) {
        super(context);
    }

    
    public boolean isDrag() {
        return isDrag;
    }

    public void setDrag(boolean isDrag) {
        this.isDrag = isDrag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isDrag()){
            return super.onTouchEvent(ev);
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isDrag()){
            return super.onInterceptTouchEvent(ev);
        }
        return true;
    }
    
    public static  interface onDragGridViewListener<T>{
        public void swap(int startPostion,int endPosition);
    }
    
}
