package core.env;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import core.helper.LogHelper;
import core.util.FileUtil;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PageObject {
    private static final LogHelper logger = LogHelper.getInstance();
    private final Map<String, JsonObject> jsonObjects;

    protected PageObject() {
        jsonObjects = new HashMap<>(5);
    }

    protected void loadFromDirectory(String dir) {
        logger.debug("Read all files from: {}", dir);
        List<String> files = FileUtil.listAllFiles(dir);
        for (String file : files) {
            loadObject(file);
        }
    }

    protected void loadObject(String filePath) {
        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            jsonObjects.put(FileUtil.getFileNameWithoutExt(filePath), jsonObject);
        } catch (IOException e) {
            logger.error("Error while reading page object file: {}", filePath);
            throw new RuntimeException(e);
        }
    }

    public JsonObject get(String key) {
        return jsonObjects.get(key);
    }

    public Set<Map.Entry<String, JsonObject>> getEntry() {
        return jsonObjects.entrySet();
    }
}
