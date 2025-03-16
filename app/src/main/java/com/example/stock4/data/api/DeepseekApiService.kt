package com.example.stock4.data.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object DeepseekApiService {
    private const val API_KEY = "sk-3ed3dce69f21447a9c20306a22239404"
    private const val API_URL = "https://api.deepseek.com/v1/chat/completions"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    /**
     * 分析股票并提供简要投资建议
     * @param stockName 股票名称
     * @param stockCode 股票代码
     * @param price 当前价格
     * @param changePercent 涨跌幅
     * @return 投资建议（买入/卖出/持有）
     */
    suspend fun analyzeStock(stockName: String, stockCode: String, price: String, changePercent: String): String = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                你是一位专业的股票分析师，请根据以下信息对股票进行简要分析并给出投资建议：
                
                股票名称: $stockName
                股票代码: $stockCode
                当前价格: $price
                涨跌幅: $changePercent
                
                请只回复一个词的投资建议：买入、卖出或持有。不要解释原因，只回复一个词。
            """.trimIndent()
            
            val jsonBody = JSONObject().apply {
                put("model", "deepseek-chat")
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
                put("temperature", 0.3)
                put("max_tokens", 10)
            }
            
            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())
            
            val request = Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer $API_KEY")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                Log.e("DeepseekAPI", "API调用失败: ${response.code} ${response.message}")
                return@withContext "持有" // 默认建议
            }
            
            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val content = jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()
            
            // 提取建议（买入/卖出/持有）
            return@withContext when {
                content.contains("买入") -> "买入"
                content.contains("卖出") -> "卖出"
                else -> "持有"
            }
        } catch (e: Exception) {
            Log.e("DeepseekAPI", "分析股票时出错", e)
            return@withContext "持有" // 出错时默认建议
        }
    }
    
    /**
     * 获取股票的详细分析结果
     * @param stockName 股票名称
     * @param stockCode 股票代码
     * @param price 当前价格
     * @param changePercent 涨跌幅
     * @return 详细分析结果
     */
    suspend fun getDetailedAnalysis(stockName: String, stockCode: String, price: String, changePercent: String): StockAnalysisResult = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                你是一位专业的股票分析师，请根据以下信息对股票进行详细分析：
                
                股票名称: $stockName
                股票代码: $stockCode
                当前价格: $price
                涨跌幅: $changePercent
                
                请提供以下五个方面的分析，每个部分控制在100字以内：
                1. 投资建议（买入/卖出/持有）
                2. 详细投资建议
                3. 风险评估
                4. 技术面分析
                5. 基本面分析
                
                请直接返回JSON格式，不要使用markdown代码块，格式如下：
                {
                  "recommendation": "买入/卖出/持有",
                  "investmentAdvice": "详细投资建议...",
                  "riskAssessment": "风险评估...",
                  "technicalAnalysis": "技术面分析...",
                  "fundamentalAnalysis": "基本面分析..."
                }
            """.trimIndent()
            
            val jsonBody = JSONObject().apply {
                put("model", "deepseek-chat")
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
                put("temperature", 0.3)
                put("max_tokens", 1000)
            }
            
            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())
            
            val request = Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer $API_KEY")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                Log.e("DeepseekAPI", "详细分析API调用失败: ${response.code} ${response.message}")
                return@withContext createDefaultAnalysisResult()
            }
            
            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val content = jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()
            
            // 记录原始内容，便于调试
            Log.d("DeepseekAPI", "原始内容: $content")
            
            // 尝试解析JSON响应
            try {
                // 清理内容，移除可能的 Markdown 代码块标记
                val cleanedContent = cleanMarkdownCodeBlocks(content)
                val analysisJson = JSONObject(cleanedContent)
                return@withContext StockAnalysisResult(
                    recommendation = analysisJson.optString("recommendation", "持有"),
                    investmentAdvice = analysisJson.optString("investmentAdvice", "暂无详细建议"),
                    riskAssessment = analysisJson.optString("riskAssessment", "暂无风险评估"),
                    technicalAnalysis = analysisJson.optString("technicalAnalysis", "暂无技术面分析"),
                    fundamentalAnalysis = analysisJson.optString("fundamentalAnalysis", "暂无基本面分析")
                )
            } catch (e: Exception) {
                Log.e("DeepseekAPI", "解析详细分析结果失败", e)
                // 如果JSON解析失败，尝试从文本中提取信息
                return@withContext extractAnalysisFromText(content)
            }
        } catch (e: Exception) {
            Log.e("DeepseekAPI", "获取详细分析时出错", e)
            return@withContext createDefaultAnalysisResult()
        }
    }
    
    /**
     * 从文本中提取分析结果
     */
    private fun extractAnalysisFromText(text: String): StockAnalysisResult {
        val recommendation = when {
            text.contains("买入") -> "买入"
            text.contains("卖出") -> "卖出"
            else -> "持有"
        }
        
        // 尝试从文本中提取各部分内容
        val investmentAdvicePattern = "详细投资建议[：:](.*?)(?=风险评估|技术面分析|基本面分析|$)".toRegex(RegexOption.DOT_MATCHES_ALL)
        val riskAssessmentPattern = "风险评估[：:](.*?)(?=详细投资建议|技术面分析|基本面分析|$)".toRegex(RegexOption.DOT_MATCHES_ALL)
        val technicalAnalysisPattern = "技术面分析[：:](.*?)(?=详细投资建议|风险评估|基本面分析|$)".toRegex(RegexOption.DOT_MATCHES_ALL)
        val fundamentalAnalysisPattern = "基本面分析[：:](.*?)(?=详细投资建议|风险评估|技术面分析|$)".toRegex(RegexOption.DOT_MATCHES_ALL)
        
        val investmentAdvice = investmentAdvicePattern.find(text)?.groupValues?.getOrNull(1)?.trim() ?: "暂无详细建议"
        val riskAssessment = riskAssessmentPattern.find(text)?.groupValues?.getOrNull(1)?.trim() ?: "暂无风险评估"
        val technicalAnalysis = technicalAnalysisPattern.find(text)?.groupValues?.getOrNull(1)?.trim() ?: "暂无技术面分析"
        val fundamentalAnalysis = fundamentalAnalysisPattern.find(text)?.groupValues?.getOrNull(1)?.trim() ?: "暂无基本面分析"
        
        return StockAnalysisResult(
            recommendation = recommendation,
            investmentAdvice = investmentAdvice,
            riskAssessment = riskAssessment,
            technicalAnalysis = technicalAnalysis,
            fundamentalAnalysis = fundamentalAnalysis
        )
    }
    
    /**
     * 创建默认分析结果
     */
    fun createDefaultAnalysisResult(): StockAnalysisResult {
        return StockAnalysisResult(
            recommendation = "持有",
            investmentAdvice = "由于无法获取实时数据，建议投资者持有观望，等待更多市场信息。",
            riskAssessment = "风险等级：未知\n由于无法获取实时数据，无法评估当前风险水平。",
            technicalAnalysis = "由于无法获取实时数据，无法提供技术面分析。建议投资者参考其他数据源。",
            fundamentalAnalysis = "由于无法获取实时数据，无法提供基本面分析。建议投资者参考公司最新财报和行业动态。"
        )
    }

    /**
     * 清理 Markdown 代码块标记
     * 移除 ```json 和 ``` 等标记
     */
    private fun cleanMarkdownCodeBlocks(content: String): String {
        // 移除开头的 ```json 或 ``` 标记
        var cleanedContent = content.trim()
        
        // 检查是否以 ``` 开头
        if (cleanedContent.startsWith("```")) {
            // 找到第一行的结束位置
            val firstLineEnd = cleanedContent.indexOf('\n')
            if (firstLineEnd != -1) {
                cleanedContent = cleanedContent.substring(firstLineEnd + 1)
            }
        }
        
        // 移除结尾的 ``` 标记
        if (cleanedContent.endsWith("```")) {
            cleanedContent = cleanedContent.substring(0, cleanedContent.length - 3).trim()
        }
        
        // 记录清理后的内容，便于调试
        Log.d("DeepseekAPI", "清理后的内容: $cleanedContent")
        
        return cleanedContent
    }
}