package com.example.stock4.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stock4.R
import com.example.stock4.navigation.Screen
import com.example.stock4.ui.components.BottomNavBar

// 股票数据模型
data class StockItem(
    val name: String,
    val code: String,
    val price: String,
    val changePercent: String,
    val isUp: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // 模拟股票数据
    val stockList = remember {
        mutableStateOf(
            listOf(
                StockItem("贵州茅台", "600519", "1789.00", "+2.35%", true),
                StockItem("腾讯控股", "00700", "368.40", "+1.52%", true),
                StockItem("阿里巴巴", "09988", "75.80", "-0.65%", false),
                StockItem("中国平安", "601318", "42.56", "-1.23%", false),
                StockItem("宁德时代", "300750", "135.20", "+3.45%", true)
            )
        )
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
                onClick = { navController.navigate(Screen.Search.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "添加",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            items(stockList.value) { stock ->
                StockCard(stock = stock, onItemClick = {
                    // 点击股票项跳转到AI分析页面
                    navController.navigate(Screen.AIAnalysis.route)
                })
            }
        }
    }
}

@Composable
fun StockCard(stock: StockItem, onItemClick: () -> Unit) {
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
            }
            
            // 股票价格和涨跌幅
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stock.price,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = if (stock.isUp) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stock.changePercent,
                        color = if (stock.isUp) Color(0xFF4CAF50) else Color(0xFFF44336),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
} 