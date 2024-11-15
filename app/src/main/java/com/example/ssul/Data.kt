package com.example.ssul

data class StoreItem(
    val storeId: Int,                           // Store ID
    val storeImage: Int,                        // Store 이미지 리소스 ID
    val storeText: String,                      // Store 이름
    var isFavorite: Boolean,                    // 즐겨찾기 상태
    val locationText: String,                   // 위치 텍스트
    val isFilterGroupChecked: Boolean,          // group 필터 선택 여부
    val isFilterDateChecked: Boolean,           // date 필터 선택 여부
    val isFilterEfficiencyChecked: Boolean,     // efficiency 필터 선택 여부
    val isFilterPartnerChecked: Boolean         // partner 필터 선택 여부
)