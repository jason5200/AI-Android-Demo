<div align="center">

# AI-Android-Demo

**Android 对话 Demo：UI 骨架 + OpenAI 兼容流式接口**

[![License](https://img.shields.io/github/license/jason5200/AI-Android-Demo)](https://github.com/jason5200/AI-Android-Demo)

</div>

---

## 这个 Demo 实际有什么

| 有 | 没有 |
|----|------|
| 对话界面 + 流式追加 | 端侧 MediaPipe / llama.cpp |
| `LlmClient` 抽象 | 车机语音 / ASR / TTS |
| 配置 Key 后走 `chat/completions` SSE | 生产级会话存储 |

没有 `llm.api.key` 时走 **Mock**（假流式），用来确认 UI 链路。配上 Key 后走真实 HTTP，兼容 OpenAI 以及通义 `compatible-mode` 等。

原理文章：[AAOS-Guide · 端侧推理](https://github.com/jason5200/AAOS-Guide/blob/main/05-ai-integration/on-device-llm.md)

## 快速开始

1. 用 Android Studio 打开本工程，或命令行：

```bash
./gradlew assembleDebug
```

2. （可选）把 `local.properties.example` 复制为 `local.properties`，填 SDK 路径和密钥：

```
sdk.dir=...你的 Android SDK...
llm.api.key=sk-...
llm.base.url=https://api.openai.com/v1
llm.model=gpt-4o-mini
```

3. 运行 app。顶栏会显示当前是 Mock 还是真实后端。

`local.properties` 已在 `.gitignore`，不要把 Key 提交上去。

## 代码结构

```
app/src/main/java/com/jason/aichat/
├── MainActivity.kt                  # 对话 UI
├── LlmClient.kt                     # 接口 + Mock
└── OpenAiCompatibleLlmClient.kt     # OpenAI 兼容 SSE
```

## 参与共建

欢迎 PR：MediaPipe 端侧实现、llama.cpp 绑定、把 Mock 换成可切换的多后端。流程见 [CONTRIBUTING.md](CONTRIBUTING.md)。

## License

[Apache-2.0](LICENSE) © [jason5200](https://github.com/jason5200)
