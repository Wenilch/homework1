package ru.digitalhabbits.homework1.plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrequencyDictionaryPlugin implements PluginInterface {

    private final Pattern pattern = Pattern.compile("(\\b[a-zA-Z][a-zA-Z.0-9]*\\b)");
    private final String pluginReturnFormat = "%s %d\n";

    @Nullable
    @Override
    public String apply(@Nonnull String text) {
        TreeMap<String, Integer> wordCounter = new TreeMap<>();

        Matcher wordMatcher = pattern.matcher(text);
        while (wordMatcher.find()) {
            wordCounter.compute(wordMatcher.group().toLowerCase(), (key, value) -> value != null ? value + 1 : 1);
        }

        StringBuilder stringBuilder = new StringBuilder(wordCounter.size());
        wordCounter
                .keySet().stream()
                .sorted()
                .forEach(key -> stringBuilder.append(String.format(pluginReturnFormat, key, wordCounter.get(key))));

        return stringBuilder.toString();
    }
}
