package com.example.android.coordinatedeffort

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.android.coordinatedeffort.fragment.FavoritesFragment
import com.example.android.coordinatedeffort.fragment.OverviewFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_navigation_bars.*

class NavigationBarsActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_bars)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        toolbarTitle.text = getString(R.string.app_name)

        topNavBar.setOnNavigationItemSelectedListener(this)
        bottomNavBar.setOnNavigationItemSelectedListener(this)
        bottomNavBar.selectedItemId = R.id.home
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        when (item.itemId) {
            R.id.home -> {
                topNavBarParent.isVisible = true
                topNavBar.selectedItemId = R.id.favorites
            }
            R.id.overview -> {
                topNavBarParent.isVisible = false
                switchFragment(OverviewFragment())
            }
            R.id.favorites -> switchFragment(FavoritesFragment())
        }
        return true
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContent, fragment).commit()
    }
}