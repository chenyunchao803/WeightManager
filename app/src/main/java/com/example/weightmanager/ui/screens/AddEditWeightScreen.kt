package com.example.weightmanager.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weightmanager.ui.theme.Orange40
import com.example.weightmanager.ui.theme.Purple40
import com.example.weightmanager.ui.theme.Yellow40
import com.example.weightmanager.viewmodel.WeightViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWeightScreen(
    navController: NavHostController,
    weightViewModel: WeightViewModel,
    recordId: Long? = null
) {
    val uiState by weightViewModel.addEditUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }

    // 如果是编辑模式，加载记录
    LaunchedEffect(recordId) {
        if (recordId != null) {
            weightViewModel.loadRecordForEdit(recordId)
        }
    }

    // 保存成功后返回
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            navController.popBackStack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditMode) "编辑体重记录" else "添加体重记录",
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "体重信息",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 体重输入
                    OutlinedTextField(
                        value = uiState.weight,
                        onValueChange = { weightViewModel.onWeightChange(it) },
                        label = { Text("体重 (kg)") },
                        placeholder = { Text("请输入体重") },
                        isError = uiState.weightError != null,
                        supportingText = uiState.weightError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 日期选择
                    OutlinedTextField(
                        value = uiState.date,
                        onValueChange = { },
                        label = { Text("记录日期") },
                        placeholder = { Text("请选择日期") },
                        isError = uiState.dateError != null,
                        supportingText = uiState.dateError?.let { { Text(it) } },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            TextButton(onClick = { showDatePicker = true }) {
                                Text("选择")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 备注
                    OutlinedTextField(
                        value = uiState.note,
                        onValueChange = { weightViewModel.onNoteChange(it) },
                        label = { Text("备注（可选）") },
                        placeholder = { Text("运动情况、饮食等") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 心情选择
                    Text(
                        text = "心情（可选）",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = uiState.mood == 1,
                            onClick = { weightViewModel.onMoodChange(if (uiState.mood == 1) 0 else 1) },
                            label = { Text("开心") },
                            leadingIcon = {
                                Icon(Icons.Default.Mood, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = uiState.mood == 2,
                            onClick = { weightViewModel.onMoodChange(if (uiState.mood == 2) 0 else 2) },
                            label = { Text("一般") },
                            leadingIcon = {
                                Icon(Icons.Default.SentimentSatisfied, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = uiState.mood == 3,
                            onClick = { weightViewModel.onMoodChange(if (uiState.mood == 3) 0 else 3) },
                            label = { Text("不好") },
                            leadingIcon = {
                                Icon(Icons.Default.MoodBad, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 保存按钮
            Button(
                onClick = { weightViewModel.saveRecord() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !uiState.isSaving,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (uiState.isSaving) "保存中..." else "保存记录",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // 错误提示
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // 日期选择器
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            weightViewModel.onDateChange(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
