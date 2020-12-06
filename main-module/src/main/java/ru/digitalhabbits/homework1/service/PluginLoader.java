package ru.digitalhabbits.homework1.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.digitalhabbits.homework1.plugin.PluginInterface;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class PluginLoader {
    private static final Logger logger = LoggerFactory.getLogger(PluginLoader.class);

    private static final String PLUGIN_EXT = "jar";
    private static final String PACKAGE_TO_SCAN = "ru.digitalhabbits.homework1.plugin";
    private static final String CLASS_EXTENSION = ".class";

    @Nonnull
    public List<Class<? extends PluginInterface>> loadPlugins(@Nonnull String pluginDirName) {
        var plugins = new ArrayList<Class<? extends PluginInterface>>();
        var files = new File(pluginDirName).listFiles((dir, name) -> name.endsWith(PLUGIN_EXT));
        if (files != null) {
            for (File file : files) {
                var classPaths = new ArrayList<String>();
                try {
                    try (var jar = new JarFile(file);
                         var loader = new URLClassLoader(new URL[]{file.toURI().toURL()})) {
                        jar.stream().forEach(jarEntry -> composeClassFromJarEntry(jarEntry, classPaths));
                        classPaths.forEach(classPath -> composePluginInterface(classPath, loader, plugins));
                    }
                } catch (IOException exception) {
                    logger.error(exception.getMessage(), exception);
                }
            }
        }


        return plugins;
    }

    private void composeClassFromJarEntry(JarEntry jarEntry, List<String> classes) {
        var entryName = jarEntry.getName();
        if (entryName.endsWith(CLASS_EXTENSION)) {
            entryName = entryName.replace("/", ".");
            if (entryName.startsWith(PACKAGE_TO_SCAN)) {
                classes.add(entryName.replace(CLASS_EXTENSION, StringUtils.EMPTY));
            }
        }
    }

    private void composePluginInterface(String classPath, URLClassLoader urlClassLoader, ArrayList<Class<? extends PluginInterface>> plugins) {
        try {
            Class clazz = urlClassLoader.loadClass(classPath);
            var clazzInterfaces = clazz.getInterfaces();
            for (var clazzInterface : clazzInterfaces) {
                if (clazzInterface.equals(PluginInterface.class)) {
                    plugins.add(clazz);
                }
            }
        } catch (ClassNotFoundException exception) {
            logger.error(exception.getMessage(), exception);
        }
    }
}
