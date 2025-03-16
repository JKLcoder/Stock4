package com.example.stock4.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stock4.R
import com.example.stock4.data.StockDataManager
import com.example.stock4.data.api.StockAnalysisResult
import com.example.stock4.data.model.StockItem
import kotlinx.coroutines.launch
import androidx.compose.material3.HorizontalDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAnalysisScreen(navController: NavController, stockCode: String) {
    // 获取股票信息
    val stock = remember { StockDataManager.getStockByCode(stockCode) }
    
    // 分析结果状态
    var analysisResult by remember { mutableStateOf<StockAnalysisResult?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // 协程作用域
    val coroutineScope = rememberCoroutineScope()
    
    // 加载分析结果
    LaunchedEffect(stockCode) {
        isLoading = true
        errorMessage = null
        
        try {
            // 获取股票信息
            val stockInfo = StockDataManager.getStockByCode(stockCode)
            
            // 如果股票已有分析结果，直接使用
            if (stockInfo?.analysisResult != null) {
                analysisResult = stockInfo.analysisResult
                isLoading = false
            } else {
                // 否则获取详细分析
                analysisResult = StockDataManager.getStockDetailedAnalysis(stockCode)
            }
        } catch (e: Exception) {
            errorMessage = "获取分析结果失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI深度分析") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    // 添加刷新按钮
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    analysisResult = StockDataManager.getStockDetailedAnalysis(stockCode)
                                } catch (e: Exception) {
                                    errorMessage = "刷新分析结果失败: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_refresh),
                            contentDescription = "刷新",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // 显示加载中
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("正在分析股票，请稍候...")
                    }
                }
            } else if (errorMessage != null) {
                // 显示错误信息
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "未知错误",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else if (stock != null && analysisResult != null) {
                // 显示分析结果
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 股票基本信息
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${stock.name} (${stock.code})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stock.price,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text(
                                    text = stock.changePercent,
                                    color = if (stock.isUp) Color(0xFF4CAF50) else Color(0xFFE53935),
                                    fontSize = 16.sp
                                )
                            }
                        }
                        
                        // 显示建议标签
                        val recommendationColor = when(analysisResult?.recommendation) {
                            "买入" -> Color(0xFF4CAF50)
                            "卖出" -> Color(0xFFE53935)
                            else -> Color(0xFFFFA000)
                        }
                        
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = recommendationColor
                            )
                        ) {
                            Text(
                                text = analysisResult?.recommendation ?: "持有",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // AI投资建议
                    AnalysisCard(
                        title = "AI投资建议",
                        content = analysisResult?.investmentAdvice ?: "暂无投资建议",
                        backgroundColor = Color(0xFFE3F2FD)
                    )
                    
                    // 风险评估
                    AnalysisCard(
                        title = "风险评估",
                        content = analysisResult?.riskAssessment ?: "暂无风险评估",
                        backgroundColor = Color(0xFFFFF8E1)
                    )
                    
                    // 技术分析
                    AnalysisCard(
                        title = "技术面分析",
                        content = analysisResult?.technicalAnalysis ?: "暂无技术面分析",
                        backgroundColor = Color(0xFFE8F5E9)
                    )
                    
                    // 基本面分析
                    AnalysisCard(
                        title = "基本面分析",
                        content = analysisResult?.fundamentalAnalysis ?: "暂无基本面分析",
                        backgroundColor = Color(0xFFF3E5F5)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 免责声明
                    Text(
                        text = "免责声明：本分析仅供参考，不构成投资建议。投资有风险，入市需谨慎。",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            } else {
                // 显示未找到股票信息
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("未找到股票信息")
                }
            }
        }
    }
}

@Composable
fun AnalysisCard(
    title: String,
    content: String,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = content,
                fontSize = 14.sp
            )
        }
    }
} 