package steps;

import core.helper.TestHelper;
import io.cucumber.java.en.When;

public class CustomDriverSteps extends BaseStep {

    private static final String MAIN = "main";
    private static final String CUSTOM = "chrome";
    private static final String CUSTOM_LOGGING = "chrome_logging";
    private static final String CUSTOM_DOWNLOAD = "chrome_main";
    private static boolean isLogging = false;

    @When("User start new custom driver")
    public void userStartNewCustomDriver() {
        createNewDriver(CUSTOM, SERVER_CUSTOM);
        isLogging = false;
    }

    @When("User start new custom driver and enable logging")
    public void userStartNewCustomDriverAndLogging() {
        createNewDriver(CUSTOM_LOGGING, SERVER_CUSTOM);
        isLogging = true;
    }

    @When("User start new custom driver for downloading")
    public void userStartNewCustomDriverForDownloading() {
        createNewDriver(CUSTOM_DOWNLOAD, SERVER_MAIN);
    }

    @When("User close the custom driver")
    public void userCloseTheCustomerDriver() {
        closeDriver(isLogging ? CUSTOM_LOGGING : CUSTOM, driver.get());
        driver.set(TestHelper.getDriver(MAIN, ""));
        init(driver.get());
    }

    @When("User switch to the custom driver")
    public void userSwitchToCustomDriver() {
        switchToDiver(isLogging ? CUSTOM_LOGGING : CUSTOM, SERVER_CUSTOM);
    }

    @When("User switch to the main driver")
    public void userSwitchToMainDriver() {
        driver.set(TestHelper.getDriver(MAIN, ""));
        init(driver.get());
    }
}