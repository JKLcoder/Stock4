package com.example.stock4.ui.screens

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.example.stock4.data.StockDataManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    
    // 使用StockDataManager进行搜索
    val searchResults = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            StockDataManager.searchStocks(searchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("搜索页面") },
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
        ) {
            // 搜索框
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("搜索股票代码或名称") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索图标"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "清除"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 搜索结果
            if (searchQuery.isNotEmpty()) {
                Text(
                    text = "搜索结果 (${searchResults.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "未找到相关股票",
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                } else {
                    LazyColumn {
                        items(searchResults) { stock ->
                            SearchResultItem(stock = stock, onAddClick = {
                                // 添加到自选股
                                // 这里可以添加保存自选股的逻辑
                                navController.navigateUp()
                            })
                            
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(stock: StockItem, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 股票信息
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
        
        // 添加按钮
        IconButton(onClick = onAddClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "添加",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
} 