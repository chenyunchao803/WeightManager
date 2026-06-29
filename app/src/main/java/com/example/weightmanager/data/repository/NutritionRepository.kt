package com.example.weightmanager.data.repository

import com.example.weightmanager.data.network.NetworkDataSource
import com.example.weightmanager.data.network.dto.NutritionInfo

class NutritionRepository(private val networkDataSource: NetworkDataSource) {

    /**
     * 搜索食品营养信息
     * 优先使用 OpenFoodFacts（免费），如果失败则尝试 CalorieNinjas
     */
    suspend fun searchFoodNutrition(query: String): Result<List<NutritionInfo>> {
        // 先用 OpenFoodFacts 免费 API
        val openFoodResult = networkDataSource.searchFoodFromOpenFoodFacts(query)
        if (openFoodResult.isSuccess && openFoodResult.getOrNull()?.isNotEmpty() == true) {
            return openFoodResult
        }

        // 如果 OpenFoodFacts 失败，尝试 CalorieNinjas
        return networkDataSource.getNutritionFromCalorieNinjas(query)
    }
}
