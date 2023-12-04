package core.util;

import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;

import java.util.Set;

public class JsonUtil {

    private Object readJsonObject(JsonObject jsonObject, String jsonPath) {
        try {
            Configuration conf = Configuration.builder().jsonProvider(new GsonJsonProvider()).build();
            return JsonPath.using(conf).parse(jsonObject).read(jsonPath);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new IllegalArgumentException(jsonPath);
        }

    }

    public JsonObject fetchJsonObject(JsonObject jsonObject, String jsonPath) {
        return (JsonObject) readJsonObject(jsonObject, jsonPath);
    }

    public String fetchJsonStringFromJsonObject(JsonObject jsonObject, String jsonPath) {
        return readJsonObject(jsonObject, jsonPath).toString();
    }

    public String getValueFromJsonObject(JsonObject jo, String key) {
        CustomStringUtil customStringUtil = new CustomStringUtil();
        return customStringUtil.getPureString(jo.getAsJsonObject().get(key).toString());
    }

    public void parseAllKeys(Set<String> keys, JsonObject object) {
        keys.addAll(object.keySet());
        object.entrySet().stream().filter(entry -> entry.getValue().isJsonObject()).forEach(entry -> parseAllKeys(keys, (JsonObject) entry.getValue()));
        object.entrySet().stream().filter(entry -> entry.getValue().isJsonArray()).forEach(entry -> entry.getValue().getAsJsonArray().forEach(subEntry -> parseAllKeys(keys, (JsonObject) subEntry)));
    }
}
