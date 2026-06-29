package com.example.weightmanager.data.network

import com.example.weightmanager.data.network.dto.CalorieNinjaResponse
import com.example.weightmanager.data.network.dto.FoodSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    /**
     * OpenFoodFacts API - 搜索食品
     * 免费开放 API，无需 API Key
     * 文档: https://world.openfoodfacts.org/data
     */
    @GET("cgi/search.pl")
    suspend fun searchFood(
        @Query("search_terms") query: String,
        @Query("search_simple") simple: Int = 1,
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 10
    ): FoodSearchResponse

    /**
     * CalorieNinjas API - 营养信息查询
     * 需要 API Key，使用 X-Api-Key header
     * 文档: https://calorieninjas.com/api
     */
    @GET("v1/nutrition")
    suspend fun getNutrition(
        @Query("query") query: String
    ): CalorieNinjaResponse
}
