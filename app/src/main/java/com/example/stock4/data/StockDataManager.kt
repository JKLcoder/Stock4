package com.example.stock4.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.stock4.data.api.DeepseekApiService
import com.example.stock4.data.api.StockAnalysisResult
import com.example.stock4.data.model.StockItem
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
    
    // 存储自选股的列表，使用mutableStateListOf以便在UI中自动更新
    private val favoriteStocks = mutableStateListOf<StockItem>()
    
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
    
    /**
     * 获取自选股列表
     */
    fun getFavoriteStocks(): List<StockItem> {
        return favoriteStocks
    }
    
    /**
     * 添加股票到自选股
     * @return 是否添加成功
     */
    fun addToFavorites(stock: StockItem): Boolean {
        // 检查是否已经在自选股中
        if (favoriteStocks.any { it.code == stock.code }) {
            return false // 已存在，添加失败
        }
        
        // 添加到自选股列表
        favoriteStocks.add(stock)
        Log.d("StockDataManager", "添加自选股: ${stock.name} (${stock.code})")
        return true
    }
    
    /**
     * 从自选股中移除股票
     * @return 是否移除成功
     */
    fun removeFromFavorites(stockCode: String): Boolean {
        val initialSize = favoriteStocks.size
        favoriteStocks.removeIf { it.code == stockCode }
        val removed = favoriteStocks.size < initialSize
        
        if (removed) {
            Log.d("StockDataManager", "移除自选股: $stockCode")
        }
        
        return removed
    }
    
    /**
     * 检查股票是否在自选股中
     */
    fun isInFavorites(stockCode: String): Boolean {
        return favoriteStocks.any { it.code == stockCode }
    }
    
    /**
     * 分析所有自选股
     * 使用Deepseek API对所有自选股进行详细分析
     */
    suspend fun analyzeAllFavoriteStocks() {
        // 如果没有自选股，使用默认数据
        val stocksToAnalyze = if (favoriteStocks.isEmpty()) {
            listOf(
                StockItem("贵州茅台", "600519", "1789.00", "+2.35%", true),
                StockItem("腾讯控股", "00700", "368.40", "+1.52%", true),
                StockItem("阿里巴巴", "09988", "75.80", "-0.65%", false),
                StockItem("中国平安", "601318", "42.56", "-1.23%", false),
                StockItem("宁德时代", "300750", "135.20", "+3.45%", true)
            )
        } else {
            favoriteStocks.toList()
        }
        
        // 先将所有股票标记为"正在分析"
        stocksToAnalyze.forEachIndexed { index, stock ->
            val updatedStock = stock.copy(isAnalyzing = true)
            if (favoriteStocks.isEmpty()) {
                // 如果是默认数据，直接更新列表
                favoriteStocks.add(updatedStock)
            } else {
                // 如果是自选股，更新现有项
                favoriteStocks[index] = updatedStock
            }
        }
        
        // 逐个分析股票
        stocksToAnalyze.forEachIndexed { index, stock ->
            try {
                // 直接进行详细分析
                val analysisResult = DeepseekApiService.getDetailedAnalysis(
                    stockName = stock.name,
                    stockCode = stock.code,
                    price = stock.price,
                    changePercent = stock.changePercent
                )
                
                // 更新股票信息，包括投资建议和详细分析结果
                val analyzedStock = stock.copy(
                    recommendation = analysisResult.recommendation,
                    analysisResult = analysisResult,
                    isAnalyzing = false
                )
                
                // 更新列表
                if (favoriteStocks.size > index) {
                    favoriteStocks[index] = analyzedStock
                }
                
                Log.d("StockDataManager", "股票分析完成: ${stock.name}, 建议: ${analysisResult.recommendation}")
            } catch (e: Exception) {
                Log.e("StockDataManager", "分析股票失败: ${stock.name}", e)
                
                // 更新为分析失败状态
                if (favoriteStocks.size > index) {
                    favoriteStocks[index] = stock.copy(
                        recommendation = "分析失败",
                        isAnalyzing = false
                    )
                }
            }
        }
    }
    
    /**
     * 根据股票代码获取股票信息
     * @param stockCode 股票代码
     * @return 股票信息，如果未找到则返回null
     */
    fun getStockByCode(stockCode: String): StockItem? {
        // 先从自选股中查找
        val favoriteStock = favoriteStocks.find { it.code == stockCode }
        if (favoriteStock != null) {
            return favoriteStock
        }
        
        // 如果自选股中没有，从所有股票中查找
        return allStocks.find { it.code == stockCode }
    }
    
    /**
     * 获取股票的详细分析
     * @param stockCode 股票代码
     * @return 详细分析结果
     */
    suspend fun getStockDetailedAnalysis(stockCode: String): StockAnalysisResult {
        val stock = getStockByCode(stockCode)
        
        // 如果找不到股票，返回默认结果
        if (stock == null) {
            return DeepseekApiService.createDefaultAnalysisResult()
        }
        
        // 如果 StockItem 类没有 analysisResult 属性，则删除或修改这段代码
        stock.analysisResult?.let { return it }
        
        // 直接进行分析
        return DeepseekApiService.getDetailedAnalysis(
            stockName = stock.name,
            stockCode = stock.code,
            price = stock.price,
            changePercent = stock.changePercent
        )
    }
} 