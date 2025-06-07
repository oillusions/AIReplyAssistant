package cn.oillusions.mcmods.aira.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TextFiller {
    protected final List<Provider> textProviders = new ArrayList<>();

    public void register(String key, Supplier<String> supplier) {
        textProviders.add(new Provider("{?%s}".formatted(key), supplier));
    }

    public String filler(String input) {
        for (Provider provider : textProviders) {
            input = input.replace(provider.key, provider.supplier.get());
        }

        return input;
    }

    protected record Provider(String key, Supplier<String> supplier) {
    }
}
