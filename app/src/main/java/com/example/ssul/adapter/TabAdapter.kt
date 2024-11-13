package com.example.ssul.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ssul.FavoritesFragment
import com.example.ssul.HomeFragment
import com.example.ssul.MapFragment

class TabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragmentList = listOf(
        FavoritesFragment(),
        HomeFragment(),
        MapFragment()
    )

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemCount(): Int = fragmentList.size
}