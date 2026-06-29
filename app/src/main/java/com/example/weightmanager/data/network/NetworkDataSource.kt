package com.example.weightmanager.data.network

import com.example.weightmanager.data.network.dto.CalorieNinjaResponse
import com.example.weightmanager.data.network.dto.FoodSearchResponse
import com.example.weightmanager.data.network.dto.NutritionInfo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkDataSource {

    // OpenFoodFacts 免费 API - 不需要 API Key
    private val openFoodFactsApi: ApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // CalorieNinjas API - 需要 API Key
    private val calorieNinjasApi: ApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Api-Key", CALORIE_NINJAS_API_KEY)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.calorieninjas.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * 从 OpenFoodFacts 搜索食品
     * 免费 API，用于搜索食品基本信息
     */
    suspend fun searchFoodFromOpenFoodFacts(query: String): Result<List<NutritionInfo>> {
        return try {
            val response = openFoodFactsApi.searchFood(query = query)
            val nutritionList = response.products
                .filter { it.productName.isNotBlank() }
                .map { product ->
                    NutritionInfo(
                        foodName = product.productName,
                        calories = product.nutriments?.energyKcal100g ?: 0.0,
                        protein = product.nutriments?.proteins100g ?: 0.0,
                        fat = product.nutriments?.fat100g ?: 0.0,
                        carbs = product.nutriments?.carbohydrates100g ?: 0.0,
                        fiber = product.nutriments?.fiber100g ?: 0.0,
                        imageUrl = product.imageUrl
                    )
                }
            if (nutritionList.isEmpty()) {
                Result.failure(Exception("未找到相关食品信息"))
            } else {
                Result.success(nutritionList)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 从 CalorieNinjas 获取营养信息
     */
    suspend fun getNutritionFromCalorieNinjas(query: String): Result<List<NutritionInfo>> {
        return try {
            val response = calorieNinjasApi.getNutrition(query = query)
            val nutritionList = response.items.map { item ->
                NutritionInfo(
                    foodName = item.name,
                    calories = item.calories,
                    protein = item.proteinG,
                    fat = item.fatTotalG,
                    carbs = item.carbohydratesTotalG,
                    fiber = item.fiberG,
                    servingSize = item.servingSizeG
                )
            }
            if (nutritionList.isEmpty()) {
                Result.failure(Exception("未找到相关食品营养信息"))
            } else {
                Result.success(nutritionList)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        // CalorieNinjas 免费 API Key（演示用，有每日限额）
        private const val CALORIE_NINJAS_API_KEY = "YOUR_API_KEY_HERE"
    }
}
