package com.github.projectrake.injector;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 22.11.2017.
 */
public class Configuration {
    Map<String, String> injectionMappings = new HashMap<>();

    public Configuration() {

    }

    public Map<String, String> getInjectionMappings() {
        return injectionMappings;
    }

    public void setInjectionMappings(Map<String, String> injectionMappings) {
        this.injectionMappings = injectionMappings;
    }

    public static Configuration getDefaultConfiguration() {
        Configuration conf = new Configuration();

        conf.injectionMappings.put(
                "com.mojang.authlib.GameProfile",
                "com.mojang.authlib.yggdrasil.GameProfile"
        );

        conf.injectionMappings.put(
                "com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService",
                "com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService"
        );

        return conf;
    }
}
