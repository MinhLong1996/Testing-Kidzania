package context;

import java.util.HashMap;
import java.util.Map;

public class TestContext {
	private Map<String, Object> sharedData = new HashMap<>();

	TestContext() {
	}

	public void setData(String key, Object value) {
		this.sharedData.put(key, value);
	}

	public Boolean isContains(String key) {
		return this.sharedData.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(String key) {
		return (T) this.sharedData.get(key);
	}
}
