package core.driver;

import core.helper.LogHelper;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.driverproviders.ConfigureChromiumOptions;
import net.serenitybdd.core.webdriver.driverproviders.UpdateDriverEnvironmentProperty;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.steps.TestContext;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


public class DriverPool {

    private static final Logger logger = LogHelper.getLogger();

    private Map<String, WebDriverFactory> factories;
    private List<RemoteWebDriver> allDrivers;
    private ThreadLocal<Map<String, RemoteWebDriver>> driverCache;

    private static class DriverPoolSingleton {
        private static final DriverPool INSTANCE = new DriverPool();
    }

    public static DriverPool get() {
        return DriverPoolSingleton.INSTANCE;
    }

    private DriverPool() {
        initDefaultFactories();
        initDriverCache();
    }

    private void initDefaultFactories() {
        factories = new ConcurrentHashMap<>();
        factories.put("chrome", new AbstractWebDriverFactory() {
            @Override
            protected ChromeOptions createOptions() {
                ChromeOptions options = newOptions();
                addHeadlessTo(options,"custom.driver.headless.mode");
                return options;
            }
        });
        factories.put("chrome_logging", new AbstractWebDriverFactory() {
            @Override
            protected ChromeOptions createOptions() {
                ChromeOptions options = newOptions();
                addLoggingTo(options);
                return options;
            }
        });
        factories.put("chrome_main", new AbstractWebDriverFactory() {
            @Override
            protected ChromeOptions createOptions() {
                ChromeOptions options = newOptions();
                addHeadlessTo(options, "headless.mode");
                return options;
            }
        });
    }

    private ChromeOptions newOptions(){
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables();
        UpdateDriverEnvironmentProperty.forDriverProperty("webdriver.chrome.driver");
        ChromeOptions chromeOptions = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").chromeOptions();
        ChromeOptions enhancedOptions = ConfigureChromiumOptions.from(environmentVariables).into(chromeOptions);
        TestContext.forTheCurrentTest().recordBrowserConfiguration(enhancedOptions);
        TestContext.forTheCurrentTest().recordCurrentPlatform();
        logger.info("Starting Chrome driver instance with capabilities:");
        logger.info(enhancedOptions.toString());
        return enhancedOptions;
    }

    private void addHeadlessTo(ChromeOptions options, String propertyName) {
        EnvironmentVariables variables = SystemEnvironmentVariables.createEnvironmentVariables();
        Optional<String> headless = EnvironmentSpecificConfiguration.from(variables).getOptionalProperty(propertyName);
        if (headless.isPresent() && Boolean.parseBoolean(headless.get())) {
            options.addArguments("--headless");
        }
    }

    private void addLoggingTo(ChromeOptions options) {
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        options.setCapability("loggingPrefs", logPrefs);
        options.setAcceptInsecureCerts(true);
        logger.info("Headless mode is enabled while enabling logging");
        options.addArguments("--headless");
    }

    private void initDriverCache() {
        driverCache = new InheritableThreadLocal<>();
        allDrivers = new ArrayList<>();
    }

    private boolean hasQuit(RemoteWebDriver driver) {
        SessionId sessionId = driver.getSessionId();
        return sessionId == null;
    }

    private void addToCache(String name, RemoteWebDriver driver) {
        Map<String, RemoteWebDriver> localThreadDrivers = driverCache.get();
        localThreadDrivers.put(name, driver);
        allDrivers.add(driver);
    }

    private void removeFromCache(String name, RemoteWebDriver driver) {
        Map<String, RemoteWebDriver> localThreadDrivers = driverCache.get();
        localThreadDrivers.remove(name);
        allDrivers.remove(driver);
        driver.quit();
    }

    public void setDriver(String name, RemoteWebDriver driver) {
        Map<String, RemoteWebDriver> localThreadDrivers = driverCache.get();
        if (localThreadDrivers == null) {
            localThreadDrivers = new HashMap<>();
            driverCache.set(localThreadDrivers);
        }
        addToCache(name, driver);
    }

    public WebDriver getDriver(String name, String server) {
        logger.info("Initializing new WebDriver for {}", name);
        Map<String, RemoteWebDriver> localThreadDrivers = driverCache.get();
        if (localThreadDrivers == null) {
            localThreadDrivers = new HashMap<>();
            driverCache.set(localThreadDrivers);
        }
        RemoteWebDriver driver = localThreadDrivers.get(name);
        boolean needsCreating;
        if (driver == null) {
            needsCreating = true;
        } else {
            if (hasQuit(driver)) {
                removeFromCache(name, driver);
                needsCreating = true;
            } else {
                needsCreating = false;
            }
        }
        if (needsCreating) {
            logger.info("WebDriver cache is empty, creating new WebDriver.");
            WebDriverFactory factory = factories.get(name);
            driver = factory.createRemoteWebDriver(server);
            addToCache(name, driver);
        }
        return driver;
    }

    public void quitAll() {
        allDrivers.forEach(RemoteWebDriver::quit);
        destroy();
    }

    public void quit(String name, RemoteWebDriver driver) {
        removeFromCache(name, driver);
    }

    public void destroy() {
        driverCache.remove();
    }
}
