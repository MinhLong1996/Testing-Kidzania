package core.helper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import core.util.CustomStringUtil;
import core.util.JsonUtil;
import core.util.ScreenshotUtil;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.environment.UndefinedEnvironmentVariableException;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class Helper {
    private Properties prop;
    private final CustomStringUtil csUtil = new CustomStringUtil();
    private final String globFile = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "glob.txt"});
    private final JsonUtil jsonUtil = new JsonUtil();
    protected static final LogHelper logger = LogHelper.getInstance();

    private Properties getProp() {
        if (prop == null) {
            prop = new Properties();
            String[] arr = new String[]{"src", "test", "resources", "config.properties"};

            try {
                InputStream is = Files.newInputStream(Paths.get(csUtil.getFullPathFromFragments(arr)));
                prop.load(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return prop;
    }

    public String getConfig(String key) {
        return getProp().getProperty(key);
    }

    public void writeStepFailed() {
        Assert.fail();
    }

    public void writeStepFailed(String message) {
        logger.fail(message);
    }

    public void compareEqual(Object source, Object target) {
        Assert.assertEquals(source, target);
    }

    public void compareNotEqual(Object source, Object target) {
        Assert.assertNotEquals(source, target);
    }

    public void writeLogToReport(String title, String content) {
        Serenity.recordReportData().withTitle(title).andContents(content);
    }

    public String readGlobalParam(String key) {
        try {
            FileReader reader = new FileReader(globFile);
            BufferedReader br = new BufferedReader(reader);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.split("=")[0].trim().equalsIgnoreCase(key)) {
                    reader.close();
                    return line.split("=")[1].trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeGlobalParam(String key, String oldVal, String newVal) {
        File fileToBeModified = new File(globFile);
        String oldContent = "";
        BufferedReader reader = null;
        FileWriter writer = null;

        try {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            String line = reader.readLine();
            while (line != null) {
                oldContent = oldContent + line + System.lineSeparator();
                line = reader.readLine();
            }

            String newContent = oldContent.replaceAll(key + "=" + oldVal, key + "=" + newVal);
            writer = new FileWriter(fileToBeModified);
            writer.write(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void takeScreenshot(WebDriver driver) {
        String key = "screenshotID";
        int ssId = Integer.parseInt(Objects.requireNonNull(readGlobalParam(key)));
        int nextSSID = ssId + 1;

        String fileName = "evidence_" + ssId;
        ScreenshotUtil.takeSnapShot(driver, fileName);
        writeLogToReport("<img src='" + fileName + ".png'>", fileName);
        writeGlobalParam(key, String.valueOf(ssId), String.valueOf(nextSSID));
    }

    public void embedScreenshot(String filePath, String fileName) {
        String destFile = csUtil.getFullPathFromFragments(new String[]{"target", "site", "serenity", fileName + ".png"});

        try {
            FileUtils.copyFile(new File(filePath), new File(destFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeLogToReport("<img src='" + fileName + ".png'>", fileName);
    }

    public String randomString(int len) {
        final String AB = "文字以内で入力してください";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public List<Integer> convertToInt(List<String> listString) {
        return listString.stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    public String encodePassPhrase(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    public String decodePassPhrase(String encodedPassword) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedPassword);
        return new String(decodedBytes);
    }

    public String readTextFile(String filePath) throws Exception {
        StringBuilder sb = new StringBuilder();
        FileReader input = new FileReader(filePath);
        BufferedReader bufRead = new BufferedReader(input);
        try {
            String line = bufRead.readLine();
            while (line != null) {
                sb.append(line).append('\n');
                line = bufRead.readLine();
            }
        } finally {
            bufRead.close();
            input.close();
        }
        return sb.toString();
    }

    public void writeToTextFile(String filePath, String textToWrite) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(new File(filePath).toPath())))) {
            writer.write(textToWrite);
            writer.newLine();  // method provided by BufferedWriter
        } catch (IOException ignored) {
        }
    }

    public boolean isContained(String filePath, String textToFind) throws Exception {
        Path input = Paths.get(filePath);
        BufferedReader bufRead = Files.newBufferedReader(input);
        boolean found = false;

        try {
            String line = bufRead.readLine();
            while (line != null) {
                logger.info(line.trim());
                if (line.trim().equals(textToFind)) {
                    found = true;
                    logger.info(line.trim());
                    break;
                } else
                    line = bufRead.readLine();
            }
        } finally {
            bufRead.close();
        }
        return found;
    }

    public JsonObject readJsonFile(String filePath) {
        Gson gson = new Gson();
        Reader reader;
        try {
            reader = Files.newBufferedReader(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return gson.fromJson(reader, JsonObject.class);
    }

    public JsonObject readJsonDataFile(String fileName) {
        String filePath = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "data", fileName + ".json"});
        return readJsonFile(filePath);
    }

    public String readJsonByPath(JsonObject jsonObject, String jsonPath) {
        String value = jsonUtil.fetchJsonStringFromJsonObject(jsonObject, jsonPath);
        return csUtil.getPureString(value);
    }

    public static String getConf(String key) {
        try {
            EnvironmentVariables variables = SystemEnvironmentVariables.createEnvironmentVariables();
            return EnvironmentSpecificConfiguration.from(variables).getProperty(key);
        } catch (UndefinedEnvironmentVariableException e) {
            return null;
        }
    }

    public void parseAllKeys(Set<String> keys, JsonObject jsonObject) {
        jsonUtil.parseAllKeys(keys, jsonObject);
    }

    public String getUploadFilePath(String fileName) {
        return csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "upload", fileName});
    }

    public void delaySync(double sec) {
        try {
            Thread.sleep((long) (sec * 1000));
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }


    public String loadTestDataIntoParam(String param) throws Exception {
        if (!param.startsWith("<data:"))
            return param;
        else {
            String fieldName = param.replace("<data:", "");
            String testDataFile = "";

            fieldName = fieldName.substring(0, fieldName.length() - 1);
            String jsonPath = "$." + fieldName;

            File jsonFile = new File(
                    System.getProperty("user.dir") + "\\src\\test\\resources\\data\\" + testDataFile + ".json");

            return JsonPath.read(jsonFile, jsonPath);
        }
    }

    public boolean isFileExists(String[] filePath) {
        File f = new File(csUtil.getFullPathFromFragments(filePath));
        return f.exists();
    }

    public boolean isFileExists(String filePath, int timeout) throws InterruptedException {
        //sample file path "/src/test/resources/download"
        String[] arrayPath = filePath.split("/");
        boolean found;
        int count = 0;
        do {
            found = isFileExists(arrayPath);
            count = count + 1;
            Thread.sleep(1000);
        } while (!found && count < timeout);

        return found;
    }

    public boolean isFileDownloaded(String[] filePath, int fileSize, int timeout) {
        File f;
        int i = 0;
        do {
            if (isFileExists(filePath)) {
                delaySync(1);
                f = new File(csUtil.getFullPathFromFragments(filePath));
                logger.info("FileUtils.sizeOf(f) = " + FileUtils.sizeOf(f));
                return FileUtils.sizeOf(f) > fileSize;
            } else {
                logger.info("i = " + i);
                delaySync(1);
                i += 1;
            }
        } while (i < timeout);
        return false;
    }

    public void deleteAllFiles(String[] directory) throws IOException {
        FileUtils.cleanDirectory(new File(csUtil.getFullPathFromFragments(directory)));
    }

    public void deleteFile(String filePath) {
        File f = new File(filePath);
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
    }
}
