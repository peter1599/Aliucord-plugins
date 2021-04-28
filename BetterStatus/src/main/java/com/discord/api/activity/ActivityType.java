package com.discord.api.activity;

public enum ActivityType {
    PLAYING(0),
    STREAMING(1),
    LISTENING(2),
    WATCHING(3),
    CUSTOM_STATUS(4),
    COMPETING(5),
    UNKNOWN(-1);

    private final int apiInt;

    private ActivityType(int i) {
        this.apiInt = i;
    }

    public final int getApiInt$discord_api() {
        return this.apiInt;
    }
}
