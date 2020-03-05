package com.example.myapplication.Adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPagerNoSwip extends ViewPager {
    private boolean enabled;
    public CustomViewPagerNoSwip(Context context, AttributeSet attrs){
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){

        return false;
    }

    public void setPagingEnabled(boolean enabled){
        this.enabled = enabled;
    }
}
