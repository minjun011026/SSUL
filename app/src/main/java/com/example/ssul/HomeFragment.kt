package com.example.ssul

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ssul.adapter.StoreAdapter

class HomeFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageView
    private lateinit var groupFilterButton: TextView
    private lateinit var dateFilterButton: TextView
    private lateinit var efficiencyFilterButton: TextView
    private lateinit var partnerFilterButton: TextView
    private lateinit var storeList: RecyclerView
    private lateinit var storeAdapter: StoreAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private val sampleStoreItems = listOf(
        StoreItem(
            storeId = 1,
            storeImage = R.drawable.sample_store1,
            storeText = "블루힐",
            isFavorite = false,
            locationText = "서울 동작구 사당로 14",
            isFilterGroupChecked = true,
            isFilterDateChecked = false,
            isFilterEfficiencyChecked = false,
            isFilterPartnerChecked = false
        ),
        StoreItem(
            storeId = 2,
            storeImage = R.drawable.sample_store2,
            storeText = "스팅 (BAR)",
            isFavorite = false,
            locationText = "서울 동작구 사당로 8 2층",
            isFilterGroupChecked = false,
            isFilterDateChecked = true,
            isFilterEfficiencyChecked = false,
            isFilterPartnerChecked = false
        ),
        StoreItem(
            storeId = 3,
            storeImage = R.drawable.sample_store3,
            storeText = "파동추야",
            isFavorite = false,
            locationText = "서울 동작구 상도로58번길",
            isFilterGroupChecked = true,
            isFilterDateChecked = false,
            isFilterEfficiencyChecked = true,
            isFilterPartnerChecked = true
        )
    )
    private val activeFilters = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)

        // 구현 사항
        // 1. 데이터 불러오기 -> isFavorite은 내부 저장소, 나머지는 서버
        //    ** 즐겨찾기 상태는 storeId와 isFavorite 매핑하여 저장 **
        // 2. 필터 클릭 시 현재 불러온 데이터에서 일치하는 필터만 표시 -> 선택된 필터 색상 변경
        // 3. 메인 화면에서 텍스트 필드 클릭시 검색 화면으로 전환
        // 4. 가게 클릭 시 세부 정보 화면(StoreActivity)로 이동 -> storeId만 intent에 extra로 전달

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences("favorite", Context.MODE_PRIVATE)

        // 내부 저장소에서 즐겨찾기 상태 로드
        sampleStoreItems.forEach { item ->
            item.isFavorite = sharedPreferences.getBoolean(item.storeId.toString(), false)
        }

        // 어댑터 초기화
        storeAdapter = StoreAdapter(sampleStoreItems) { storeId ->
            toggleFavorite(storeId)

            // 내부 저장소 로깅
            val allFavorites = sharedPreferences.all
            allFavorites.forEach { (key, value) ->
                if (value is Boolean) {
                    Log.d("FavoritesLog", "Store ID: $key, isFavorite: $value")
                }
            }
        }
        storeList.adapter = storeAdapter

        // toggleFilter 함수로 필터 이름과 클릭된 뷰(it) 전달
        groupFilterButton.setOnClickListener { toggleFilter("group", it as TextView) }
        dateFilterButton.setOnClickListener { toggleFilter("date", it as TextView) }
        efficiencyFilterButton.setOnClickListener { toggleFilter("efficiency", it as TextView) }
        partnerFilterButton.setOnClickListener { toggleFilter("partner", it as TextView) }
    }

    private fun setupViews(view: View) {
        searchEditText = view.findViewById(R.id.search_store_textfield)
        searchButton = view.findViewById(R.id.search_button)
        groupFilterButton = view.findViewById(R.id.filter_group_button)
        dateFilterButton = view.findViewById(R.id.filter_date_button)
        efficiencyFilterButton = view.findViewById(R.id.filter_efficiency_button)
        partnerFilterButton = view.findViewById(R.id.filter_partner_button)
        storeList = view.findViewById(R.id.store_list)
        storeList.layoutManager = LinearLayoutManager(requireContext())
    }

    // 즐겨찾기 클릭 시 내부 저장소에 상태 저장/업데이트
    private fun toggleFavorite(storeId: Int) {
        val storeItem = sampleStoreItems.find { it.storeId == storeId }
        storeItem?.let {
            it.isFavorite = !it.isFavorite
            storeAdapter.notifyDataSetChanged()

            // SharedPreferences 업데이트
            with(sharedPreferences.edit()) {
                putBoolean(storeId.toString(), it.isFavorite)
                apply()
            }

            // FavoritesFragment 갱신
            (activity as MainActivity).refreshFragment(0)
        }
    }

    // 필터 선택 시 필터 UI 변경 + 선택된 필터에 해당하는 가게 정보 표시
    private fun toggleFilter(filter: String, button: TextView) {
        // 필터 UI 변경
        if (activeFilters.contains(filter)) {
            activeFilters.remove(filter)
            button.background = ContextCompat.getDrawable(requireContext(), R.drawable.filter_non_clicked)
            button.setTextAppearance(requireContext(), R.style.filter_text_style)
        } else {
            activeFilters.add(filter)
            button.background = ContextCompat.getDrawable(requireContext(), R.drawable.filter_clicked)
            button.setTextAppearance(requireContext(), R.style.filter_selected_text_style)
        }

        // 필터링된 가게 표시
        if (activeFilters.isEmpty()) {
            storeAdapter.updateItems(sampleStoreItems)
            return
        }

        // 필터링 1 : 가게 필터에 내가 선택한 필터 중 하나라도 일치하면 출력
//        val filteredItems = sampleStoreItems.filter { item ->
//            (activeFilters.contains("group") && item.isFilterGroupChecked) ||
//                    (activeFilters.contains("date") && item.isFilterDateChecked) ||
//                    (activeFilters.contains("efficiency") && item.isFilterEfficiencyChecked) ||
//                    (activeFilters.contains("partner") && item.isFilterPartnerChecked)
//        }

        // 필터링 2 : 가게 필터에 내가 선택한 필터 중 하나라도 일치하지 않으면 출력 X
        val filteredItems = sampleStoreItems.filter { item ->
            activeFilters.all { filter ->
                when (filter) {
                    "group" -> item.isFilterGroupChecked
                    "date" -> item.isFilterDateChecked
                    "efficiency" -> item.isFilterEfficiencyChecked
                    "partner" -> item.isFilterPartnerChecked
                    else -> false
                }
            }
        }

        storeAdapter.updateItems(filteredItems)
    }

}