package com.mojang.authlib;

import com.mojang.authlib.properties.PropertyMap;
import com.github.projectrake.injector.annotation.Patched;
import com.github.projectrake.injector.events.EventSystem;
import com.github.projectrake.injector.events.GameProfileCreationEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

@Patched
public class GameProfile {
    private final UUID id;
    private final String name;
    private final PropertyMap properties = new PropertyMap();
    private boolean legacy;

    public GameProfile(UUID uUID, String string) {
        if (uUID == null && StringUtils.isBlank(string)) {
            throw new IllegalArgumentException("Name and ID cannot both be blank");
        }

        GameProfileCreationEvent ev = new GameProfileCreationEvent(uUID, string);
        EventSystem.getInstance().call(ev);
        UUID realUUID = ev.getUuid();
        String realName = ev.getName();

        if (realUUID == null && StringUtils.isBlank(realName)) {
            throw new IllegalArgumentException("Name and ID cannot both be blank");
        }

        this.id = realUUID;
        this.name = realName;
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

