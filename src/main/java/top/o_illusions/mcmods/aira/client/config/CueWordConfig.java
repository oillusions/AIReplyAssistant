package top.o_illusions.mcmods.aira.client.config;

import top.o_illusions.mcmods.aira.config.Config;
import top.o_illusions.mcmods.aira.util.TextFiller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class CueWordConfig implements Config<String> {
    private final TextFiller filler = new TextFiller();
    private final Path configFilePath;
    private String systemCue = "";


    public CueWordConfig(Path configFilePath) {
        this.configFilePath = configFilePath;

        load();
    }

    @Override
    public void load() {
        try {
            if (Files.exists(configFilePath)) {

            }
        } catch (Exception e) {

        }
    }

    @Override
    public void save() {

    }

    @Override
    public void reset() {

    }

    @Override
    public String getConfig() {
        return "";
    }

    @Override
    public void updateConfig(String newConfig) {

    }

    @Override
    public String getDefaultConfig() {
        return "";
    }

    @Override
    public void addChangeListener(Consumer<String> listener) {

    }

    public Path getConfigFilePath() {
        return configFilePath;
    }

    public TextFiller getFiller() {
        return filler;
    }

    public String getSystemCue() {
        return systemCue;
    }
}
