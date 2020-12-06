package ru.digitalhabbits.homework1.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.digitalhabbits.homework1.plugin.PluginInterface;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

public class PluginEngine {
    private static final Logger logger = LoggerFactory.getLogger(PluginEngine.class);

    @Nonnull
    public <T extends PluginInterface> String applyPlugin(@Nonnull Class<T> cls, @Nonnull String text) {
        try {
            var pluginInterface = cls.getConstructor().newInstance();
            var pluginResult = pluginInterface.apply(text);
            if (pluginResult != null) {
                return pluginResult;
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            logger.error(exception.getMessage(), exception);
        }

        return StringUtils.EMPTY;
    }
}
