package com.example.android.coordinatedeffort.behaviors.navbars

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

fun View.getAbsoluteBottom(): Int {
    val location = IntArray(2)
    getLocationOnScreen(location)
    return location[1] + height
}

fun View.getAbsoluteTop(): Int {
    val location = IntArray(2)
    getLocationOnScreen(location)
    return location[1]
}

fun <B : NavigationBarsBehavior<out View>> View.getBehavior() = (layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as? B