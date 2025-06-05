package top.o_illusions.mcmods.aira.client.config;

import top.o_illusions.mcmods.aira.config.Config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CueWordConfig implements Config<String> {
    private final Path configFilePath;
    private final String defaultCueWord;
    private String content;
    private boolean isModified = false;
    private final List<Consumer<String>> listeners = new ArrayList<>();


    public CueWordConfig(Path configFilePath, String defaultCueWord) {
        this.configFilePath = configFilePath;
        this.defaultCueWord = defaultCueWord;

        load();
    }

    @Override
    public void load() {
        try {
            if (Files.exists(configFilePath)) {
                content = Files.readString(configFilePath);
            } else {
                content = defaultCueWord;
            }
            isModified = false;
        } catch (Exception e) {

        }
        notifyListener();
    }

    @Override
    public void save() {
        try {
            if (Files.exists(configFilePath)) {
                Files.writeString(configFilePath, content);
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
        content = defaultCueWord;
        isModified = false;
        notifyListener();
    }

    @Override
    public String getConfig() {
        return content;
    }

    @Override
    public void updateConfig(String newConfig) {
        content = newConfig;
        isModified = true;
        notifyListener();
    }

    @Override
    public String getDefaultConfig() {
        return defaultCueWord;
    }

    @Override
    public void addChangeListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    @Override
    public boolean isModified() {
        return false;
    }

    private void notifyListener() {
        for (Consumer<String> listener : listeners) {
            listener.accept(content);
        }
    }

    public Path getConfigFilePath() {
        return configFilePath;
    }
}
