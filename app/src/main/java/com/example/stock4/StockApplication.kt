package com.example.stock4

import android.app.Application
import com.example.stock4.data.StockDataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    
    override fun onCreate() {
        super.onCreate()
        
        // 在应用启动时初始化股票数据
        applicationScope.launch {
            StockDataManager.initialize(applicationContext)
        }
    }
} 