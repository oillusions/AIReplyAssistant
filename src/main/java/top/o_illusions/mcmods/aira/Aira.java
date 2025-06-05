package top.o_illusions.mcmods.aira;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import top.o_illusions.mcmods.aira.client.config.JsonConfig;


import java.nio.file.Path;

public class Aira implements ModInitializer {
    public static final String MOD_ID = "aira";
    public static final Path CONFIGS_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    public static final JsonConfig<JsonObject> AIRA_CONFIG = new JsonConfig<>(CONFIGS_PATH, defaultConfig());

    @Override
    public void onInitialize() {

    }

    public static JsonObject defaultConfig() {
        JsonObject defaultConfig = new JsonObject();
        JsonArray defaultStyle = new JsonArray();
        defaultConfig.addProperty("api_url", "https://api.deepseek.com/chat/completions");
        defaultConfig.addProperty("api_key", "sk-xxx");
        defaultConfig.addProperty("current", "default");

        defaultStyle.add("default");
        defaultConfig.add("styles", defaultStyle);

        return defaultConfig;
    }
}
