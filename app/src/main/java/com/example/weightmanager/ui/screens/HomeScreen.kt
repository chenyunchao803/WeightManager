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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weightmanager.navigation.Routes
import com.example.weightmanager.ui.components.EmptyState
import com.example.weightmanager.ui.components.WeightRecordCard
import com.example.weightmanager.viewmodel.WeightListUiState
import com.example.weightmanager.viewmodel.WeightViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    weightViewModel: WeightViewModel
) {
    val listUiState by weightViewModel.listUiState.collectAsState()
    val searchQuery by weightViewModel.searchQuery.collectAsState()
    val isSearching by weightViewModel.isSearching.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var searchActive by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<com.example.weightmanager.data.entity.WeightRecordEntity?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (searchActive) {
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = { weightViewModel.onSearchQueryChange(it) },
                            onSearch = { weightViewModel.onSearchQueryChange(searchQuery) },
                            expanded = false,
                            onExpandedChange = {},
                            placeholder = { Text("搜索体重记录...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "搜索")
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    weightViewModel.clearSearch()
                                    searchActive = false
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "关闭")
                                }
                            }
                        )
                    },
                    expanded = false,
                    onExpandedChange = {}
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = "体重管理助手",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    actions = {
                        IconButton(onClick = { searchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "搜索")
                        }
                        IconButton(onClick = { navController.navigate(Routes.NUTRITION_SEARCH) }) {
                            Icon(Icons.Default.RestaurantMenu, contentDescription = "食品查询")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    weightViewModel.resetAddEditState()
                    navController.navigate(Routes.addEditWeight())
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "添加记录",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Outlined.MonitorWeight, contentDescription = "记录") },
                    label = { Text("记录") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Routes.STATS) },
                    icon = { Icon(Icons.Outlined.BarChart, contentDescription = "统计") },
                    label = { Text("统计") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Routes.SETTINGS) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "设置") },
                    label = { Text("设置") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = listUiState) {
                is WeightListUiState.Loading -> {
                    com.example.weightmanager.ui.components.LoadingState("加载体重记录...")
                }
                is WeightListUiState.Success -> {
                    if (state.isEmpty) {
                        EmptyState(
                            title = "暂无体重记录",
                            subtitle = "点击右下角的 + 按钮添加第一条记录吧！"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(4.dp)) }

                            // 快速统计卡片
                            item {
                                QuickStatsCard(
                                    recordCount = state.records.size,
                                    latestWeight = state.records.firstOrNull()?.weight ?: 0.0,
                                    latestDate = state.records.firstOrNull()?.recordDate ?: ""
                                )
                            }

                            items(
                                items = state.records,
                                key = { it.id }
                            ) { record ->
                                WeightRecordCard(
                                    record = record,
                                    onEdit = {
                                        weightViewModel.resetAddEditState()
                                        navController.navigate(Routes.addEditWeight(record.id))
                                    },
                                    onDelete = {
                                        recordToDelete = record
                                        showDeleteDialog = true
                                    }
                                )
                            }

                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
                is WeightListUiState.Error -> {
                    com.example.weightmanager.ui.components.ErrorState(
                        message = state.message,
                        onRetry = { /* 重新加载 */ }
                    )
                }
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog && recordToDelete != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                recordToDelete = null
            },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条体重记录吗？此操作不可撤销。") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        recordToDelete?.let { weightViewModel.deleteRecord(it) }
                        showDeleteDialog = false
                        recordToDelete = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showDeleteDialog = false
                        recordToDelete = null
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun QuickStatsCard(
    recordCount: Int,
    latestWeight: Double,
    latestDate: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "数据概览",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$recordCount",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "总记录",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${latestWeight}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "最新体重 (kg)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = latestDate.takeLast(5),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "最近记录",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
