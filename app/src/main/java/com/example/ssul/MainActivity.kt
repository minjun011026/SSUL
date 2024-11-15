package com.example.ssul

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.ssul.adapter.TabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupViews()
        viewPager.adapter = TabAdapter(this)
        viewPager.setCurrentItem(1, false) // 메인 화면 설정(홈)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.customView = createTabView("즐겨찾기", R.drawable.ic_favorites)
                1 -> tab.customView = createTabView("홈", R.drawable.ic_home)
                2 -> tab.customView = createTabView("지도", R.drawable.ic_map)
            }
        }.attach()
    }

    private fun setupViews() {
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
    }

    // 탭바 UI 설정
    private fun createTabView(title: String, icon: Int): View {
        val view = LayoutInflater.from(this).inflate(R.layout.tab_item, null)
        val tabIcon = view.findViewById<ImageView>(R.id.tab_icon)
        val tabText = view.findViewById<TextView>(R.id.tab_text)

        tabIcon.setImageResource(icon)
        tabText.text = title
        return view
    }

    // 프래그먼트 갱신 메서드
    fun refreshFragment(position: Int) {
        viewPager.adapter?.notifyItemChanged(position)
    }
}