package com.mojang.authlib;

import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Created on 22.11.2017.
 */
public class CompleteGameProfileCreationEvent extends GameProfileCreationEvent {
    private static final HandlerList handlers = new HandlerList();

    public CompleteGameProfileCreationEvent(UUID uuid, String name) {
        super(uuid, name);
    }

    @Override
    public String toString() {
        return "CompleteGameProfileCreationEvent{" +
                "uuid=" + getUuid() +
                ", name='" + getName() + '\'' +
                "}";
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
