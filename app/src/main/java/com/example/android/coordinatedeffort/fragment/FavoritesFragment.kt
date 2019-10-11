package com.example.android.coordinatedeffort.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.coordinatedeffort.R
import com.example.android.coordinatedeffort.SimpleAdapter
import kotlinx.android.synthetic.main.fragment_recycler_view.*

class FavoritesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_recycler_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.adapter = SimpleAdapter(list)
    }
}