package com.mojang.authlib;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Created on 22.11.2017.
 */
public class GameProfileCreationEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private UUID uuid;
    private String name;

    public GameProfileCreationEvent(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GameProfileCreationEvent{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                "}";
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
