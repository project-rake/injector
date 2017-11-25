/**
 * Created on 22.11.2017.
 */

package com.github.projectrake.injector;

import com.github.projectrake.injector.annotation.Patched;
import javassist.ClassPool;
import javassist.CtClass;

import java.util.List;

public class InjectorMain {
    private static final List<String> patchClasses = List.of(
            "com.mojang.authlib.GameProfile",
            "net.minecraft.server.v1_12_R1.ContainerEnchantTable"
    );

    public static void main(String[] args) throws Exception {
        System.setProperty("com.mojang.eula.agree", "true");

        for (String requestedClassname : patchClasses) {

            CtClass clazz = ClassPool.getDefault().get(requestedClassname);
            _assert(clazz.getAnnotation(Patched.class), "Missing @Patched annotation on " + requestedClassname);
            clazz.setName(requestedClassname);
            clazz.toClass();
        }

        org.bukkit.craftbukkit.Main.main(args);
    }

    private static void _assert(Object annotation) {
        _assert(annotation != null);
    }

    private static void _assert(boolean annotation) {
        if (!annotation) {
            throw new AssertionError();
        }
    }

    private static void _assert(Object annotation, String msg) {
        _assert(annotation != null, msg);
    }

    private static void _assert(boolean annotation, String msg) {
        if (!annotation) {
            throw new AssertionError(msg);
        }
    }
}
