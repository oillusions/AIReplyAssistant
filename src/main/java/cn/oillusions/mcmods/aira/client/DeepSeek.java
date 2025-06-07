package cn.oillusions.mcmods.aira.client;

import cn.oillusions.mcmods.aira.deepseek.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import cn.oillusions.mcmods.aira.Aira;
import cn.oillusions.mcmods.aira.client.config.PresetManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class DeepSeek {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final DeepSeekContext deepSeek;
    private JsonArray replyCandidate;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final JsonObject airaConfig;
    private final JsonObject style;
    private final String prompt;
    private final String brainwashPrompt;
    private boolean complete = true;
    private int retryCount = 0;

    public DeepSeek() {
        airaConfig = Aira.AIRA_CONFIG.getConfig().deepCopy();
        style = PresetManager.getStyle(airaConfig.get("current").getAsString()).getConfig().deepCopy();
        prompt = PresetManager.getFiller().filler(PresetManager.getPrompt(airaConfig.get("current").getAsString()).getConfig());
        brainwashPrompt = PresetManager.getFiller().filler(PresetManager.getBrainwashPrompt(airaConfig.get("current").getAsString()).getConfig());


        DeepSeekConfig deepSeekConfig = new DeepSeekConfig.Builder()
                .requestMode(true)
                .apiUrl(airaConfig.get("api_url").getAsString())
                .apiKey(airaConfig.get("api_key").getAsString())
                .model(Model.CHAT)
                .format(ResultFormat.JSON_OBJECT)
                .stream(false)

                .maxTokens(style.get("max_tokens").getAsInt())
                .topP(style.get("top_p").getAsFloat())
                .presencePenalty(style.get("presence_penalty").getAsFloat())
                .frequencyPenalty(style.get("frequency_penalty").getAsFloat())
                .systemPrompt(prompt)
                .build();
        deepSeek = new DeepSeekContext(deepSeekConfig);
        deepSeek.addListener((response -> {
            try {
                complete = true;
                JsonArray reply = gson.fromJson(response.getContent(), JsonObject.class).getAsJsonArray("responses");
                System.out.println(gson.toJson(reply));
                replyCandidate = reply;
                if (AiraClient.getInstance().isAutoReply()) {
                    if (client.player != null) {
                        client.player.networkHandler.sendChatMessage(reply.get(0).getAsString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (retryCount < airaConfig.get("max_retries").getAsInt()) {
                    deepSeek.addMessage(Role.SYSTEM, brainwashPrompt);
                    onTriggerGen();

                    System.out.println(Text.translatable("aira.error.warn.retry.message.message"));
                    sendToastMessage(
                            Text.translatable("aira.error.warn.retry.message.title"),
                            Text.literal(
                                    Text.literal("aira.error.warn.retry.message.message")
                                            .getString().formatted(retryCount))
                    );
                } else {
                    deepSeek.resetConversation();

                    System.out.println(Text.translatable("aira.error.warn.retry.timeout.message.message").getString());
                    sendToastMessage(
                            Text.translatable("aira.error.warn.retry.timeout.message.title"),
                            Text.translatable("aira.error.warn.retry.timeout.message.message")
                    );

                    retryCount = 0;
                    return;
                }
                retryCount++;
            }
        }));

        replyCandidate = new JsonArray();
        replyCandidate.add("空");
    }

    public void onReceiveMessage(String messageContent, String name, GameRole gameRole) {
        addMsg(gameRole, name, messageContent);
    }

    public void onTriggerGen() {
        if (complete) {
            complete = false;
            deepSeek.request();

            System.out.println(Text.translatable("aira.gen.message.message").getString());
            sendToastMessage(
                    Text.translatable("aira.gen.message.title"),
                    Text.translatable("aira.gen.message.message")
            );

            replyCandidate = new JsonArray();
            replyCandidate.add("生成中");
        } else {
            System.out.println(Text.translatable("aira.gen.wait.message.message").getString());
            sendToastMessage(
                    Text.translatable("aira.gen.wait.message.title"),
                    Text.translatable("aira.gen.wait.message.message")
            );
        }
    }

    private void sendToastMessage(Text title, Text message) {
        if (airaConfig.get("silent_message").getAsBoolean()) {
            return;
        }
        client.getToastManager().add(SystemToast.create(
                client,
                SystemToast.Type.LOW_DISK_SPACE,
                title,
                message
        ));
    }

    public void addMsg(GameRole gameRole, String name, String messageContent) {
        switch (gameRole) {
            case PLAYER, THIS -> {
                deepSeek.addMessage(gameRole.getDsRole(), name, messageContent);
            }
            case SYSTEM -> {
                deepSeek.addMessage(gameRole.getDsRole(), gameRole.getGameRole(), messageContent);
            }
        }
    }

    public void setReplyCandidate(JsonArray replyCandidate) {
        this.replyCandidate = replyCandidate;
    }

    public JsonArray getReplyCandidate() {
        return replyCandidate;
    }

    public JsonObject getStyle() {
        return style;
    }

    public String getPrompt() {
        return prompt;
    }

    public boolean isComplete() {
        return complete;
    }

    public int getRetryCount() {
        return retryCount;
    }
}
