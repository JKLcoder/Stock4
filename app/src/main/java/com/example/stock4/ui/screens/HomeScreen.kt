package com.example.stock4.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.stock4.navigation.Screen
import com.example.stock4.ui.components.BottomNavBar
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.stock4.data.model.StockItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // 使用StockDataManager获取自选股数据
    val favoriteStocks = remember { mutableStateOf(StockDataManager.getFavoriteStocks()) }
    
    // 添加分析状态
    var isAnalyzing by remember { mutableStateOf(false) }
    
    // 添加Snackbar状态
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    // 添加一个副作用，当自选股列表变化时更新UI
    LaunchedEffect(StockDataManager.getFavoriteStocks().size) {
        favoriteStocks.value = StockDataManager.getFavoriteStocks()
    }

    // 如果自选股列表为空，显示默认数据
    val displayStocks = if (favoriteStocks.value.isEmpty()) {
        listOf(
            StockItem("贵州茅台", "600519", "1789.00", "+2.35%", true),
            StockItem("腾讯控股", "00700", "368.40", "+1.52%", true),
            StockItem("阿里巴巴", "09988", "75.80", "-0.65%", false),
            StockItem("中国平安", "601318", "42.56", "-1.23%", false),
            StockItem("宁德时代", "300750", "135.20", "+3.45%", true)
        )
    } else {
        favoriteStocks.value
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("自选股") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // 修改为调用Deepseek API分析股票
                    if (!isAnalyzing) {
                        isAnalyzing = true
                        coroutineScope.launch {
                            try {
                                // 使用 launch 块内部调用 suspend 函数
                                snackbarHostState.showSnackbar("正在分析股票，请稍候...")
                                StockDataManager.analyzeAllFavoriteStocks()
                                snackbarHostState.showSnackbar("分析完成")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("分析失败: ${e.message}")
                            } finally {
                                isAnalyzing = false
                            }
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_stock),
                    contentDescription = "分析股票",
                    tint = Color.White
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (favoriteStocks.value.isEmpty() && displayStocks.isEmpty()) {
            // 显示空状态
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_empty),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "暂无自选股",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { navController.navigate(Screen.Search.route) }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("添加自选股")
                        }
                    }
                }
            }
        } else {
            // 显示自选股列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                items(displayStocks) { stock ->
                    StockCard(
                        stock = stock, 
                        onItemClick = {
                            // 点击股票项跳转到AI分析页面，传递股票代码
                            navController.navigate(Screen.AIAnalysis.createRoute(stock.code))
                        },
                        onDeleteClick = {
                            // 从自选股中删除
                            StockDataManager.removeFromFavorites(stock.code)
                            // 更新UI
                            favoriteStocks.value = StockDataManager.getFavoriteStocks()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockCard(
    stock: StockItem, 
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onItemClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 股票名称和代码
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stock.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stock.code,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                
                // 添加分析建议显示
                if (stock.isAnalyzing) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "分析中...",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                } else if (stock.recommendation.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val recommendationColor = when(stock.recommendation) {
                        "买入" -> Color(0xFF4CAF50)
                        "卖出" -> Color(0xFFE53935)
                        else -> Color(0xFFFFA000)
                    }
                    Text(
                        text = "AI建议: ${stock.recommendation}",
                        fontSize = 12.sp,
                        color = recommendationColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 价格和涨跌幅
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stock.price,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stock.changePercent,
                    color = if (stock.isUp) Color(0xFF4CAF50) else Color(0xFFE53935),
                    fontSize = 14.sp
                )
                
                // 删除按钮
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onDeleteClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "删除",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "删除",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
} 