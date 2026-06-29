package com.example.weightmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.weightmanager.data.database.AppDatabase
import com.example.weightmanager.data.network.NetworkDataSource
import com.example.weightmanager.data.repository.NutritionRepository
import com.example.weightmanager.data.repository.TargetRepository
import com.example.weightmanager.data.repository.WeightRepository
import com.example.weightmanager.datastore.UserPreferencesRepository
import com.example.weightmanager.navigation.AppNavigation
import com.example.weightmanager.ui.theme.WeightManagerTheme
import com.example.weightmanager.viewmodel.NutritionViewModel
import com.example.weightmanager.viewmodel.SettingsViewModel
import com.example.weightmanager.viewmodel.WeightViewModel

class MainActivity : ComponentActivity() {

    // 手动依赖注入
    private lateinit var appDatabase: AppDatabase
    private lateinit var weightRepository: WeightRepository
    private lateinit var targetRepository: TargetRepository
    private lateinit var nutritionRepository: NutritionRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var networkDataSource: NetworkDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 初始化数据库和依赖
        appDatabase = AppDatabase.getInstance(this)
        networkDataSource = NetworkDataSource()
        weightRepository = WeightRepository(appDatabase.weightRecordDao())
        targetRepository = TargetRepository(appDatabase.targetDao())
        nutritionRepository = NutritionRepository(networkDataSource)
        userPreferencesRepository = UserPreferencesRepository(this)

        setContent {
            WeightManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // 创建 ViewModel 实例
                    val weightViewModel = ViewModelProvider(
                        this,
                        WeightViewModel.Factory(
                            weightRepository = weightRepository,
                            targetRepository = targetRepository,
                            userPreferencesRepository = userPreferencesRepository
                        )
                    )[WeightViewModel::class.java]

                    val nutritionViewModel = ViewModelProvider(
                        this,
                        NutritionViewModel.Factory(
                            nutritionRepository = nutritionRepository,
                            userPreferencesRepository = userPreferencesRepository
                        )
                    )[NutritionViewModel::class.java]

                    val settingsViewModel = ViewModelProvider(
                        this,
                        SettingsViewModel.Factory(
                            userPreferencesRepository = userPreferencesRepository
                        )
                    )[SettingsViewModel::class.java]

                    AppNavigation(
                        navController = navController,
                        weightViewModel = weightViewModel,
                        nutritionViewModel = nutritionViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}
