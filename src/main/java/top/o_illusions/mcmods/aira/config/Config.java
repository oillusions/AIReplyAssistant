package top.o_illusions.mcmods.aira.config;

import java.util.function.Consumer;

public interface Config<T> {
    void load();

    void save();

    void reset();

    T getConfig();

    void updateConfig(T newConfig);

    T getDefaultConfig();

    void addChangeListener(Consumer<T> listener);


}
