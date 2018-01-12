/**
 * Created on 22.11.2017.
 */

package com.github.projectrake.sagittarius;

import com.github.projectrake.sagittarius.annotation.Patched;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SagittariusMain {
    private final static Logger LOG = LogManager.getLogger(SagittariusMain.class);
    private static SagittariusMain instance;
    private boolean injected = false;

//    private static final List<String> patchClasses = List.of(
//            "com.mojang.authlib.GameProfile",
//            "org.bukkit.plugin.java.JavaPlugin",
//            "org.bukkit.plugin.java.PluginClassLoader"
//    );

    private SagittariusMain() {

    }

    public static void main(String[] args) throws Exception {
        SagittariusMain.getInstance().run(args);
    }

    private void run(String[] args) throws Exception {
        File spigotJar = findSpigotJar();
        LOG.info("Using " + spigotJar + " as spigot jar.");
        System.setProperty("com.mojang.eula.agree", "true");

        InjectionClassLoader loader = new InjectionClassLoader(
                Arrays.asList(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()),
                Arrays.asList(spigotJar.toURI())
        );
        Thread.currentThread().setContextClassLoader(loader);
        Class<?> clazz = loader.loadClass("org.bukkit.craftbukkit.Main");
        clazz.getMethod("main", new Class[]{String[].class}).invoke(null, new Object[]{args});

        /*
        for (String requestedClassname : patchClasses) {

            CtClass clazz = ClassPool.getDefault().get(requestedClassname);
            _assert(clazz.getAnnotation(Patched.class), "Missing @Patched annotation on " + requestedClassname);
            clazz.setName(requestedClassname);
            clazz.toClass();
        }

        injected = true;
        */
        //org.bukkit.craftbukkit.Main.main(args);
    }

    private File findSpigotJar() throws IOException {
        String matchregex = "spigot-([0-9]\\.?-?)+\\.jar";

        File base = new File(".");
        for (File file : base.listFiles()) {
            if (file.getName().matches(matchregex)) {
                return file;
            }
        }
        throw new IllegalStateException("Can't find any spigot jar.");
    }

    public boolean isInjected() {
        return true;
    }

    public static SagittariusMain getInstance() {
        if (instance == null) {
            instance = new SagittariusMain();
        }

        return instance;
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

    public static boolean requirePatched(String classname) {
        try {
            return requirePatched(Class.forName(classname));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean requirePatched(Class<?> aClass) {
        return aClass.getAnnotation(Patched.class) != null;
    }
}
