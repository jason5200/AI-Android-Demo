package com.jason.aichat

/**
 * LLM 推理接口抽象。
 *
 * 屏蔽具体推理后端（MediaPipe / llama.cpp / 云端 API），
 * 让 UI 层只依赖这个接口。
 */
interface LlmClient {

    /**
     * 流式生成回复。
     *
     * @param prompt     用户输入
     * @param onToken    每生成一个 token 回调一次
     * @return 完整回复文本
     */
    suspend fun generate(prompt: String, onToken: (String) -> Unit): String

    companion object {
        /**
         * 创建一个演示用的假实现（无真实模型）。
         * 替换真实模型时，只需换掉这里的实现。
         */
        fun createMock(): LlmClient = MockLlmClient()
    }
}

/**
 * 演示实现：模拟流式输出，用于跑通 UI 链路。
 * 接入真实模型时，替换为 MediaPipe / llama.cpp / 云端 API 的实现。
 */
private class MockLlmClient : LlmClient {
    override suspend fun generate(prompt: String, onToken: (String) -> Unit): String {
        val reply = "这是对「$prompt」的演示回复。\n" +
            "接入真实 LLM 后，这里会返回模型生成的回答。"
        // 模拟逐字输出，展示流式效果
        reply.forEach { char ->
            onToken(char.toString())
            kotlinx.coroutines.delay(30)
        }
        return reply
    }
}
