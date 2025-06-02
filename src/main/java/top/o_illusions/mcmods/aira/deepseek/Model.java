package top.o_illusions.mcmods.aira.deepseek;

public enum Model {
    CHAT("deepseek-chat"),
    REASONER("deepseek-reasoner");


    private String value;

    Model(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
