package com.example.stock4.data

import android.content.Context
import android.util.Log
import com.example.stock4.ui.screens.StockItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 股票数据管理器
 * 负责从本地资源文件加载股票数据并提供搜索功能
 */
object StockDataManager {
    // 存储所有股票数据的列表
    private var allStocks: List<StockItem> = emptyList()
    
    // 标记是否已初始化
    private var isInitialized = false
    
    /**
     * 初始化股票数据
     * 从assets文件夹中读取股票列表数据
     */
    suspend fun initialize(context: Context) {
        if (isInitialized) return
        
        withContext(Dispatchers.IO) {
            try {
                val stockList = mutableListOf<StockItem>()
                
                // 打开assets中的股票列表文件
                context.assets.open("stock_list.txt").use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            line?.let {
                                // 解析每一行数据，格式为: 股票名称|股票代码
                                val parts = it.split("|")
                                if (parts.size >= 2) {
                                    val name = parts[0].trim()
                                    val code = parts[1].trim()
                                    
                                    // 创建StockItem对象并添加到列表
                                    stockList.add(
                                        StockItem(
                                            name = name,
                                            code = code,
                                            price = "0.00",  // 默认价格
                                            changePercent = "0.00%",  // 默认涨跌幅
                                            isUp = false  // 默认不上涨
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                
                // 更新全局股票列表
                allStocks = stockList
                isInitialized = true
                
                Log.d("StockDataManager", "股票数据加载完成，共 ${allStocks.size} 条")
            } catch (e: Exception) {
                Log.e("StockDataManager", "加载股票数据失败", e)
            }
        }
    }
    
    /**
     * 搜索股票
     * 根据关键词在股票名称和代码中进行搜索
     */
    fun searchStocks(query: String): List<StockItem> {
        if (query.isBlank()) return emptyList()
        
        return allStocks.filter { stock ->
            stock.name.contains(query, ignoreCase = true) || 
            stock.code.contains(query, ignoreCase = true)
        }
    }
    
    /**
     * 获取所有股票
     */
    fun getAllStocks(): List<StockItem> {
        return allStocks
    }
} 