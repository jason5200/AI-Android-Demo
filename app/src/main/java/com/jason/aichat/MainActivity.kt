package com.jason.aichat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * 对话界面：输入 → [LlmClient.generate] → 流式追加。
 *
 * 未配置 `llm.api.key` 时走 Mock；配置后走 OpenAI 兼容流式接口。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var scrollChat: ScrollView
    private lateinit var tvChat: TextView
    private lateinit var tvStatus: TextView
    private lateinit var etInput: EditText
    private lateinit var btnSend: Button

    private val llmClient: LlmClient = LlmClient.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scrollChat = findViewById(R.id.scrollChat)
        tvChat = findViewById(R.id.tvChat)
        tvStatus = findViewById(R.id.tvStatus)
        etInput = findViewById(R.id.etInput)
        btnSend = findViewById(R.id.btnSend)

        tvStatus.text = getString(R.string.status_backend, llmClient.backendLabel)
        btnSend.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        val prompt = etInput.text.toString().trim()
        if (prompt.isEmpty()) return

        appendChat("你：$prompt\n")
        etInput.setText("")
        btnSend.isEnabled = false
        appendChat("AI：")

        lifecycleScope.launch {
            try {
                llmClient.generate(prompt) { token ->
                    runOnUiThread { appendChat(token) }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    appendChat("\n[请求失败] ${e.message ?: e.javaClass.simpleName}")
                }
            } finally {
                runOnUiThread {
                    appendChat("\n\n")
                    btnSend.isEnabled = true
                }
            }
        }
    }

    private fun appendChat(text: String) {
        tvChat.append(text)
        scrollChat.post { scrollChat.fullScroll(ScrollView.FOCUS_DOWN) }
    }
}
