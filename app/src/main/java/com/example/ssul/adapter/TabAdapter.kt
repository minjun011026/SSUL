package com.example.ssul.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ssul.FavoritesFragment
import com.example.ssul.HomeFragment
import com.example.ssul.MapFragment

class TabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentCreators: Map<Int, () -> Fragment> = mapOf(
        0 to { FavoritesFragment() },
        1 to { HomeFragment() },
        2 to { MapFragment() }
    )

    override fun createFragment(position: Int): Fragment {
        return fragmentCreators[position]?.invoke() ?: throw IllegalStateException("Invalid position")
    }

    override fun getItemCount(): Int = fragmentCreators.size
}