package core.driver;

import core.helper.LogHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public abstract class AbstractWebDriverFactory implements WebDriverFactory {

    private static final LogHelper logger = LogHelper.getInstance();

    @Override
    public final RemoteWebDriver createRemoteWebDriver(String seleniumServer) {
        Object options = createOptions();
        if (null == seleniumServer || seleniumServer.equalsIgnoreCase("local") || seleniumServer.isEmpty()) {
            logger.info("Start new local browser");
            if (options instanceof ChromeOptions) {
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver((ChromeOptions) options);
            } else if (options instanceof FirefoxOptions) {
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver((FirefoxOptions) options);
            } else {
                throw new NotImplementedException();
            }
        } else {
            logger.info("Start new remote browser: {}", seleniumServer);
            try {
                URL url = new URL(seleniumServer);
                DesiredCapabilities capabilities = new DesiredCapabilities();
                if (options instanceof ChromeOptions) {
                    capabilities.setCapability(CapabilityType.BROWSER_NAME, "chrome");
                    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                } else if (options instanceof FirefoxOptions) {
                    capabilities.setCapability(CapabilityType.BROWSER_NAME, "firefox");
                    capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
                } else {
                    throw new NotImplementedException();
                }
                return new RemoteWebDriver(url, capabilities);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    protected abstract Object createOptions();
}