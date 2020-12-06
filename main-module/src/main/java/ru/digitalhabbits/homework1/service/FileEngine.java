package ru.digitalhabbits.homework1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static java.util.Arrays.stream;

public class FileEngine {
    private static final String RESULT_FILE_PATTERN = "results-%s.txt";
    private static final String RESULT_DIR = "results";
    private static final String RESULT_EXT = "txt";

    private static final Logger logger = LoggerFactory.getLogger(FileEngine.class);

    public boolean writeToFile(@Nonnull String text, @Nonnull String pluginName) {
        try (var fileWriter = new BufferedWriter(new FileWriter(Path.of(RESULT_DIR, String.format(RESULT_FILE_PATTERN, pluginName)).toString(), false))) {
            fileWriter.write(text);
            fileWriter.flush();

            return true;
        } catch (IOException exception) {
            logger.error(exception.getMessage(), exception);
        }

        return false;
    }

    public void cleanResultDir() {
        final String currentDir = System.getProperty("user.dir");
        final File resultDir = new File(Path.of(currentDir, RESULT_DIR).toUri());
        stream(resultDir.list((dir, name) -> name.endsWith(RESULT_EXT)))
                .forEach(fileName -> new File(resultDir + "/" + fileName).delete());
    }
}
