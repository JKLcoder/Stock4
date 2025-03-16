package com.example.stock4.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
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
        ) {
            // 通知设置
            SettingItem(
                title = "通知设置",
                description = "已开启",
                hasSwitch = true,
                initialSwitchState = true
            )
            
            Divider()
            
            // 深色模式
            SettingItem(
                title = "深色模式",
                description = "关闭",
                hasSwitch = true,
                initialSwitchState = false
            )
            
            Divider()
            
            // 清除缓存
            SettingItem(
                title = "清除缓存",
                description = "2.5MB",
                hasSwitch = false
            )
            
            Divider()
            
            // 关于我们
            SettingItem(
                title = "关于我们",
                description = "",
                hasSwitch = false,
                showArrow = true
            )
            
            Divider()
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    hasSwitch: Boolean,
    initialSwitchState: Boolean = false,
    showArrow: Boolean = false
) {
    var switchState by remember { mutableStateOf(initialSwitchState) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            
            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
        
        if (hasSwitch) {
            Switch(
                checked = switchState,
                onCheckedChange = { switchState = it }
            )
        }
        
        if (showArrow) {
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "更多",
                tint = Color.Gray
            )
        }
    }
} 