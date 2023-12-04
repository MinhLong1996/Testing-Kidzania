package core.helper;

import core.driver.DriverPool;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;

public class TestHelper {

    private TestHelper() {
    }

    private static final Logger logger = LogHelper.getLogger();

    public static WebDriver getDriver(String browserName, String server) {
        logger.info("Initializing new Web Driver");
        return DriverPool.get().getDriver(browserName, server);
    }

    public static void setMainDriver(RemoteWebDriver driver) {
        DriverPool pool = DriverPool.get();
        pool.setDriver("main", driver);
    }

    public static void tearDown() {
        logger.info("Removing all Web Drivers");
        DriverPool.get().quitAll();
        ThucydidesWebDriverSupport.getWebdriverManager().closeAllDrivers();
    }

    public static void removeDriver(String name, RemoteWebDriver driver) {
        logger.info("Removing Web Driver: {}", name);
        DriverPool.get().quit(name, driver);
    }
}
