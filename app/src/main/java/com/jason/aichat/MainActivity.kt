package com.jason.aichat

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * AI 对话 Demo 主界面。
 *
 * 演示：输入 prompt → 调用 LLM 接口 → 流式输出到界面。
 * 通过 LlmClient 接口抽象，替换推理后端不影响 UI 层。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tvChat: TextView
    private lateinit var etInput: EditText
    private lateinit var btnSend: Button

    private val llmClient: LlmClient = LlmClient.createMock()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvChat = findViewById(R.id.tvChat)
        etInput = findViewById(R.id.etInput)
        btnSend = findViewById(R.id.btnSend)

        btnSend.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        val prompt = etInput.text.toString().trim()
        if (prompt.isEmpty()) return

        // 显示用户输入
        appendChat("你：$prompt\n")
        etInput.setText("")

        // 禁用按钮，防止重复提交
        btnSend.isEnabled = false
        appendChat("AI：")

        lifecycleScope.launch {
            val reply = withContext(Dispatchers.Default) {
                llmClient.generate(prompt) { token ->
                    // 流式回调：切到主线程更新 UI
                    runOnUiThread { tvChat.append(token) }
                }
            }
            // 收尾
            runOnUiThread {
                appendChat("\n\n")
                btnSend.isEnabled = true
            }
        }
    }

    private fun appendChat(text: String) {
        tvChat.append(text)
    }
}
