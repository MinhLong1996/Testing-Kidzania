package core.util;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import core.env.Environment;
import core.env.PageObject;
import core.helper.Helper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ConvertJson {


    public static void main(String[] args) throws IOException {
        String folderPath = Helper.getConf("locator.folder") + File.separator + "converted";
        FileUtils.deleteDirectory(new File(folderPath));
        PageObject pageObject = Environment.INSTANCE.getPageObject();
        for (Map.Entry<String, JsonObject> entry : pageObject.getEntry()) {
            JsonObject jsonObject = entry.getValue();
            String fileName = jsonObject.get("name").getAsString();
            String filePath = folderPath + File.separator + fileName + ".json";
            File file = new File(filePath);
            file.getParentFile().mkdir();
            file.createNewFile();

            JsonGenerator j = new JsonFactory().createGenerator(file, JsonEncoding.UTF8);
            j.writeStartObject();
            j.writeStringField("name", jsonObject.get("name").getAsString());
            j.writeObjectFieldStart("detectors");
            j.writeStringField("web", jsonObject.get("detectors").getAsJsonObject().get("web").getAsString());
            j.writeEndObject();
            JsonArray elements = jsonObject.getAsJsonArray("elements");
            if (null == elements) {
                continue;
            }
            for (JsonElement element : elements) {
                String name = element.getAsJsonObject().get("name").getAsString();
                JsonObject object;
                try {
                    object = element.getAsJsonObject().get("locators").getAsJsonObject().get("web").getAsJsonObject();
                } catch (NullPointerException e) {
                    continue;
                }

                j.writeObjectFieldStart(name);
                j.writeObjectFieldStart("web");
                j.writeStringField("selector", object.get("selector").getAsString());
                j.writeStringField("strategy", object.get("strategy").getAsString());
                j.writeEndObject();
                j.writeEndObject();
            }


            j.writeEndObject();
            j.close();

            System.out.println("Converted: " + fileName + " ---> " + filePath);
        }
    }
}
