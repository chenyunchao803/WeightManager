package com.example.weightmanager.data.network.dto

import com.google.gson.annotations.SerializedName

/**
 * OpenFoodFacts API 返回的食品营养信息 DTO
 */
data class FoodSearchResponse(
    @SerializedName("products")
    val products: List<FoodProductDto> = emptyList(),

    @SerializedName("count")
    val count: Int = 0
)

data class FoodProductDto(
    @SerializedName("product_name")
    val productName: String = "",

    @SerializedName("brands")
    val brands: String = "",

    @SerializedName("image_url")
    val imageUrl: String = "",

    @SerializedName("nutriments")
    val nutriments: NutrimentsDto? = null,

    @SerializedName("nutriscore_grade")
    val nutriScoreGrade: String = ""
)

data class NutrimentsDto(
    @SerializedName("energy-kcal_100g")
    val energyKcal100g: Double? = null,

    @SerializedName("proteins_100g")
    val proteins100g: Double? = null,

    @SerializedName("fat_100g")
    val fat100g: Double? = null,

    @SerializedName("carbohydrates_100g")
    val carbohydrates100g: Double? = null,

    @SerializedName("fiber_100g")
    val fiber100g: Double? = null,

    @SerializedName("sugars_100g")
    val sugars100g: Double? = null
)

/**
 * CalorieNinjas API 返回的食品营养信息 DTO
 */
data class CalorieNinjaResponse(
    @SerializedName("items")
    val items: List<CalorieNinjaItem> = emptyList()
)

data class CalorieNinjaItem(
    @SerializedName("name")
    val name: String = "",

    @SerializedName("calories")
    val calories: Double = 0.0,

    @SerializedName("serving_size_g")
    val servingSizeG: Double = 100.0,

    @SerializedName("fat_total_g")
    val fatTotalG: Double = 0.0,

    @SerializedName("fat_saturated_g")
    val fatSaturatedG: Double = 0.0,

    @SerializedName("protein_g")
    val proteinG: Double = 0.0,

    @SerializedName("sodium_mg")
    val sodiumMg: Double = 0.0,

    @SerializedName("potassium_mg")
    val potassiumMg: Double = 0.0,

    @SerializedName("cholesterol_mg")
    val cholesterolMg: Double = 0.0,

    @SerializedName("carbohydrates_total_g")
    val carbohydratesTotalG: Double = 0.0,

    @SerializedName("fiber_g")
    val fiberG: Double = 0.0,

    @SerializedName("sugar_g")
    val sugarG: Double = 0.0
)

/**
 * 统一的营养信息模型，用于 App 内部展示
 */
data class NutritionInfo(
    val foodName: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val fiber: Double = 0.0,
    val servingSize: Double = 100.0,
    val imageUrl: String = ""
)
