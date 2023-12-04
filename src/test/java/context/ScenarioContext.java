package context;

import com.google.gson.JsonPrimitive;

public class ScenarioContext {

    private final ThreadLocal<TestContext> testContext = new ThreadLocal<>();

    private static ScenarioContext instance = null;

    private ScenarioContext() {
        testContext.set(new TestContext());
    }

    public static ScenarioContext instance() {
        if (instance == null) {
            instance = new ScenarioContext();
        }
        return instance;
    }

    public void setContext(String key, Object value) {
        if (testContext.get() == null) {
            testContext.set(new TestContext());
        }
        testContext.get().setData(key, value);
    }

    public <T> T getContext(String key) {
        return testContext.get().getData(key);
    }

    public Boolean isContains(String key) {
        return testContext.get().isContains(key);
    }

    public String translate(String key) {
        String ret = key;
        if (isContains(key)) {
            Object object = getContext(key);
            if (object instanceof JsonPrimitive) {
                ret = ((JsonPrimitive) object).getAsString();
            } else {
                return object.toString();
            }
        }
        return ret;
    }
}
