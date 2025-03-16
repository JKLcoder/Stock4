package com.example.stock4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stock4.R
import com.example.stock4.navigation.Screen
import com.example.stock4.ui.components.BottomNavBar
import com.example.stock4.ui.components.ProfileMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 用户信息区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E88E5))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 用户头像
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Text(
                            text = "用户名",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 用户名
                    Text(
                        text = "用户名",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 菜单项
            ProfileMenuItem(
                icon = R.drawable.ic_favorite,
                title = "我的收藏",
                onClick = { /* 跳转到收藏页面 */ }
            )
            
            Divider()
            
            ProfileMenuItem(
                icon = R.drawable.ic_history,
                title = "分析历史",
                onClick = { /* 跳转到历史页面 */ }
            )
            
            Divider()
            
            ProfileMenuItem(
                icon = R.drawable.ic_notification,
                title = "消息提醒",
                onClick = { /* 跳转到提醒页面 */ }
            )
            
            Divider()
            
            ProfileMenuItem(
                icon = R.drawable.ic_settings,
                title = "设置",
                onClick = { navController.navigate(Screen.Settings.route) }
            )
            
            Divider()
        }
    }
} 