package com.example.stock4.data.model

import com.example.stock4.data.api.StockAnalysisResult

data class StockItem(
    val name: String,
    val code: String,
    val price: String,
    val changePercent: String,
    val isUp: Boolean = changePercent.startsWith("+"),
    val isAnalyzing: Boolean = false,
    val recommendation: String = "",
    val analysisResult: StockAnalysisResult? = null
) 