package com.example.stock4.data.api

/**
 * 股票分析结果数据类
 * 包含投资建议、风险评估、技术面分析和基本面分析
 */
data class StockAnalysisResult(
    val recommendation: String,  // 买入/卖出/持有
    val investmentAdvice: String, // 详细投资建议
    val riskAssessment: String,   // 风险评估
    val technicalAnalysis: String, // 技术面分析
    val fundamentalAnalysis: String // 基本面分析
) 