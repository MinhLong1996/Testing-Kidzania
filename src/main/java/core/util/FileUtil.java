package core.util;

import core.helper.LogHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {

    protected static final LogHelper logger = LogHelper.getInstance();

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static List<String> listAllFiles(String dir) {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), Integer.MAX_VALUE)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    public static List<String> listAllFileNames(String dir) {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), Integer.MAX_VALUE)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(e -> e.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    public static String getFileName(String filePath) {
        Path p = Paths.get(filePath);
        return p.getFileName().toString();
    }

    public static String getFileNameWithoutExt(String filePath) {
        return FilenameUtils.getBaseName(getFileName(filePath));
    }

    public static boolean isExist(String dir, String fileName) {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), Integer.MAX_VALUE)) {
            return stream.filter(Files::isRegularFile)
                    .anyMatch(e -> e.getFileName().toString().equals(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getPath(String dir, String fileName) {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), Integer.MAX_VALUE)) {
            return stream.filter(Files::isRegularFile)
                    .filter(e -> e.getFileName().toString().equals(fileName)).findFirst().orElse(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path toPath(String dir, String fileName) {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), Integer.MAX_VALUE)) {
            return stream.filter(Files::isRegularFile)
                    .filter(e -> e.getFileName().toString().equals(fileName))
                    .findFirst().orElse(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static long size(String dir, String fileName) {
        try {
            return Files.size(toPath(dir, fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeTexToFile(String str, String filePath) {
        final File file = new File(filePath);
        try {
            FileUtils.writeStringToFile(file, str, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFilePath(String dir, String fileName) {
        return getFilePath(dir, fileName, 30);
    }

    public static String getFilePath(String dir, String fileName, int timeout) {
        final String fileNameCI = "download";
        do {
            if (null != FileUtil.getPath(dir, fileName)) {
                return FileUtil.getPath(dir, fileName).toString();
            } else if (null != FileUtil.getPath(dir, fileNameCI)) {
                return FileUtil.getPath(dir, fileNameCI).toString();
            }
            logger.info("Waiting for {} file is downloaded: {}", fileName, timeout--);
            sleep(1000);
        } while (timeout > 0);
        return null;
    }

    public static void cleanDirectory(String directory) {
        File file = new File(directory);
        if (!file.exists()) {
            file.mkdir();
        } else {
            try {
                FileUtils.cleanDirectory(file);
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public static String getLastFileCreated(String directory) {
        try {
            Optional<Path> path = Files.list(Paths.get(directory))
                    .filter(f -> !Files.isDirectory(f))
                    .max(Comparator.comparingLong(f -> f.toFile().lastModified()));

            if (path.isPresent()) {
                return path.get().getFileName().toString();
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    public static String waitForFileIsDownLoaded(String directory, List<String> currentFiles) {
        List<String> newFiles = listAllFileNames(directory);
        int max = 30;
        while (newFiles.size() == currentFiles.size() && max > 0) {
            sleep(1000);
            newFiles = listAllFileNames(directory);
            max--;
        }

        return newFiles.stream().filter(e -> !currentFiles.contains(e)).findFirst().orElse(null);
    }
}