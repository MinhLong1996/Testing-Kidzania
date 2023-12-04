package core.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class DragAndDropHelper {
    protected static final LogHelper logger = LogHelper.getInstance();
    public static String dragAndDropJS;

    static {
        dragAndDropJS = init("src/main/resources/drag-drop.js");
    }

    private static String init(String jsFile) {
        String dragAndDropJS = null;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(jsFile))) {
            dragAndDropJS = br
                    .lines()
                    .collect(Collectors.joining(" "));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return dragAndDropJS;
    }
}