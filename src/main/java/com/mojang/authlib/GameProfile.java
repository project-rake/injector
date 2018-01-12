package com.mojang.authlib;

import com.github.projectrake.sagittarius.annotation.Patched;
import com.mojang.authlib.properties.PropertyMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import java.util.Objects;
import java.util.UUID;

@Patched
public class GameProfile {
    private final Logger LOG = LogManager.getLogger(GameProfile.class);
    private final UUID id;
    private final String originalName;
    private final String name;
    private final PropertyMap properties = new PropertyMap();
    private boolean legacy;

    public GameProfile(UUID uUID, String name) {
        originalName = name;

        if (uUID != null && name != null) {
            CompleteGameProfileCreationEvent cev = new CompleteGameProfileCreationEvent(uUID, name);
            tryCallEvent(cev);

            this.name = Objects.requireNonNull(cev.getName());
            this.id = Objects.requireNonNull(cev.getUuid());
        } else {
            GameProfileCreationEvent ev = new GameProfileCreationEvent(uUID, name);
            tryCallEvent(ev);

            this.name = ev.getName();
            this.id = ev.getUuid();
        }

        if (this.id == null && StringUtils.isBlank(this.name)) {
            throw new IllegalArgumentException("Name and ID cannot both be blank");
        }
    }

    private void tryCallEvent(Event ev) {
        Server serv = Bukkit.getServer();
        if (serv != null) {
            PluginManager manager = serv.getPluginManager();
            manager.callEvent(ev);
        } else {
            LOG.warn("Early construction detected. Server unavailable. This may be an error.");
        }
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public PropertyMap getProperties() {
        return this.properties;
    }

    public boolean isComplete() {
        return this.id != null && StringUtils.isNotBlank(this.getName());
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        GameProfile gameProfile = (GameProfile) object;
        if (this.id != null ? !this.id.equals((Object) gameProfile.id) : gameProfile.id != null) {
            return false;
        }
        if (this.name != null ? !this.name.equals((Object) gameProfile.name) : gameProfile.name != null) {
            return false;
        }
        return true;
    }

    public String getOriginalName() {
        return originalName;
    }

    public int hashCode() {
        int n = this.id != null ? this.id.hashCode() : 0;
        n = 31 * n + (this.name != null ? this.name.hashCode() : 0);
        return n;
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", (Object) this.id).append("name", this.name).append("properties", this.properties).append("legacy", this.legacy).toString();
    }

    public boolean isLegacy() {
        return this.legacy;
    }
}

