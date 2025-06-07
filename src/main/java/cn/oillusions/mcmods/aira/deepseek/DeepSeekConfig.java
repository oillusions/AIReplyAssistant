package cn.oillusions.mcmods.aira.deepseek;

public class DeepSeekConfig {
    private boolean requestMode = false;
    private String apiUrl = "https://api.deepseek.com/chat/completions";
    private String apiKey = "sk-xxx";
    private Model model = Model.CHAT;
    private ResultFormat format = ResultFormat.TEXT;
    private boolean stream = false;

    private int maxTokens = 2048;
    private float topP = 0.6f;
    private float presencePenalty = 0.5f;
    private float frequencyPenalty = 0.5f;

    private String systemPrompt = "你是一只乐于助人的DeepSeek小姐";


    public static class Builder {
        private final DeepSeekConfig config = new DeepSeekConfig();

        public Builder requestMode(boolean requestMode) {
            config.requestMode = requestMode;
            return this;
        }

        public Builder apiUrl(String url) {
            config.apiUrl = url;
            return this;
        }

        public Builder apiKey(String key) {
            config.apiKey = key;
            return this;
        }

        public Builder model(Model model) {
            config.model = model;
            return this;
        }

        public Builder format(ResultFormat format) {
            config.format = format;
            return this;
        }

        public Builder stream(boolean stream) {
            config.stream = stream;
            return this;
        }

        public Builder maxTokens(int maxTokens) {
            config.maxTokens = maxTokens;
            return this;
        }

        public Builder topP(float top_p) {
            config.topP = top_p;
            return this;
        }

        public Builder presencePenalty(float presencePenalty) {
            config.presencePenalty = presencePenalty;
            return this;
        }

        public Builder frequencyPenalty(float frequencyPenalty) {
            config.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public Builder systemPrompt(String systemPrompt) {
            config.systemPrompt = systemPrompt;
            return this;
        }

        public DeepSeekConfig build() {
            return config;
        }
    }

    public boolean isRequestMode() {
        return requestMode;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public Model getModel() {
        return model;
    }

    public ResultFormat getFormat() {
        return format;
    }

    public boolean isStream() {
        return stream;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public float getTopP() {
        return topP;
    }

    public float getPresencePenalty() {
        return presencePenalty;
    }

    public float getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }
}
