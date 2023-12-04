package core.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.ArrayMap;
import core.api.exception.TestContextException;
import core.util.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface Request {
	static final Logger LOGGER = LoggerFactory.getLogger(Request.class);

	@SuppressWarnings({ "java:S3011", "java:S2139" })
	public default Map<String, Object> getDefaultRequestParams() {
		try {
			Map<String, Object> requestParams = new ArrayMap<>();
			Field[] allFields = this.getClass().getDeclaredFields();
			for (Field field : allFields) {
				field.setAccessible(true);
				if (field.get(this) != null) {
					requestParams.put(field.getName(), field.get(this));
				}
			}
			return requestParams;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			LOGGER.error(String.format("Error in mapping DTO class [%s] to request params. %s",
					this.getClass().getSimpleName(), e.getMessage()));
			throw new TestContextException(String.format("Error in mapping DTO class [%s] to request params. %s",
					this.getClass().getSimpleName(), e.getMessage()), e);
		}

	}

	public default List<Field> getAllFields() {
		Field[] allFields = this.getClass().getDeclaredFields();
		List<Field> allFiledsWithAccessible = new ArrayList<>();
		for (Field field : allFields) {
			field.setAccessible(true);
			allFiledsWithAccessible.add(field);
		}
		return allFiledsWithAccessible;
	}

	public default List<Field> getAllFiledsHaveValue() throws IllegalArgumentException, IllegalAccessException {
		Field[] allFields = this.getClass().getDeclaredFields();
		List<Field> allFiledsWithAccessible = new ArrayList<>();
		for (Field field : allFields) {
			field.setAccessible(true);
			if (field.get(this) != null)
				allFiledsWithAccessible.add(field);
		}
		return allFiledsWithAccessible;
	}

	@SuppressWarnings({ "java:S2139" })
	public default String convertDTOObjectToJSONString() {
		try {
			ObjectMapper mapper = ObjectMapperUtils.getMapperInstance();
			return mapper.writeValueAsString(this.getDefaultRequestParams());
		} catch (JsonProcessingException e) {
			LOGGER.error(String.format("Error in mapping to response [%s] class. %s", this.getClass().getSimpleName(),
					e.getMessage()));
			throw new TestContextException(String.format("Error in mapping to response [%s] class. %s",
					this.getClass().getSimpleName(), e.getMessage()), e);
		}
	}

	@SuppressWarnings({ "java:S2139" })
	public static <T extends Request> T createDTOObjectByDataTable(Class<T> dtoClass, Map<String, String> dataTable) {
		T dtoClassInstance;
		try {
			dtoClassInstance = dtoClass.getDeclaredConstructor().newInstance();
			for (Entry<String, String> param : dataTable.entrySet()) {
				String paramKey = param.getKey();
				String paramValue = param.getValue();
				Field paramFiled = dtoClassInstance.getClass().getDeclaredField(paramKey);
				paramFiled.setAccessible(true);
				paramFiled.set(dtoClassInstance, paramValue);
			}
			return dtoClassInstance;
		} catch (NoSuchFieldException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			LOGGER.error(String.format("Error in mapping DTO class [%s] to request params. %s",
					dtoClass.getClass().getSimpleName(), e.getMessage()));
			throw new TestContextException(String.format("Error in mapping DTO class [%s] to request params. %s",
					dtoClass.getClass().getSimpleName(), e.getMessage()), e);
		}
	}
}
