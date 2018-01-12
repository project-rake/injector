package com.github.projectrake.sagittarius;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Created on 08/01/2018.
 */
public class InjectionClassLoader extends ClassLoader {
    private final Logger LOG = LogManager.getLogger(InjectionClassLoader.class);
    private Set<URI> patchedPaths = new HashSet<>(), realPaths = new HashSet<>();
    //private Map<String, Class<?>> loadedClasses = new HashMap<>();

    public InjectionClassLoader(ClassLoader parent, Collection<URI> patchedPaths, Collection<URI> realPaths) {
        super("InjectionClassLoader", parent);
        this.patchedPaths.addAll(patchedPaths);
        this.realPaths.addAll(realPaths);
        LOG.debug("Resources: " + patchedPaths + " / " + realPaths);
    }

    public InjectionClassLoader(Collection<URI> patchedPaths, Collection<URI> realPaths) {
        this(InjectionClassLoader.class.getClassLoader(), patchedPaths, realPaths);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedclazz = findLoadedClass(name);
        if (loadedclazz != null) {
            return loadedclazz;
        }

        for (URI uri : patchedPaths) {
            Class<?> clazz = tryLoadClassFrom(uri, name, resolve);
            if (clazz != null) {
                LOG.debug("Found " + clazz + " in " + uri);
                return clazz;
            }
        }

        for (URI uri : realPaths) {
            Class<?> clazz = tryLoadClassFrom(uri, name, resolve);
            if (clazz != null) {
                return clazz;
            }
        }

        return super.loadClass(name, resolve);
    }

    private Class<?> tryLoadClassFrom(URI uri, String className, boolean resolve) {
        if (uri.getScheme().equalsIgnoreCase("file")) {
            if (uri.getPath().toLowerCase().endsWith(".jar")) {
                return loadClassFromJar(uri.getPath(), className, resolve);
            } else {
                LOG.error("Can't load from " + uri);
            }
        }

        return null;
    }

    private Class<?> loadClassFromJar(String path, String name, boolean resolve) {
        try {
            JarFile file = new JarFile(new File(path));
            String entryName = name.replaceAll("\\.", "/").trim();
            if (!entryName.toLowerCase().endsWith(".class")) {
                entryName += ".class";
            }

            ZipEntry entry = file.getEntry(entryName);

            if (entry != null) {
                try (InputStream in = file.getInputStream(entry)) {
                    byte[] bytes = readFull(in);
                    return defineClass(name, bytes, 0, bytes.length);
                }
            } else {
                //LOG.warn("Can't find " + name + " as " + entryName + " in " + path);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private byte[] readFull(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int read;
        while ((read = in.read()) != -1) {
            out.write(read);
        }

        return out.toByteArray();
    }
}
