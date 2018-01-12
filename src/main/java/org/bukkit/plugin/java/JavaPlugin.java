/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  java.io.File
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.Reader
 *  java.lang.Class
 *  java.lang.ClassLoader
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.net.URL
 *  java.net.URLConnection
 *  java.nio.charset.Charset
 *  java.util.List
 *  java.util.Locale
 *  java.util.logging.Level
 *  java.util.logging.Logger
 */
package org.bukkit.plugin.java;

import com.github.projectrake.sagittarius.annotation.Patched;
import com.google.common.base.Charsets;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginLogger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@Patched
public abstract class JavaPlugin
        extends PluginBase {
    private boolean isEnabled = false;
    private PluginLoader loader = null;
    private Server server = null;
    private File file = null;
    private PluginDescriptionFile description = null;
    private File dataFolder = null;
    private ClassLoader classLoader = null;
    private boolean naggable = true;
    private FileConfiguration newConfig = null;
    private File configFile = null;
    private PluginLogger logger = null;

    public JavaPlugin() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof PluginClassLoader)) {
            throw new IllegalStateException("JavaPlugin requires " + PluginClassLoader.class.getName());
        }
        ((PluginClassLoader) classLoader).initialize(this);
    }

    protected JavaPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (classLoader instanceof PluginClassLoader) {
            throw new IllegalStateException("Cannot use initialization constructor at runtime");
        }
        this.init(loader, loader.server, description, dataFolder, file, classLoader);
    }

    @Override
    public final File getDataFolder() {
        return this.dataFolder;
    }

    @Override
    public final PluginLoader getPluginLoader() {
        return this.loader;
    }

    @Override
    public final Server getServer() {
        return this.server;
    }

    @Override
    public final boolean isEnabled() {
        return this.isEnabled;
    }

    protected File getFile() {
        return this.file;
    }

    @Override
    public final PluginDescriptionFile getDescription() {
        return this.description;
    }

    @Override
    public FileConfiguration getConfig() {
        if (this.newConfig == null) {
            this.reloadConfig();
        }
        return this.newConfig;
    }

    protected final Reader getTextResource(String file) {
        InputStream in = this.getResource(file);
        return in == null ? null : new InputStreamReader(in, Charsets.UTF_8);
    }

    @Override
    public void reloadConfig() {
        this.newConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defConfigStream = this.getResource("config.yml");
        if (defConfigStream == null) {
            return;
        }
        this.newConfig.setDefaults(YamlConfiguration.loadConfiguration((Reader) new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    @Override
    public void saveConfig() {
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException ex) {
            this.logger.log(Level.SEVERE, "Could not save config to " + (Object) this.configFile, (Throwable) ex);
        }
    }

    @Override
    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            this.saveResource("config.yml", false);
        }
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals((Object) "")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
        InputStream in = this.getResource(resourcePath = resourcePath.replace('\\', '/'));
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + (Object) this.file);
        }
        File outFile = new File(this.dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf(47);
        File outDir = new File(this.dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        try {
            if (!outFile.exists() || replace) {
                int len;
                FileOutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                this.logger.log(Level.WARNING, "Could not save " + outFile.getName() + " to " + (Object) outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            this.logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + (Object) outFile, (Throwable) ex);
        }
    }

    @Override
    public InputStream getResource(String filename) {
        URL url;
        block4:
        {
            if (filename == null) {
                throw new IllegalArgumentException("Filename cannot be null");
            }
            try {
                url = this.getClassLoader().getResource(filename);
                if (url != null) break block4;
                return null;
            } catch (Exception iOException) {
                return null;
            }
        }

        try {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (Exception e) {
            return null;
        }
    }

    protected final ClassLoader getClassLoader() {
        return this.classLoader;
    }

    protected final void setEnabled(boolean enabled) {
        if (this.isEnabled != enabled) {
            this.isEnabled = enabled;
            if (this.isEnabled) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        }
    }

    final void init(PluginLoader loader, Server server, PluginDescriptionFile description, File dataFolder, File file, ClassLoader classLoader) {
        this.loader = loader;
        this.server = server;
        this.file = file;
        this.description = description;
        this.dataFolder = dataFolder;
        this.classLoader = classLoader;
        this.configFile = new File(dataFolder, "config.yml");
        this.logger = new PluginLogger(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    public PluginCommand getCommand(String name) {
        String alias = name.toLowerCase(Locale.ENGLISH);
        PluginCommand command = this.getServer().getPluginCommand(alias);
        if (command == null || command.getPlugin() != this) {
            command = this.getServer().getPluginCommand(String.valueOf((Object) this.description.getName().toLowerCase(Locale.ENGLISH)) + ":" + alias);
        }
        if (command != null && command.getPlugin() == this) {
            return command;
        }
        return null;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return null;
    }

    @Override
    public final boolean isNaggable() {
        return this.naggable;
    }

    @Override
    public final void setNaggable(boolean canNag) {
        this.naggable = canNag;
    }

    @Override
    public final Logger getLogger() {
        return this.logger;
    }

    public String toString() {
        return this.description.getFullName();
    }

    public static <T extends JavaPlugin> T getPlugin(Class<T> clazz) {
        Validate.notNull(clazz, "Null class cannot have a plugin");
        if (!JavaPlugin.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz + " does not extend " + JavaPlugin.class);
        }
        ClassLoader cl = clazz.getClassLoader();
        if (!(cl instanceof PluginClassLoader)) {
            throw new IllegalArgumentException(clazz + " is not initialized by " + PluginClassLoader.class);
        }
        JavaPlugin plugin = ((PluginClassLoader) cl).plugin;
        if (plugin == null) {
            throw new IllegalStateException("Cannot get plugin for " + clazz + " from a static initializer");
        }
        return (T) ((JavaPlugin) clazz.cast((Object) plugin));
    }

    public static JavaPlugin getProvidingPlugin(Class<?> clazz) {
        Validate.notNull(clazz, "Null class cannot have a plugin");
        ClassLoader cl = clazz.getClassLoader();
        if (!(cl instanceof PluginClassLoader)) {
            throw new IllegalArgumentException(clazz + " is not provided by " + PluginClassLoader.class);
        }
        JavaPlugin plugin = ((PluginClassLoader) cl).plugin;
        if (plugin == null) {
            throw new IllegalStateException("Cannot get plugin for " + clazz + " from a static initializer");
        }
        return plugin;
    }
}

