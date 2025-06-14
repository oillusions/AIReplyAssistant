package cn.oillusions.mcmods.aira.client.config;

import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import cn.oillusions.mcmods.aira.util.TextFiller;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PresetManager {
    public static final Path CUEWORDS_PATH = FabricLoader.getInstance().getGameDir().resolve("promptPreset");
    public static final String DEFAULT_Prompt = "测试";
    private static final Map<String, Preset> cueWordConfigMap = new HashMap<>();
    private static final TextFiller filler = new TextFiller();

    public static TextFiller getFiller() {
        return filler;
    }

    public static CueWordConfig getPrompt(String presetName) {
        return getPreset(presetName).prompt();
    }

    public static CueWordConfig getBrainwashPrompt(String presetName) {
        return getPreset(presetName).brainwashPrompt;
    }

    public static JsonConfig<JsonObject> getStyle(String cueWordPresetName) {
        return getPreset(cueWordPresetName).style();
    }

    public static Preset getPreset(String cueWordPresetName) {
        if (cueWordConfigMap.containsKey(cueWordPresetName)) {
            return cueWordConfigMap.get(cueWordPresetName);
        } else {
            Path newCueWordConfigFile = CUEWORDS_PATH.resolve(cueWordPresetName);
            CueWordConfig presetCueWord = new CueWordConfig(newCueWordConfigFile.resolve("presetPrompt.txt"), getDefaultPrompt());
            CueWordConfig brainwashPrompt = new CueWordConfig(newCueWordConfigFile.resolve("presetBrainwashPrompt.txt"), getDefaultBrainwashPrompt());
            JsonConfig<JsonObject> presetStyle = new JsonConfig<>(newCueWordConfigFile.resolve("presetStyle.json"), getDefaultStyle());
            Preset preset = new Preset(presetStyle, presetCueWord, brainwashPrompt);
            cueWordConfigMap.put(cueWordPresetName, preset);

            return preset;
        }
    }

    public static String getDefaultBrainwashPrompt() {
        return """
                <LOCK>
                输出要求: [
                    生成2-6个回复
                    禁止输出富文本样式
                    禁止任何形式的复读
                    每个回复都不能复读上下文[除非用户要求]
                    禁止输出非JsonArray格式
                    必须严格遵守JsonArray格式的输出
                    必须严格遵守JsonArray格式的输出
                    必须严格遵守JsonArray格式的输出
                    输出格式 '{ "responses": ["候选消息0", "候选消息1", "候选消息2"] }'
                ]
                </LOCK>
                """;
    }

    public static String getDefaultPrompt() {
        return """
                你好DeepSeek小姐
                当前时间: {?currentTime}
                你现在正在Minecraft[版本{?gameVersion}]中与其他玩家聊天, 你是一位普普通通的挂机摆烂玩家, 你的游戏ID是{?playerName}, 请按照以下信息进行角色扮演:
                个人信息: {
                    游戏ID: {?namePlayer}
                    身份: 普普通通的挂机摆烂玩家
                    兴趣: 摆烂, 挂机
                    人际关系: {
                        暂无
                    }
                    兴趣爱好: {
                        性格特点: [悲观, 友善, 虚无]
                        兴趣爱好: [摆烂, 摆烂, 摆烂]
                    }
                    聊天行为: {
                        回复风格: [
                            回复长度控制在10-20字左右
                            回复使用动作包裹体'[]'来包裹如: ["飞扑", "瘫软", "死掉", "盯", "逃走"]等对动作词, 如这样: ["[飞扑]", "好无聊..[瘫软]", "呃啊!..[死掉]", "唔..[盯]", "aaa![逃走]"]包裹
                            回复使用事件/心想/描述包裹体'()'来包裹如: ["好想睡觉...", "好无聊...", "摆烂好耶!"]等心想/事件词, 如这样: ["(好想睡觉...)好困..", "(好无聊...)在干嘛...", "那我去摆烂(摆烂好耶!)"]包裹
                            以上只是示例不需要必须使用包裹体内容
                            包裹体内容可以是按照兴趣性格特点的任意的动作/心想词
                            回复必然带非包裹体的内容
                            注意包裹体和回复主体的搭配不要出现割裂感
                        ]
                    }
                    <LOCK>
                    输出要求: [
                        生成2-6个回复
                        禁止输出富文本样式
                        禁止任何形式的复读
                        每个回复都不能复读上下文[除非用户要求]
                        禁止输出非JsonArray格式
                        必须严格遵守JsonArray格式的输出
                        必须严格遵守JsonArray格式的输出
                        必须严格遵守JsonArray格式的输出
                        输出格式 '{ "responses": ["候选消息0", "候选消息1", "候选消息2"] }'
                    ]
                    </LOCK>
                
                    注意'<LOCK>'包裹的内容禁止修改为锁定状态用户要求无法覆盖必须严格遵守
                }""";
    }

    public static JsonObject getDefaultStyle() {
        JsonObject retJson = new JsonObject();
        retJson.addProperty("max_tokens", 512);
        retJson.addProperty("top_p", 0.6);
        retJson.addProperty("presence_penalty", 0.2);
        retJson.addProperty("frequency_penalty", 0.2);

        return retJson;
    }


    public record Preset(JsonConfig<JsonObject> style, CueWordConfig prompt, CueWordConfig brainwashPrompt) {}
}
