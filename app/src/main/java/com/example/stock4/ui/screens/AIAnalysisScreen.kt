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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stock4.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAnalysisScreen(navController: NavController) {
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
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 股票基本信息
            Text(
                text = "贵州茅台 (600519)",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // AI投资建议
            AnalysisCard(
                title = "AI投资建议",
                content = "基于技术面和基本面分析，建议持有该股票。短期内可能会有小幅波动，但长期来看具有较好的投资价值。",
                backgroundColor = Color(0xFFE3F2FD)
            )
            
            // 风险评估
            AnalysisCard(
                title = "风险评估",
                content = "风险等级：中等\n• 市场风险：受大盘波动影响较大\n• 行业风险：白酒行业竞争加剧\n• 公司风险：估值较高，业绩增长压力大",
                backgroundColor = Color(0xFFFFF8E1)
            )
            
            // 技术分析
            AnalysisCard(
                title = "技术面分析",
                content = "• K线图显示处于上升通道\n• MACD指标显示多头趋势\n• KDJ指标显示超买状态\n• 成交量逐渐放大，说明上涨动能较强",
                backgroundColor = Color(0xFFE8F5E9)
            )
            
            // 基本面分析
            AnalysisCard(
                title = "基本面分析",
                content = "公司财务状况良好，营收持续增长。\n• 营收：同比增长15.3%\n• 净利润：同比增长12.7%\n• 毛利率：保持在75%以上\n• 市盈率：35.6倍，高于行业平均水平",
                backgroundColor = Color(0xFFE1F5FE)
            )
            
            // 添加收藏按钮
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_favorite),
                            contentDescription = "收藏",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(
                            text = "收藏分析报告",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = content,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
} 