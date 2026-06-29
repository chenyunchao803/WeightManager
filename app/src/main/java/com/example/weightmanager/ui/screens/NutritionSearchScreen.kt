package com.example.weightmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weightmanager.data.network.dto.NutritionInfo
import com.example.weightmanager.navigation.Routes
import com.example.weightmanager.ui.components.EmptyState
import com.example.weightmanager.ui.components.ErrorState
import com.example.weightmanager.ui.components.LoadingState
import com.example.weightmanager.ui.theme.Orange40
import com.example.weightmanager.ui.theme.Blue40
import com.example.weightmanager.ui.theme.Yellow40
import com.example.weightmanager.viewmodel.NutritionSearchUiState
import com.example.weightmanager.viewmodel.NutritionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionSearchScreen(
    navController: NavHostController,
    nutritionViewModel: NutritionViewModel
) {
    val searchUiState by nutritionViewModel.searchUiState.collectAsState()
    val searchQuery by nutritionViewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "食品营养查询",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    icon = { Icon(androidx.compose.material.icons.Icons.Outlined.MonitorWeight, contentDescription = "记录") },
                    label = { Text("记录") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Routes.STATS) },
                    icon = { Icon(androidx.compose.material.icons.Icons.Outlined.BarChart, contentDescription = "统计") },
                    label = { Text("统计") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Routes.SETTINGS) },
                    icon = { Icon(androidx.compose.material.icons.Icons.Default.Settings, contentDescription = "设置") },
                    label = { Text("设置") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 搜索框
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { nutritionViewModel.onSearchQueryChange(it) },
                label = { Text("搜索食品") },
                placeholder = { Text("输入食品名称，如：苹果、鸡胸肉...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "搜索") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 搜索提示
            if (searchUiState is NutritionSearchUiState.Idle) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Orange40.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "搜索食物了解营养信息",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "输入食物名称，获取热量、蛋白质等营养数据\n数据来源：OpenFoodFacts 开放 API",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            // 搜索结果
            when (val state = searchUiState) {
                is NutritionSearchUiState.Loading -> {
                    LoadingState("正在搜索食品营养信息...")
                }
                is NutritionSearchUiState.Success -> {
                    Text(
                        text = "找到 ${state.results.size} 个结果",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.results) { nutrition ->
                            NutritionCard(nutrition = nutrition)
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
                is NutritionSearchUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = {
                            nutritionViewModel.searchFood(searchQuery)
                        }
                    )
                }
                is NutritionSearchUiState.Idle -> {
                    // 空闲状态已在上面处理
                }
            }
        }
    }
}

@Composable
fun NutritionCard(
    nutrition: NutritionInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = nutrition.foodName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${nutrition.calories.toInt()} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    color = Orange40,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 营养成分详情
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutrientItem(label = "蛋白质", value = "${String.format("%.1f", nutrition.protein)}g", color = Blue40)
                NutrientItem(label = "脂肪", value = "${String.format("%.1f", nutrition.fat)}g", color = Yellow40)
                NutrientItem(label = "碳水", value = "${String.format("%.1f", nutrition.carbs)}g", color = Orange40)
                if (nutrition.fiber > 0) {
                    NutrientItem(label = "纤维", value = "${String.format("%.1f", nutrition.fiber)}g", color = MaterialTheme.colorScheme.primary)
                }
            }

            if (nutrition.imageUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "数据来源: OpenFoodFacts",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun NutrientItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
