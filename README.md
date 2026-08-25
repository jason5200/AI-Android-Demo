<div align="center">

# AI-Android-Demo 🤖

**大模型 × Android 应用落地示例 —— 端侧推理、对话式 AI、车载场景**

[![Stars](https://img.shields.io/github/stars/jason5200/AI-Android-Demo?style=social)](https://github.com/jason5200/AI-Android-Demo)
[![License](https://img.shields.io/github/license/jason5200/AI-Android-Demo)](https://github.com/jason5200/AI-Android-Demo)

</div>

---

## 📌 为什么有这个 Demo

大模型正在进入 Android 和车机，但「怎么把 LLM 跑起来、接到 App 里」的工程示例还很少。这个仓库用**最小可运行的代码**，演示一个大模型对话 App 的骨架，配合 [AAOS-Guide](https://github.com/jason5200/AAOS-Guide) 的 AI 系列文章理解原理。

## ✨ 特性

- ✅ 对话式聊天界面（消息列表 + 输入框）
- ✅ 抽象了 LLM 推理接口（可接入 MediaPipe / llama.cpp / 云端 API）
- ✅ 演示流式输出（逐 token 显示）
- ✅ 清晰的 TODO 标注，方便你替换成自己的模型

## 🗂️ 目录结构

```
AI-Android-Demo/
├── README.md
├── build.gradle
├── settings.gradle
└── app/
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/jason/aichat/
        │   ├── MainActivity.kt        # 对话界面 + 流式输出
        │   └── LlmClient.kt           # LLM 推理接口抽象
        └── res/
            ├── layout/activity_main.xml
            ├── values/strings.xml
            └── drawable/ic_launcher.xml
```

## 🚀 快速开始

1. 用 Android Studio 打开本工程。
2. 按需接入推理后端（三种方式任选）：

| 方式 | 说明 | 适合 |
|------|------|------|
| MediaPipe LLM | Google 官方，Android 友好 | 快速验证端侧 |
| llama.cpp | 跑 Llama 系量化模型 | 追求端侧性能 |
| 云端 API | 调用 OpenAI/通义等 | 最简单，无本地模型 |

3. 运行 app，开始对话。

## 📚 关联文章

| 文章 | 位置 |
|------|------|
| 《大模型上车：端侧推理的可行方案》 | [AAOS-Guide/05-ai-integration](https://github.com/jason5200/AAOS-Guide) |
| 《车载语音助手：从 ASR 到 LLM》 | AAOS-Guide/05-ai-integration |

## 🤝 参与共建

欢迎 PR 补充：MediaPipe 接入示例、llama.cpp 接入示例、车机语音场景。

## 📄 License

[Apache-2.0](LICENSE) © [jason5200](https://github.com/jason5200)
