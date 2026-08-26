package com.jason.aichat

/**
 * LLM 推理接口。UI 只依赖这个接口。
 *
 * 实现：
 * - [MockLlmClient]：无 Key 时的本地假流式，用来跑通界面
 * - [OpenAiCompatibleLlmClient]：OpenAI / 通义 compatible-mode 等 `/chat/completions` 流式接口
 */
interface LlmClient {

    /** 当前后端说明，用于界面状态栏。 */
    val backendLabel: String

    /**
     * 流式生成回复。
     *
     * @param prompt 用户输入
     * @param onToken 每个增量回调一次（可能是单字或一小段）
     * @return 完整回复
     */
    suspend fun generate(prompt: String, onToken: (String) -> Unit): String

    companion object {
        fun create(): LlmClient {
            val key = BuildConfig.LLM_API_KEY.trim()
            if (key.isEmpty()) {
                return MockLlmClient()
            }
            return OpenAiCompatibleLlmClient(
                baseUrl = BuildConfig.LLM_BASE_URL,
                apiKey = key,
                model = BuildConfig.LLM_MODEL
            )
        }
    }
}

/** 无真实模型：只验证输入 → 流式展示 → 收尾。 */
private class MockLlmClient : LlmClient {

    override val backendLabel: String = "Mock（未配置 llm.api.key）"

    override suspend fun generate(prompt: String, onToken: (String) -> Unit): String {
        val reply = "这是对「$prompt」的演示回复。\n" +
            "在项目根目录 local.properties 写入 llm.api.key 后，会走 OpenAI 兼容接口。"
        reply.forEach { char ->
            onToken(char.toString())
            kotlinx.coroutines.delay(20)
        }
        return reply
    }
}
