[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/oillusions/AIReplyAssistant)

# Minecraft 不智能回复助手 [aira]

**AIRA** 是一款为 Minecraft 设计的智能聊天助手模组，利用 DeepSeek 语言模型生成上下文感知的聊天回复建议，让您的多人游戏社交体验更加流畅！

## ✨ 功能槽点

- **智能回复生成** - 使用 DeepSeek API 生成自然语言回复
- **候选回复展示** - 在游戏内 HUD 界面显示多个回复选项
- **快捷键发送** - 简单按键组合即可发送选定回复
- **自动回复模式** - 自动响应其他玩家的消息
- **高度可定制** - 自定义提示词、API 参数和界面样式
- **上下文感知** - 智能识别玩家、系统消息和自身消息

## ⚙️ 配置禁忌

配置文件路径：`<游戏文件夹>/config/aira/aira.json`

### 配置示例：

```json
{
  "api_url": "https://api.deepseek.com/chat/completions",
  "api_key": "在此填写您的 DeepSeek API 密钥",
  "current": "default",
  "styles": ["default"]
}
```

### 🔧 自定义提示词
在以下路径创建和编辑提示词模板：`<游戏文件夹>/cueWordPreset/`

## 🎮 使用误区

### 基本操作
- **双击 Tab 键** - 生成回复候选列表
- **按住 Tab + 数字键 (1-6)** - 发送选定的候选回复

### 自动回复模式
在暂停菜单中切换自动回复模式：


### HUD 界面
候选回复显示在屏幕左上角：
- "测试" 标题
- 回复选项列表（最多 6 个）

## 🧩 非核心模块

| 模块 | 关键类 | 功能 |
|------|--------|------|
| **API 集成** | DeepSeekContext, DeepSeekConfig, DeepSeekResponse | 处理 API 请求和响应 |
| **用户界面** | ReplyHud, GameMenuScreenMixin, KeyTriggerListener | 渲染 HUD 并处理输入 |
| **配置管理** | JsonConfig, CueWordConfig, PresetManager | 管理设置和预设 |
| **核心逻辑** | AiraClient, DeepSeek, TextFiller | 协调模组功能 |

## ⚠️ 不重要注意事项

1. **需要 API 密钥** - 从 DeepSeek 平台获取 API 密钥
2. **网络连接要求** - 需要稳定网络访问 API
3. **响应时间** - 生成回复可能需要几秒钟
4. **使用限制** - 需遵守 DeepSeek API 的使用条款和限制
5. **成本考量** - 高频使用可能产生费用

## 📝 开源协议

本项目采用 GNU 通用公共许可证 v3.0 - 详见 LICENSE 文件。

## 📬 别联系我

如有任何问题或建议，请随时联系我们(虽然我不一定会注意到[坏笑])：

📧 **邮箱**: o-illusions@qq.com

---

**让 AI 助力您的 Minecraft 社交体验！** 🎮💬✨

---

**[坏笑]实际上这个README也是用DeepSeek写的(别问, 问就是我压根不会写, 况且这种事情用AI写很合理吧!)**