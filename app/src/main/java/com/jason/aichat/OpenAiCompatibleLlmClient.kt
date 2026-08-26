package com.jason.aichat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * OpenAI 兼容 Chat Completions 流式客户端（SSE）。
 *
 * 已验证路径：`POST {baseUrl}/chat/completions`，`stream: true`。
 * 通义等兼容模式把 [baseUrl] 换成对应 endpoint 即可，例如
 * `https://dashscope.aliyuncs.com/compatible-mode/v1`。
 */
class OpenAiCompatibleLlmClient(
    baseUrl: String,
    private val apiKey: String,
    private val model: String
) : LlmClient {

    private val endpoint = baseUrl.trimEnd('/') + "/chat/completions"

    override val backendLabel: String = "OpenAI 兼容 · $model"

    override suspend fun generate(prompt: String, onToken: (String) -> Unit): String {
        return withContext(Dispatchers.IO) {
            val body = JSONObject()
                .put("model", model)
                .put("stream", true)
                .put(
                    "messages",
                    JSONArray().put(
                        JSONObject().put("role", "user").put("content", prompt)
                    )
                )
                .toString()

            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 120_000
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "text/event-stream")
                setRequestProperty("Authorization", "Bearer $apiKey")
            }

            try {
                connection.outputStream.use { os ->
                    os.write(body.toByteArray(StandardCharsets.UTF_8))
                }

                val status = connection.responseCode
                val stream = if (status in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream ?: connection.inputStream
                }

                val reader = BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8))
                if (status !in 200..299) {
                    val err = reader.readText()
                    throw IllegalStateException("HTTP $status: ${shortError(err)}")
                }

                val full = StringBuilder()
                reader.useLines { lines ->
                    lines.forEach { raw ->
                        val line = raw.trim()
                        if (line.isEmpty() || !line.startsWith("data:")) return@forEach
                        val payload = line.removePrefix("data:").trim()
                        if (payload == "[DONE]") return@forEach
                        val delta = parseDelta(payload) ?: return@forEach
                        full.append(delta)
                        onToken(delta)
                    }
                }
                if (full.isEmpty()) {
                    throw IllegalStateException("模型没有返回内容，请检查模型名与额度。")
                }
                full.toString()
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun parseDelta(payload: String): String? {
        return try {
            val obj = JSONObject(payload)
            if (obj.has("error")) {
                throw IllegalStateException(shortError(payload))
            }
            val delta = obj.optJSONArray("choices")
                ?.optJSONObject(0)
                ?.optJSONObject("delta")
                ?: return null
            if (!delta.has("content") || delta.isNull("content")) null
            else delta.getString("content")
        } catch (e: IllegalStateException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }

    private fun shortError(raw: String): String {
        return try {
            val obj = JSONObject(raw)
            obj.optJSONObject("error")?.optString("message")
                ?.takeIf { it.isNotBlank() }
                ?: raw.take(300)
        } catch (_: Exception) {
            raw.take(300)
        }
    }
}
