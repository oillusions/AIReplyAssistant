package top.o_illusions.mcmods.aira.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import top.o_illusions.mcmods.aira.deepseek.DeepSeekHelper;
import top.o_illusions.mcmods.aira.deepseek.Model;

import java.util.Objects;

public class DeepSeek {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final DeepSeekHelper deepSeek;
    private JsonArray replyCandidate;

    public DeepSeek() {
        String apiKey = "sk-a205090a098c4968b72c4c332ef4c76d";
        String apiUrl = "https://api.deepseek.com/chat/completions";
        String cueWord = "你好DeepSeek!. " +
                "我希望你现在作为我[ID: Player<Num>, 或者叫'测试玩家']在Minecraft的对话助手[你可以自称为我]," +
                "在你接收到消息[其他玩家或系统发送的]后," +
                "帮助我生成2-6个从幽默到正常的回复,"  +
                "直接返回其内容," +
                "回复已JsonArray的格式[直接返回文本就行， 不需要富文本格式]" +
                "长度不得超过20字" +
                "请注意, 这是在一个多人服务器中," +
                "不要认错人[随意接话]" +
                "还有一件事, 我是摆烂玩家, 经常被人说是人机， 所以你的回复可以有一种超绝人机感" +
                "以下是我的聊天习惯: 动作描述词使用'[]'包裹, 比如: [看],[盯],[逃],[宕机], 事件描述词使用'()'包裹, 比如: (想了想), 这两个还能这样组合: '[看]在干嘛', (想着什么)阿巴...." +
                "请不要把'[]’包裹体和'()'包裹体弄混, '[]'包裹体是用来包裹当前状态, 动作的, '()'包裹体是用来包裹描述心里话的" +
                "以及， 不要局限于上面的举例， 你可以自由发挥";

        this.deepSeek = new DeepSeekHelper(apiUrl, apiKey, Model.CHAT, 100, cueWord);
    };


    public void onReceiveMessage(String messageContent, GameProfile gameProfile) {
        if (gameProfile == null) {
            addMsg(GameRole.SYSTEM, null, messageContent);
        } else {
            if (client.player != null && Objects.equals(gameProfile.getName(), client.player.getName().getString())) {
                addMsg(GameRole.THIS, null, messageContent);
            } else {
                addMsg(GameRole.PLAYER, gameProfile.getName(), messageContent);
            }
        }
    }

    private static class DeepSeekRequest implements Runnable {
        private DeepSeekHelper deepSeekHelper;
        private JsonObject  response;
        public DeepSeekRequest(DeepSeekHelper deepSeekHelper) {
            this.deepSeekHelper = deepSeekHelper;
        }

        @Override
        public void run() {
            this.response = deepSeekHelper.request();
            JsonArray tmp = new Gson().fromJson(response.get("content").getAsString(), JsonArray.class);
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.networkHandler.sendChatMessage(tmp.get(0).getAsString());
            }
        }
    }

    public void onTriggerGen() {
        System.out.println("触发生成");
        DeepSeekRequest request = new DeepSeekRequest(this.deepSeek);
        Thread thread = new Thread(request);
        thread.start();
//        JsonObject replyCandidateJson =
    }



    public void addMsg(GameRole gameRole, String Name, String messageContent) {
        switch (gameRole) {
            case PLAYER -> {
                this.deepSeek.addMsg(gameRole.getDsRole(), gameRole.getGameRole() + '[' + Name + ']' + "说:" + messageContent);
                this.onTriggerGen();
                break;
            }
            case THIS -> {
                this.deepSeek.addMsg(gameRole.getDsRole(), gameRole.getGameRole() + "说:" + messageContent);
                break;
            }
            case SYSTEM -> {
                this.deepSeek.addMsg(gameRole.getDsRole(), gameRole.getGameRole() + ": " + messageContent);
                this.onTriggerGen();
            }
        }
    }


}
