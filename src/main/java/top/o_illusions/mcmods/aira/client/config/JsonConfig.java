package top.o_illusions.mcmods.aira.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import top.o_illusions.mcmods.aira.config.Config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JsonConfig<T extends JsonElement> implements Config<T> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private final Path configFilePath;
    private final T defaultConfig;
    private T config;
    private boolean isModified = false;
    private final List<Consumer<T>> listeners = new ArrayList<>();


    public JsonConfig(Path configFilePath, T defaultConfig) {
        this.configFilePath = configFilePath;
        this.defaultConfig = defaultConfig;
        this.config = deepCopyJson(defaultConfig);

        this.load();
    }

    @Override
    public void load() {
        try {
            if (Files.exists(configFilePath)) {
                String jsonStr = Files.readString(configFilePath);
                JsonElement loaded = JsonParser.parseString(jsonStr);
                if (loaded.getClass().equals(defaultConfig.getClass())) {
                    config = (T) loaded;
                } else {
                    config = deepCopyJson(defaultConfig);
                }
            } else {
                config = (T) defaultConfig.deepCopy();

                save();
            }
            isModified = false;
        } catch (Exception e) {

        }

    }

    @Override
    public void save() {
        try {
            if (Files.exists(configFilePath)) {
                Files.writeString(configFilePath, gson.toJson(config));
                isModified = false;
            } else {
                Files.createDirectories(configFilePath.getParent());
                Files.createFile(configFilePath);

                save();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void reset() {
        config = deepCopyJson(defaultConfig);
        isModified = false;
        notifyListener();
    }

    @Override
    public T getConfig() {
        return config;
    }

    @Override
    public void updateConfig(T newConfig) {
        config = newConfig;
        isModified = true;
        notifyListener();
    }
    @Override
    public T getDefaultConfig() {
        return this.defaultConfig;
    }

    @Override
    public void addChangeListener(Consumer<T> listener) {

    }

    private void notifyListener() {
        for (Consumer<T> listener : listeners) {
            listener.accept(config);
        }
    }

    private T deepCopyJson(T element) {
        return (T) JsonParser.parseString(gson.toJson(element));
    }

    public Path getConfigFilePath() {
        return configFilePath;
    }

    public String getConfigFileName() {
        return configFilePath.getFileName().toString();
    }

    @Override
    public boolean isModified() {
        return isModified;
    }
}
