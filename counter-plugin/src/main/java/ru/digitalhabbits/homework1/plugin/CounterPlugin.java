package ru.digitalhabbits.homework1.plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CounterPlugin implements PluginInterface {

    private final Pattern pattern = Pattern.compile("(\\b[a-zA-Z][a-zA-Z.0-9]*\\b)");

    @Nullable
    @Override
    public String apply(@Nonnull String text) {
        int lines = text.split("\r\n|\r|\n").length;
        int words = 0;
        int letters = text.length();

        Matcher wordMatcher = pattern.matcher(text);
        while (wordMatcher.find()) {
            words++;
        }

        return String.format("%d;%d;%d", lines, words, letters);
    }
}
