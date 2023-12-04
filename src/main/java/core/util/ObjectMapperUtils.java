package core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.api.exception.TestContextException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ObjectMapperUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMapperUtils.class);

    private ObjectMapperUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static ObjectMapper getMapperInstance() {
        return MAPPER;
    }

    @SuppressWarnings({ "java:S2139" })
    public static <T> T dtoClassMapper(String jsonString, Class<T> clazz) {
        try {
            return MAPPER.readValue(jsonString, clazz);
        } catch (IOException e) {
            LOGGER.error(String.format("Error in mapping to response [%s] class. %s", clazz.getSimpleName(),
                    e.getMessage()));
            throw new TestContextException(
                    String.format("Error in mapping to response [%s] class. %s", clazz.getSimpleName(), e.getMessage()),
                    e);
        }
    }

    @SuppressWarnings({ "java:S2139" })
    public static String dtoClassMapperToJSON(Object dtoObject) {
        try {
            return MAPPER.writeValueAsString(dtoObject);
        } catch (JsonProcessingException e) {
            LOGGER.error(String.format("Error in mapping to response [%s] class. %s",
                    dtoObject.getClass().getSimpleName(), e.getMessage()));
            throw new TestContextException(String.format("Error in mapping to response [%s] class. %s",
                    dtoObject.getClass().getSimpleName(), e.getMessage()), e);
        }
    }

    public static String dtoWriteValueAsString(Object object) throws IOException {
        return MAPPER.writeValueAsString(object);
    }

}
