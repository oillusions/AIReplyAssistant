package top.o_illusions.mcmods.aira.client;

import top.o_illusions.mcmods.aira.deepseek.Role;

public enum GameRole {
    PLAYER(Role.USER, "Player"),
    SYSTEM(Role.USER, "System"),
    THIS(Role.USER, "This")
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