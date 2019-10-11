package com.example.android.coordinatedeffort.behaviors.navbars

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isGone
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.math.max
import kotlin.math.min

class BottomNavigationBehavior(context: Context, attrs: AttributeSet) : NavigationBarsBehavior<BottomNavigationView>(context, attrs) {

    private var animator: ObjectAnimator? = null

    override fun onDependentViewChanged(coordinator: CoordinatorLayout, child: BottomNavigationView, dependency: View): Boolean =
            if (!getTopNavBar(coordinator).isGone) {
                child.translationY = max(0f, min(child.height.toFloat(), -dependency.translationY))
                true
            } else false

    override fun onNestedPreScroll(coordinator: CoordinatorLayout, child: BottomNavigationView, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (isTargetInScreenBounds(target, coordinator)
                && getBottomNavBar(coordinator).translationY.toInt() == 0) return

        child.translationY = max(0f, min(child.height.toFloat(), child.translationY + dy))
    }

    override fun onAfterStopNestedScroll(coordinator: CoordinatorLayout, child: BottomNavigationView, target: View) {
        if (!getTopNavBar(coordinator).isGone) return
        if (child.translationY <= 0F || child.translationY >= child.height) return

        val value = if (child.translationY < child.height / 2) 0F else child.height.toFloat()
        animateSettle(child, value)
    }

    private fun animateSettle(view: View, value: Float) {
        animator?.cancel()
        animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, value).apply {
            duration = SETTLE_DURATION
            start()
        }
    }

    override fun showNavigationBars(coordinator: CoordinatorLayout) {
        if (!getTopNavBar(coordinator).isGone) return
        getBottomNavBar(coordinator).apply {
            animateSettle(this, 0F)
        }
    }
}
