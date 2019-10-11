package com.example.android.coordinatedeffort.behaviors;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ScaleImageBehavior extends CoordinatorLayout.Behavior<ImageView> {

    private static final int DIRECTION_UP = 1;

    private static final int DIRECTION_DOWN = -1;

    private int oldDistance;

    /* Tracking direction of user motion */
    private int mScrollingDirection;

    /* Tracking last threshold crossed */
    private int mScrollTrigger;

    private boolean onReachEnd;

    private int mShiftDistance;

    //Required to instantiate as a default behavior
    public ScaleImageBehavior() {
    }

    //Required to attach behavior via XML
    public ScaleImageBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDependentViewChanged(final CoordinatorLayout parent, final ImageView child, final View dependency) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        layoutParams.height = child.getHeight() + (Math.abs(mShiftDistance) - oldDistance);
        oldDistance = Math.abs(mShiftDistance);
        child.setLayoutParams(layoutParams);
        return true;
    }

    @Override
    public boolean layoutDependsOn(final CoordinatorLayout parent, final ImageView child, final View dependency) {
        return dependency instanceof RecyclerView;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, ImageView child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, ImageView child, View target, int dx, int dy, int[] consumed) {
        if (dy > 0 && mScrollingDirection != DIRECTION_UP) {
            mScrollingDirection = DIRECTION_UP;
        } else if (dy < 0 && mScrollingDirection != DIRECTION_DOWN) {
            mScrollingDirection = DIRECTION_DOWN;
        }

        if (mScrollingDirection == DIRECTION_DOWN) {
            if (onReachEnd) {
                mShiftDistance += dy;
                target.setTranslationY(Math.abs(mShiftDistance));
                consumed[1] = dy;
            }
        } else {
            if (mShiftDistance < 0) {
                mShiftDistance += dy;
                if (mShiftDistance > 0) {
                    mShiftDistance = 0;
                }
                target.setTranslationY(Math.abs(mShiftDistance));
                consumed[1] = dy;
            }
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, ImageView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int
            dyUnconsumed) {
        if (mScrollTrigger != DIRECTION_UP) {
            mScrollTrigger = DIRECTION_UP;
        } else {
            mScrollTrigger = DIRECTION_DOWN;
        }
        onReachEnd = dyUnconsumed != 0;
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, ImageView child, View target, float velocityX, float velocityY, boolean consumed) {
        if (consumed) {
            if (velocityY > 0 && mScrollTrigger != DIRECTION_UP) {
                mScrollTrigger = DIRECTION_UP;
            } else if (velocityY < 0 && mScrollTrigger != DIRECTION_DOWN) {
                mScrollTrigger = DIRECTION_DOWN;
            }
        }

        return true;
    }

    @Override
    public boolean onNestedPreFling(final CoordinatorLayout coordinatorLayout, final ImageView child, final View target, final float velocityX, final float velocityY) {
        if (velocityY > 0 && mScrollTrigger != DIRECTION_UP) {
            mScrollTrigger = DIRECTION_UP;
        } else if (velocityY < 0 && mScrollTrigger != DIRECTION_DOWN) {
            mScrollTrigger = DIRECTION_DOWN;
        }
        return true;
    }
}
