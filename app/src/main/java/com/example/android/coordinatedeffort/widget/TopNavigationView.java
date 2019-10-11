package com.example.android.coordinatedeffort.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;

import com.example.android.coordinatedeffort.R;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Field;

public class TopNavigationView extends BottomNavigationView {

    private final static String TAG = TopNavigationView.class.getName();

    private Resources resources = getResources();
    final int itemDimHeight = resources.getDimensionPixelSize(R.dimen.top_navigation_height);

    public TopNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("RestrictedApi")
    private void init() {
        Field itemHeight;
        Field menuView;
        Field bottomNavigationPresenter;

        BottomNavigationMenuView bottomNavigationMenuView = null;
        try {
            menuView = BottomNavigationView.class.getDeclaredField("menuView");
            menuView.setAccessible(true);
            bottomNavigationMenuView = (BottomNavigationMenuView) menuView.get(this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Log.e(TAG, "Field does not exist or is not accessible: " + e);
        }

        @SuppressLint("RestrictedApi") BottomNavigationPresenter presenter = null;

        try {
            bottomNavigationPresenter = BottomNavigationView.class.getDeclaredField("presenter");
            bottomNavigationPresenter.setAccessible(true);

            itemHeight = BottomNavigationMenuView.class.getDeclaredField("itemHeight");
            itemHeight.setAccessible(true);
            itemHeight.set(bottomNavigationMenuView, itemDimHeight);

            presenter = (BottomNavigationPresenter) bottomNavigationPresenter.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Field does not exist or is not accessible: " + e);
        }
        if (bottomNavigationMenuView != null) {
            bottomNavigationMenuView.setPresenter(presenter);
        }

        try {
            menuView = BottomNavigationView.class.getDeclaredField("menuView");
            menuView.setAccessible(true);
            menuView.set(this, bottomNavigationMenuView);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Field does not exist or is not accessible: " + e);
        }
    }

}
