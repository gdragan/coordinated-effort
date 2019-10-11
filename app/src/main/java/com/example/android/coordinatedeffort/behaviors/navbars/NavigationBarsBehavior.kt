package com.example.android.coordinatedeffort.behaviors.navbars

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
import androidx.core.view.ScrollingView
import androidx.core.view.ViewCompat
import androidx.core.view.get
import com.example.android.coordinatedeffort.R
import com.google.android.material.appbar.AppBarLayout

const val SETTLE_DURATION = 250L

open class NavigationBarsBehavior<V : View>(context: Context, attrs: AttributeSet) : Behavior<V>(context, attrs) {
    private var onFling = false

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int): Boolean =
            axes == ViewCompat.SCROLL_AXIS_VERTICAL

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean =
            dependency is FrameLayout && dependency.id == R.id.fragmentContent

    override fun onNestedPreFling(parent: CoordinatorLayout, child: V, target: View, velocityX: Float, velocityY: Float): Boolean {
        onFling = true
        return super.onNestedPreFling(parent, child, target, velocityX, velocityY)
    }

    override fun onStopNestedScroll(coordinator: CoordinatorLayout, child: V, target: View, type: Int) {
        if (onFling) {
            onFling = false
            return
        }

        onAfterStopNestedScroll(coordinator, child, target)
    }

    protected open fun onAfterStopNestedScroll(coordinator: CoordinatorLayout, child: V, target: View) = Unit

    open fun showNavigationBars(coordinator: CoordinatorLayout) = Unit

    fun isBottomNavBarOnScreen(coordinator: CoordinatorLayout) = getBottomNavBar(coordinator).translationY == 0F

    fun isTargetInScreenBounds(target: View, coordinator: CoordinatorLayout): Boolean {
        val scrollingView = findNestedScrollingView(target) ?: return true
        return scrollingView.getAbsoluteBottom() < coordinator.bottom
    }

    private fun findNestedScrollingView(target: View): View? {
        if (target is ScrollingView) return target
        if (target !is ViewGroup) return null

        var index = 0
        var found: View? = null

        while (index < target.childCount) {
            val childAt = target.getChildAt(index)
            found = findNestedScrollingView(childAt)

            if (found is ScrollingView) {
                return found
            } else {
                index++
            }
        }

        return found
    }

    fun getFrameLayout(parent: CoordinatorLayout) = parent[2]

    fun getButtonsLayout(parent: CoordinatorLayout) = parent[3]

    fun getBottomNavBar(parent: CoordinatorLayout) = parent[1]

    fun getTopNavBar(child: AppBarLayout) = (child[0] as ViewGroup)[0]

    fun getTopNavBar(coordinator: CoordinatorLayout) = ((coordinator[0] as ViewGroup)[0] as ViewGroup)[0]

    fun getToolbar(child: AppBarLayout) = (child[0] as ViewGroup)[1]
}