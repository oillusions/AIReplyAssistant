package top.o_illusions.mcmods.aira.deepseek;

public enum ResultFormat {
    TEXT("text"),
    JSON_OBJECT("json_object");


    private final String value;

    ResultFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
