package com.example.android.coordinatedeffort.behaviors.navbars

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isGone
import com.google.android.material.bottomnavigation.BottomNavigationView

class ButtonsLayoutBehavior(context: Context, attrs: AttributeSet) : NavigationBarsBehavior<FrameLayout>(context, attrs) {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: FrameLayout, dependency: View): Boolean {
        return dependency is BottomNavigationView
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: FrameLayout, dependency: View): Boolean {
        return updateButtonsLayout(child, dependency)
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: FrameLayout, layoutDirection: Int): Boolean {
        updateButtonsLayout(child, getBottomNavBar(parent))
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    private fun updateButtonsLayout(child: FrameLayout, dependency: View): Boolean {
        if (!child.isGone) {
            child.translationY = dependency.translationY - dependency.height
            return true
        }
        return false
    }
}