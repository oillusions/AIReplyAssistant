package top.o_illusions.mcmods.aira.deepseek;

public enum ResponseType {
    TEXT("text"),
    JSON_OBJECT("json_object");


    private final String value;

    ResponseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
