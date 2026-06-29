package com.example.weightmanager.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weightmanager.navigation.Routes
import com.example.weightmanager.ui.components.LoadingState
import com.example.weightmanager.ui.theme.Orange40
import com.example.weightmanager.ui.theme.Red40
import com.example.weightmanager.ui.theme.Blue40
import com.example.weightmanager.ui.theme.Yellow40
import com.example.weightmanager.ui.theme.Purple40
import com.example.weightmanager.viewmodel.WeightViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    navController: NavHostController,
    weightViewModel: WeightViewModel
) {
    val statsState by weightViewModel.statsUiState.collectAsState()

    LaunchedEffect(Unit) {
        weightViewModel.loadStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "数据统计",
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
                    icon = { Icon(Icons.Outlined.MonitorWeight, contentDescription = "记录") },
                    label = { Text("记录") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Outlined.BarChart, contentDescription = "统计") },
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
        if (statsState.isLoading) {
            LoadingState("加载统计数据...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 统计概览
                Text(
                    text = "近一周统计",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = weightViewModel.getDateRangeLabel(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 统计卡片网格
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "记录数",
                        value = "${statsState.recordCount}",
                        unit = "条",
                        icon = Icons.Outlined.BarChart,
                        color = Blue40,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "平均体重",
                        value = String.format("%.1f", statsState.averageWeight),
                        unit = "kg",
                        icon = Icons.Outlined.MonitorWeight,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "最低",
                        value = String.format("%.1f", statsState.minWeight),
                        unit = "kg",
                        icon = Icons.Outlined.TrendingDown,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "最高",
                        value = String.format("%.1f", statsState.maxWeight),
                        unit = "kg",
                        icon = Icons.Outlined.TrendingUp,
                        color = Orange40,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 体重变化趋势
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "体重变化趋势",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (statsState.weeklyRecords.size >= 2) {
                            val weightChange = statsState.weightChange
                            val changeText = if (weightChange < 0) "↓ ${String.format("%.1f", kotlin.math.abs(weightChange))} kg" 
                                else if (weightChange > 0) "↑ ${String.format("%.1f", weightChange)} kg"
                                else "无变化"
                            val changeColor = if (weightChange < 0) MaterialTheme.colorScheme.primary 
                                else if (weightChange > 0) Red40
                                else MaterialTheme.colorScheme.onSurfaceVariant

                            Text(
                                text = changeText,
                                style = MaterialTheme.typography.headlineSmall,
                                color = changeColor,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 简单折线图
                            WeightChart(records = statsState.weeklyRecords)
                        } else {
                            Text(
                                text = "需要至少 2 条记录才能显示趋势",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // BMI 卡片
                if (statsState.bmi > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "BMI 指数",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = String.format("%.1f", statsState.bmi),
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = statsState.bmiCategory,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = when (statsState.bmiCategory) {
                                            "正常" -> MaterialTheme.colorScheme.primary
                                            "偏瘦" -> Yellow40
                                            "偏胖" -> Orange40
                                            "肥胖" -> Red40
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                                // BMI 进度条
                                Column(modifier = Modifier.weight(1f)) {
                                    val bmiProgress = (statsState.bmi / 35.0).coerceIn(0.0, 1.0).toFloat()
                                    LinearProgressIndicator(
                                        progress = { bmiProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(12.dp),
                                        color = when {
                                            statsState.bmi < 18.5 -> Yellow40
                                            statsState.bmi < 24.0 -> MaterialTheme.colorScheme.primary
                                            statsState.bmi < 28.0 -> Orange40
                                            else -> Red40
                                        },
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("偏瘦", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("正常", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("偏胖", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("肥胖", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WeightChart(
    records: List<com.example.weightmanager.data.entity.WeightRecordEntity>,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) return

    val lineColor = MaterialTheme.colorScheme.primary
    val pointColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant

    val minWeight = records.minOf { it.weight } - 1
    val maxWeight = records.maxOf { it.weight } + 1
    val range = maxWeight - minWeight

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (range <= 0 || records.size < 2) return@Canvas

            val width = size.width
            val height = size.height
            val padding = 40f
            val chartWidth = width - padding * 2
            val chartHeight = height - padding * 2

            // 绘制网格线
            for (i in 0..4) {
                val y = padding + chartHeight * i / 4
                drawLine(
                    color = gridColor,
                    start = Offset(padding, y),
                    end = Offset(width - padding, y),
                    strokeWidth = 1f
                )
            }

            // 绘制数据点和连线
            val path = Path()
            val points = records.mapIndexed { index, record ->
                val x = padding + (chartWidth * index / (records.size - 1).coerceAtLeast(1))
                val y = padding + chartHeight * (1 - (record.weight - minWeight).toFloat() / range.toFloat())
                Offset(x, y)
            }

            points.forEachIndexed { index, point ->
                if (index == 0) {
                    path.moveTo(point.x, point.y)
                } else {
                    path.lineTo(point.x, point.y)
                }
            }

            // 绘制连线
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )

            // 绘制数据点
            points.forEach { point ->
                drawCircle(
                    color = pointColor,
                    radius = 5f,
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 3f,
                    center = point
                )
            }
        }
    }
}
