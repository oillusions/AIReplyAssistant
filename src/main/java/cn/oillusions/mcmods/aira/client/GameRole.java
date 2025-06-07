package cn.oillusions.mcmods.aira.client;

import cn.oillusions.mcmods.aira.deepseek.Role;

public enum GameRole {
    PLAYER(Role.USER, "Player"),
    SYSTEM(Role.USER, "System"),
    THIS(Role.ASSISTANT, "This")
    ;
    private final Role dsRole;
    private final String gameRole;

    GameRole(Role role, String gameRole) {
        this.dsRole = role;
        this.gameRole = gameRole;
    }

    public Role getDsRole() {
        return dsRole;
    }

    public String getGameRole() {
        return gameRole;
    }
}