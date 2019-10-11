package com.example.android.coordinatedeffort.behaviors.navbars

import android.animation.PropertyValuesHolder
import android.animation.PropertyValuesHolder.ofFloat
import android.animation.PropertyValuesHolder.ofInt
import android.animation.ValueAnimator
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.android.coordinatedeffort.R
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val DIRECTION_UP = 1
private const val DIRECTION_DOWN = -1

private const val NAV_BAR_MARGIN_TOP = "marginTop"
private const val APP_BAR_HEIGHT = "appBarHeight"
private const val CONTENT_TRANSLATION_Y = "contentTranslationY"

class AppBarBehavior(context: Context, attrs: AttributeSet) : NavigationBarsBehavior<AppBarLayout>(context, attrs) {
    var appBarListener: AppBarListener? = null

    private var scrollDirection: Int = 0
    private var shiftDistance: Int = 0
    private var isSettling = false
    private var offset = 0
    private var wasVisible = true
    private var navBarHeight: Float
    private var shiftThreshold: Float

    init {
        val navBarMenuHeight = context.resources.getDimension(R.dimen.top_navigation_height)
        navBarHeight = navBarMenuHeight
        shiftThreshold = navBarHeight / 2
    }

    private fun resetScrollingData(dy: Int) {
        if (dy > 0 && scrollDirection != DIRECTION_UP) {
            scrollDirection = DIRECTION_UP
            shiftDistance = 0
        } else if (dy < 0 && scrollDirection != DIRECTION_DOWN) {
            scrollDirection = DIRECTION_DOWN
            shiftDistance = 0
        }
    }

    override fun onNestedPreScroll(coordinator: CoordinatorLayout, appBarLayout: AppBarLayout, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (isTargetInScreenBounds(target, coordinator)) return

        val navBar = getTopNavBar(appBarLayout)
        if (navBar.isGone) {
            return
        }

        resetScrollingData(dy)

        val toolbar = getToolbar(appBarLayout)
        val frameLayout = getFrameLayout(coordinator)

        if (scrollDirection == DIRECTION_DOWN) {
            shiftDistance = if (navBar.top + abs(dy) <= toolbar.bottom) abs(dy) else toolbar.bottom - navBar.top
            frameLayout.translationY = min(0F, frameLayout.translationY + abs(dy))
        } else {
            shiftDistance = if (navBar.bottom - dy >= toolbar.bottom) -dy else toolbar.bottom - navBar.bottom
            frameLayout.translationY = max(-navBarHeight, frameLayout.translationY - dy)
        }

        if (shiftDistance != 0) {
            consumed[1] = dy
        }
    }

    override fun onNestedScroll(parent: CoordinatorLayout, child: AppBarLayout, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        val buttonsLayout = getButtonsLayout(parent)
        val frameLayout = getFrameLayout(parent)
        val bottomNavBar = getBottomNavBar(parent)
        val scrollUp = dyConsumed > 0 || dyUnconsumed > 0
        if (buttonsLayout.isVisible) {
            if (dyUnconsumed > 0) {
                frameLayout.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    val margin = bottomMargin + dyUnconsumed
                    if (frameLayout.bottom + frameLayout.translationY > buttonsLayout.top) {
                        bottomMargin = min(0, margin)
                        consumed[1] = dyUnconsumed
                    }
                }
            } else if (!scrollUp && dyConsumed < 0) {
                frameLayout.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    val margin = bottomMargin + dyConsumed
                    if (frameLayout.bottom + frameLayout.translationY < parent.bottom) {
                        if (parent.bottom >= frameLayout.bottom + margin) {
                            bottomMargin = margin
                            consumed[1] = dyConsumed
                        } else {
                            bottomMargin = parent.bottom - frameLayout.bottom
                            consumed[1] = dyConsumed
                        }
                    }
                }
            }
        } else {
            if (dyUnconsumed > 0) {
                if (bottomNavBar.translationY.toInt() == 0 && bottomNavBar.isVisible) {
                    frameLayout.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                        val margin = bottomMargin + dyUnconsumed
                        if (target.getAbsoluteBottom() > bottomNavBar.top) {
                            val frameLayoutBottom = frameLayout.bottom + frameLayout.translationY.toInt()
                            if (frameLayoutBottom >= bottomNavBar.top) {
                                if (frameLayoutBottom - dyUnconsumed >= bottomNavBar.top) {
                                    bottomMargin = margin
                                    consumed[1] = dyUnconsumed
                                } else {
                                    val dy = frameLayoutBottom - bottomNavBar.top
                                    bottomMargin += dy
                                    consumed[1] = dy
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDependentViewChanged(coordinator: CoordinatorLayout, appBarLayout: AppBarLayout, dependency: View): Boolean {
        val navBar = getTopNavBar(appBarLayout)
        val frameLayout = getFrameLayout(coordinator)

        if (!navBar.isGone) {
            notifyOnChangingCollapsedState(frameLayout.translationY.toInt())
        }

        if (isSettling || shiftDistance == 0) return false

        var appBarHeight = appBarLayout.height + shiftDistance
        if (appBarHeight < getToolbar(appBarLayout).height) {
            appBarHeight = getToolbar(appBarLayout).height
        }
        if (appBarHeight > getToolbar(appBarLayout).height + navBar.height) {
            appBarHeight = getToolbar(appBarLayout).height + navBar.height
        }

        val navBarMarginTop = appBarHeight - navBar.height

        updateLayoutParams(appBarLayout, navBar, frameLayout, appBarHeight, navBarMarginTop, frameLayout.translationY.toInt())

        shiftDistance = 0

        return true
    }

    private fun notifyOnChangingCollapsedState(yOffset: Int) {
        if (offset != yOffset) {
            offset = yOffset
            if (offset == 0) {
                appBarListener?.onAppBarCollapsed(false)
            } else if (offset == -navBarHeight.toInt()) {
                appBarListener?.onAppBarCollapsed(true)
            }
        }
    }

    override fun onLayoutChild(coordinator: CoordinatorLayout, appBarLayout: AppBarLayout, layoutDirection: Int): Boolean {
        val frameLayout = getFrameLayout(coordinator)

        updateOnNavigationBarVisibilityChange(frameLayout, appBarLayout)

        if (frameLayout.top < appBarLayout.bottom) {
            frameLayout.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                topMargin = appBarLayout.bottom
            }
        }

        return false
    }

    private fun updateOnNavigationBarVisibilityChange(frameLayout: View, appBarLayout: AppBarLayout) {
        val toolbar = getToolbar(appBarLayout)
        val navBar = getTopNavBar(appBarLayout)

        if (toolbar.height == 0) return

        if (navBar.isGone) {
            if (wasVisible) {
                onHideNavigationBar(frameLayout, navBar, appBarLayout, toolbar)
            }
        } else {
            if (!wasVisible) {
                onShowNavigationBar(frameLayout, appBarLayout, navBar, toolbar)
            }
        }
    }

    private fun onShowNavigationBar(frameLayout: View, appBarLayout: AppBarLayout, navBar: View, toolbar: View) {
        wasVisible = true
        frameLayout.translationY = 0F
        updateLayoutParams(appBarLayout, navBar, frameLayout, toolbar.height + navBarHeight.toInt(), toolbar.bottom, 0)
    }

    private fun onHideNavigationBar(frameLayout: View, navBar: View, appBarLayout: AppBarLayout, toolbar: View) {
        wasVisible = false
        frameLayout.translationY = -navBar.height.toFloat()
        updateLayoutParams(appBarLayout, navBar, frameLayout, toolbar.height, toolbar.bottom - navBarHeight.toInt(), -navBar.height)
    }

    override fun onAfterStopNestedScroll(coordinator: CoordinatorLayout, appBarLayout: AppBarLayout, target: View) {
        val toolbar = getToolbar(appBarLayout)
        val shift = appBarLayout.height - toolbar.height
        if (shift == 0) return

        val frameLayout = getFrameLayout(coordinator)
        val navBar = getTopNavBar(appBarLayout)

        if (shift < shiftThreshold.toInt()) {
            settle(arrayOf(
                    ofInt(NAV_BAR_MARGIN_TOP, navBar.top, toolbar.bottom - navBar.height),
                    ofInt(APP_BAR_HEIGHT, appBarLayout.height, toolbar.height),
                    ofFloat(CONTENT_TRANSLATION_Y, frameLayout.translationY, -navBar.height.toFloat())
            ), appBarLayout, coordinator)
        } else if (shift < navBarHeight.toInt()) {
            settle(arrayOf(
                    ofInt(NAV_BAR_MARGIN_TOP, navBar.top, toolbar.bottom),
                    ofInt(APP_BAR_HEIGHT, appBarLayout.height, toolbar.height + navBar.height),
                    ofFloat(CONTENT_TRANSLATION_Y, frameLayout.translationY, 0F)
            ), appBarLayout, coordinator)
        }
    }

    override fun showNavigationBars(coordinator: CoordinatorLayout) {
        val appBarLayout = coordinator[0] as AppBarLayout
        val toolbar = getToolbar(appBarLayout)
        val navBar = getTopNavBar(appBarLayout)
        val frameLayout = getFrameLayout(coordinator)

        if (navBar.isGone) return
        if (navBar.top == toolbar.bottom) return

        if (appBarLayout.height < toolbar.height + navBar.height) {
            if (isBottomNavBarOnScreen(coordinator)) {
                frameLayout.translationY = 0F
                updateLayoutParams(appBarLayout, navBar, frameLayout, toolbar.height + navBar.height, toolbar.bottom, 0)
            } else {
                settle(arrayOf(
                        ofInt(NAV_BAR_MARGIN_TOP, navBar.top, toolbar.bottom),
                        ofInt(APP_BAR_HEIGHT, appBarLayout.height, toolbar.height + navBar.height),
                        ofFloat(CONTENT_TRANSLATION_Y, frameLayout.translationY, 0F)
                ), appBarLayout, coordinator)
            }
        }
    }

    private fun settle(properties: Array<PropertyValuesHolder>, appBarLayout: AppBarLayout, coordinator: CoordinatorLayout) {
        val frameLayout = getFrameLayout(coordinator)
        val navBar = getTopNavBar(appBarLayout)

        val animator = ValueAnimator.ofPropertyValuesHolder(*properties)
        animator.addUpdateListener { animation ->
            val marginTop = animation.getAnimatedValue(NAV_BAR_MARGIN_TOP) as Int
            val appBarHeight = animation.getAnimatedValue(APP_BAR_HEIGHT) as Int
            val contentTranslationY = animation.getAnimatedValue(CONTENT_TRANSLATION_Y) as Float

            frameLayout.translationY = contentTranslationY

            updateLayoutParams(appBarLayout, navBar, frameLayout, appBarHeight, marginTop, contentTranslationY.toInt())
        }
        animator.duration = SETTLE_DURATION
        animator.doOnStart { isSettling = true }
        animator.doOnEnd { isSettling = false }
        animator.start()
    }

    private fun updateLayoutParams(appBarLayout: AppBarLayout, navBar: View, frameLayout: View,
                                   appBarHeight: Int, navBarMarginTop: Int, contentMarginBottom: Int) {
        appBarLayout.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            height = appBarHeight
        }
        navBar.updateLayoutParams<FrameLayout.LayoutParams> {
            topMargin = navBarMarginTop
        }
        frameLayout.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            bottomMargin = contentMarginBottom
        }
    }

    override fun onSaveInstanceState(parent: CoordinatorLayout, child: AppBarLayout): Parcelable? {
        val parcelable = super.onSaveInstanceState(parent, child)
        return SavedState(parcelable, wasVisible)
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: AppBarLayout, state: Parcelable) {
        if (state !is SavedState) return
        wasVisible = state.wasVisible
    }

    private class SavedState(parcelable: Parcelable?, val wasVisible: Boolean = true) : View.BaseSavedState(parcelable)

    interface AppBarListener {
        fun onAppBarCollapsed(collapsed: Boolean)
    }
}
