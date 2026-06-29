package com.example.weightmanager.data.network.dto

/**
 * 健康小贴士 DTO（使用 mock 数据 / 真实 API）
 */
data class HealthTipResponse(
    val tips: List<HealthTipDto> = emptyList()
)

data class HealthTipDto(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val category: String = "",
    val imageUrl: String = ""
)
