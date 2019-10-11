package com.example.android.coordinatedeffort.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.android.coordinatedeffort.R
import com.example.android.coordinatedeffort.SimpleAdapter
import kotlinx.android.synthetic.main.fragment_overview.*

class OverviewFragment : Fragment() {
    private lateinit var buttonsLayout: FrameLayout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_overview, container, false)

        val coordinatorLayout = requireActivity().findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
        buttonsLayout = coordinatorLayout.findViewById(R.id.buttonsLayout)
        CoordinatorLayout.inflate(coordinatorLayout.context, R.layout.layout_buttons, buttonsLayout).apply { isVisible = true }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        SimpleAdapter.setAdapter(scrollView)
    }

    override fun onDestroy() {
        buttonsLayout.removeAllViews()
        buttonsLayout.isVisible = false
        super.onDestroy()
    }
}
