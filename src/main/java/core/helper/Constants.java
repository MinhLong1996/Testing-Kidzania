package core.helper;

public class Constants {
    private static Constants instance = null;

    private Constants() {
    }

    public static Constants instance() {
        if (instance == null) {
            instance = new Constants();
        }
        return instance;
    }

    public static final String LOG_ASSERT_MESSAGE = "Expected: {} -/- Actual: {}";
    public static final String ASSERT_MESSAGE = "Expected: %s -/- Actual: %s";
}
