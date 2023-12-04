package core.util;

import com.google.gson.JsonObject;
import core.env.Environment;
import core.env.PageObject;
import core.helper.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WebDriverUtil {

    protected static final LogHelper logger = LogHelper.getInstance();
    protected final CustomStringUtil csUtil = new CustomStringUtil();
    protected final JsonUtil jsonUtil = new JsonUtil();
    protected final Helper helper = new Helper();

    public ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static final int TIMEOUT = Environment.TIMEOUT_DEFAULT;
    public static final int TIMEOUT_SMALL = Environment.TIMEOUT_SMALL;
    public static final String SERVER_MAIN = Environment.SERVER_MAIN;
    public static final String SERVER_CUSTOM = Environment.SERVER_CUSTOM;
    public static final String ACCOUNT_EMAIL = Environment.ACCOUNT_EMAIL;
    PageObject pageObject = Environment.INSTANCE.getPageObject();
    private Actions builder;
    protected final static String ASSERT_MESSAGE = Constants.ASSERT_MESSAGE;
    protected final static String LOG_ASSERT_MESSAGE = Constants.LOG_ASSERT_MESSAGE;

    public WebDriverUtil() {
        if (null == driver.get() || null == ((RemoteWebDriver) driver.get()).getSessionId()) {
            driver.set(getDriver());
            ThucydidesWebDriverSupport.getWebdriverManager().setCurrentDriver(driver.get());
        }
        builder = new Actions(driver.get());
    }

    public WebDriverUtil(WebDriver driver) {
        init(driver);
    }

    public void init(WebDriver driver) {
        this.driver.set(driver);
        ThucydidesWebDriverSupport.getWebdriverManager().setCurrentDriver(driver);
        builder = new Actions(driver);
    }

    public WebDriver getDriver() {
        return ((WebDriverFacade) ThucydidesWebDriverSupport.getDriver()).getProxiedDriver();
    }

    public void delaySync(double sec) {
        helper.delaySync(sec);
    }

    private JsonObject getElementObject(String elementName) {
        String jPath = String.format("$.%s.web", elementName);
        String pageName = csUtil.extractPageName(elementName);

        return jsonUtil.fetchJsonObject(pageObject.get(pageName), jPath);
    }

    private By getElementBy(String elementName) {
        JsonObject jo = getElementObject(elementName);
        String selector = jsonUtil.getValueFromJsonObject(jo, "selector");
        String strategy = jsonUtil.getValueFromJsonObject(jo, "strategy");

        By by;

        switch (strategy) {
            case "xpath":
                by = By.xpath(selector);
                break;
            case "id":
                by = By.id(selector);
                break;
            case "name":
                by = By.name(selector);
                break;
            case "cssSelector":
            case "css":
                by = By.cssSelector(selector);
                break;
            case "className":
                by = By.className(selector);
                break;
            case "linkText":
                by = By.linkText(selector);
                break;
            case "tagName":
                by = By.tagName(selector);
                break;
            case "partialLinkText":
                by = By.partialLinkText(selector);
                break;
            default:
                throw new IllegalArgumentException("Invalid strategy");
        }

        return by;

    }

    public WebElement element(By by) {
        return waitForElementPresent(by);
    }

    public WebElement element(String xpathOrCss) {
        return element(xpathOrCss(xpathOrCss));
    }

    public List<WebElement> elements(By by) {
        return waitForAllElementsPresent(by, TIMEOUT);
    }

    public List<WebElement> elements(String xpathOrCss) {
        return elements(xpathOrCss(xpathOrCss));
    }

    public WebElement getElement(String elementName, int timeout) {
        By by = getElementBy(elementName);
        return waitForElementPresent(by, timeout);
    }

    public WebElement getElement(String elementName) {
        return getElement(elementName, TIMEOUT);
    }

    public List<WebElement> getElements(String elementName, int timeout) {
        By by = getElementBy(elementName);
        return waitForAllElementsPresent(by, timeout);
    }

    public List<WebElement> getElements(String elementName) {
        return getElements(elementName, TIMEOUT_SMALL);
    }

    public String getElementLocator(String elementName) {
        return jsonUtil.getValueFromJsonObject(getElementObject(elementName), "selector");
    }

    public String getText(WebElement element) {
        String ret;
        String tagName = element.getTagName();
        if (tagName.equalsIgnoreCase("input") || tagName.equalsIgnoreCase("textarea")) {
            ret = element.getAttribute("value");
        } else {
            ret = element.getText().trim();
        }
        return ret;
    }

    public String getElementText(String elementName) {
        return getText(getElement(elementName));
    }

    public WebElement getElementByXPath(String expression) {
        return waitForElementPresent(expression, TIMEOUT_SMALL);
    }

    public List<WebElement> getElementsByXPath(String expression) {
        return waitForAllElementsPresent(By.xpath(expression), TIMEOUT_SMALL);
    }

    public void clickElement(String elementName) {
        clickOnElement(getElement(elementName));
    }

    public Object executeScript(String script, WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver.get();
        return executor.executeScript(script, element);
    }

    public Object executeScript(String script) {
        JavascriptExecutor executor = (JavascriptExecutor) driver.get();
        return executor.executeScript(script);
    }

    public void clickElementByJS(String elementName) {
        executeScript("arguments[0].click();", getElement(elementName));
    }

    public void clickElementByJS(WebElement element) {
        executeScript("arguments[0].click();", element);
    }

    public void typeText(String text, String elementName) {
        WebElement element = getElement(elementName);
        clearText(element);
        element.sendKeys(text);
    }

    public void typeTextWithoutClear(String text, String elementName) {
        WebElement element = getElement(elementName);
//        clearText(element);
        element.sendKeys(text);
    }

    public void typeText(String text, WebElement element) {
        waitForElementClickable(element);
        element.clear();
        element.sendKeys(text);
    }

    public void clearText(String elementName) {
        WebElement element = getElement(elementName);
        element.clear();
    }

    public void clearText(WebElement element) {
        element.clear();
        String actText = getText(element);
        if (!actText.isEmpty()) {
            clearByBackspace(element);
        }
    }

    public void clearByBackspace(WebElement element) {
        waitForElementVisible(element);
        while (element.getAttribute("value").length() > 0) {
            element.sendKeys(Keys.BACK_SPACE);
        }
    }

    public void typeTextByJS(String elementId, String value) {
        String script = String.format("document.getElementById('%s').value='%s'", elementId, value);
        executeScript(script);
    }

    public void goToURL(String url) {
        driver.get().get(url);
    }

    private String getPageDetector(String pageName) {
        String jPath = "$.detectors.web";
        JsonObject jsonObject = pageObject.get(pageName).getAsJsonObject();
        return csUtil.getPureString(jsonUtil.fetchJsonStringFromJsonObject(jsonObject, jPath));
    }

    public void assertPageShowUp(String pageName) {
        assertPageShowUpInGivenTimeout(pageName, 1);
    }

    public void assertPageShowUpInGivenTimeout(String pageName, int n) {
        String element = getPageDetector(pageName);
        assertElementPresentInGivenTimeout(element, n);
    }

    public void refreshBrowser() {
        driver.get().navigate().refresh();
    }

    public void redirectTo(String url) {
        driver.get().navigate().to(url);
    }

    public void navigateBack() {
        driver.get().navigate().back();
    }

    public void navigateForward() {
        driver.get().navigate().forward();
    }

    public void switchToIframe(String iframeName) {
        driver.get().switchTo().frame(getElement(iframeName));
    }

    public void exitIframe() {
        driver.get().switchTo().defaultContent();
    }

    public void singleScrollDown(int pixel) {
        String script = String.format("window.scrollBy(0,%d)", pixel);
        executeScript(script);
    }

    public void multiScrollDown(int pixel, int n) {
        for (int i = 0; i < n; i++) {
            singleScrollDown(pixel);
            delaySync(0.5);
        }
    }

    public void scrollToElement(WebElement element) {
        executeScript("arguments[0].scrollIntoView();", element);
    }

    public void scrollToElement(String elementName) {
        scrollToElement(getElement(elementName));
    }

    public void clickElementIfPresent(String elementName) {
        clickElementIfPresent(elementName, 3);
    }

    public void clickElementIfPresent(String elementName, int timeout) {
        if (isElementPresent(getElementBy(elementName), timeout)) {
            clickElement(elementName);
        }
    }

    public boolean isElementPresent(String elementName) {
        return isElementPresent(getElementBy(elementName), TIMEOUT_SMALL);
    }

    public void assertElementPresent(String elementName) {
        if (!isElementPresent(elementName)) {
            helper.writeStepFailed("Element is NOT being presented -> " + elementName);
        }
    }

    public void assertElementDisplayed(String elementName) {
        boolean isVisible = isElementVisible(getElementBy(elementName), TIMEOUT);
        if (!isVisible) {
            helper.writeStepFailed("Element is NOT being displayed -> " + elementName);
        }
    }

    public void assertElementNotDisplayed(String elementName) {
        assertElementNotDisplayed(elementName, TIMEOUT_SMALL);
    }

    public void assertElementNotDisplayed(String elementName, int timeout) {
        if (isElementVisible(getElementBy(elementName), timeout)) {
            helper.writeStepFailed("Element is being displayed -> " + elementName);
        }
    }

    public void assertElementPresentInGivenTimeout(String elementName, int n) {
        logger.info("Searching for element -> " + elementName);
        boolean isPresent = isElementPresent(getElementBy(elementName), TIMEOUT_SMALL * n);

        if (!isPresent) {
            helper.writeStepFailed("Element is NOT being presented -> " + elementName);
        }
    }

    public void assertElementNotPresent(String elementName) {
        if (elementName.startsWith("//")) {
            String locator = elementName;
            FluentWait<WebDriver> wait = new WebDriverWait(driver.get(), Duration.ofSeconds(TIMEOUT_SMALL))
                    .ignoring(StaleElementReferenceException.class);

            driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            try {
                wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath(locator), 0));
            } finally {
                resetTimeout();
            }
        } else {
            assertElementNotPresentInGivenTimeout(elementName, 1);
        }
    }

    private void resetTimeout() {
        driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(TIMEOUT_SMALL));
    }

    public void assertElementNotPresentInGivenTimeout(String elementName, int n) {
        boolean isInvisible = isElementInvisible(getElementBy(elementName), TIMEOUT_SMALL * n);
        if (!isInvisible) {
            helper.writeStepFailed("Element is being presented -> " + elementName);
        }
    }

    public void assertElementShowText(String elementName, String expectedText) {
        assertElementDisplayed(elementName);
        assertElementShowText(getElement(elementName), expectedText);
    }

    public void assertElementShowText(WebElement element, String expectedText) {
        waitForElementVisible(element);
        String actual = csUtil.getPureString(getText(element));
        if (!expectedText.equals(actual)) {
            scrollToElement(element);
            Assert.assertEquals(expectedText, actual);
        }
    }

    public void assertElementContainText(String elementName, String expectedText) {
        assertElementDisplayed(elementName);
        Assert.assertTrue(getElement(elementName).getText().trim().contains(expectedText));
    }

    public void assertElementContainText(WebElement element, String expectedText) {
        Assert.assertTrue(element.getText().trim().contains(expectedText));
    }

    public void assertElementAttributeHasValue(String attribute, String elementName, String expected) {
        assertElementDisplayed(elementName);
        Assert.assertEquals(expected, getElement(elementName).getAttribute(attribute).trim());
    }

    public void assertElementAttributeHasValue(WebElement element, String attribute, String expected) {
        Assert.assertEquals(expected, element.getAttribute(attribute).trim());
    }

    public void assertElementAttributeContainValue(String attribute, String elementName, String expected) {
        assertElementDisplayed(elementName);
        Assert.assertTrue(getElement(elementName).getAttribute(attribute).trim().contains(expected));
    }

    public void assertElementAttributeNotContainValue(String attribute, String elementName, String expected) {
        Assert.assertFalse(getElement(elementName).getAttribute(attribute).trim().contains(expected));
    }

    public void assertElementAttributeContainValue(WebElement element, String attribute, String expected) {
        Assert.assertTrue(element.getAttribute(attribute).trim().contains(expected));
    }

    public void doubleClick(String elementName) {
        builder.doubleClick(getElement(elementName)).perform();
    }

    public void doubleClick(WebElement element) {
        builder.doubleClick(element).perform();
    }

    public void mouseHover(String elementName) {
        builder.moveToElement(getElement(elementName)).build().perform();
    }

    public void mouseHover(WebElement element) {
        builder.moveToElement(element).build().perform();
    }

    public void clickUsingAction(WebElement element) {
        builder.moveToElement(element)
                .click()
                .build().perform();
    }

    public void pressKey(String keyCode) {
        String OS = System.getProperty("os.name").toLowerCase();
        boolean IS_WINDOWS = (OS.contains("win"));
        boolean IS_MAC = (OS.contains("mac"));
        boolean IS_UNIX = (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
        boolean IS_SOLARIS = (OS.contains("sunos"));

        switch (keyCode) {
            case "ESC":
                builder.sendKeys(Keys.ESCAPE).build().perform();
                break;
            case "TAB":
                builder.sendKeys(Keys.TAB).build().perform();
                break;
            case "ENTER":
                builder.sendKeys(Keys.ENTER).build().perform();
                break;
            case "BACK_SPACE":
                builder.sendKeys(Keys.BACK_SPACE).build().perform();
                break;
            case "DEL":
                builder.sendKeys(Keys.DELETE).build().perform();
                break;
            case "DOWN":
                builder.sendKeys(Keys.ARROW_DOWN).build().perform();
                break;
            case "UP":
                builder.sendKeys(Keys.ARROW_UP).build().perform();
                break;
            case "PAGE_DOWN":
                builder.sendKeys(Keys.PAGE_DOWN).build().perform();
                break;
            case "PAGE_UP":
                builder.sendKeys(Keys.PAGE_UP).build().perform();
                break;
            case "CTRL_A":
                if (IS_WINDOWS || IS_UNIX)
                    builder.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u0061')).keyUp(Keys.CONTROL).build().perform();
                else if (IS_MAC)
                    builder.keyDown(Keys.COMMAND).sendKeys(String.valueOf('\u0061')).keyUp(Keys.COMMAND).build().perform();
                break;
            case "CTRL_C":
                if (IS_WINDOWS || IS_UNIX)
                    builder.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u0063')).keyUp(Keys.CONTROL).build().perform();
                else if (IS_MAC)
                    builder.keyDown(Keys.COMMAND).sendKeys(String.valueOf('\u0063')).keyUp(Keys.COMMAND).build().perform();
                break;
            case "CTRL_V":
                if (IS_WINDOWS || IS_UNIX)
                    builder.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u0076')).keyUp(Keys.CONTROL).build().perform();
                else if (IS_MAC)
                    builder.keyDown(Keys.COMMAND).sendKeys(String.valueOf('\u0076')).keyUp(Keys.COMMAND).build().perform();
                break;
            case "CTRL_T":
                if (IS_WINDOWS || IS_UNIX)
                    builder.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u0074')).keyUp(Keys.CONTROL).build().perform();
                else if (IS_MAC)
                    builder.keyDown(Keys.COMMAND).sendKeys(String.valueOf('\u0074')).keyUp(Keys.COMMAND).build().perform();
                break;
            case "CTRL_SUB":
                if (IS_WINDOWS || IS_UNIX)
                    builder.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u002D')).keyUp(Keys.CONTROL).build().perform();
                else if (IS_MAC)
                    builder.keyDown(Keys.COMMAND).sendKeys(String.valueOf('\u002D')).keyUp(Keys.COMMAND).build().perform();
                break;
            case "CTRL_ADD":
                if (IS_WINDOWS || IS_UNIX)
                    builder.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u002B')).keyUp(Keys.CONTROL).build().perform();
                else if (IS_MAC)
                    builder.keyDown(Keys.COMMAND).sendKeys(String.valueOf('\u002B')).keyUp(Keys.COMMAND).build().perform();
                break;
            case "NUM_0":
                builder.sendKeys(Keys.NUMPAD0).build().perform();
                break;
            case "NUM_1":
                builder.sendKeys(Keys.NUMPAD1).build().perform();
                break;
            case "NUM_2":
                builder.sendKeys(Keys.NUMPAD2).build().perform();
                break;
            case "NUM_3":
                builder.sendKeys(Keys.NUMPAD3).build().perform();
                break;
            case "NUM_4":
                builder.sendKeys(Keys.NUMPAD4).build().perform();
                break;
            case "NUM_5":
                builder.sendKeys(Keys.NUMPAD5).build().perform();
                break;
            case "NUM_6":
                builder.sendKeys(Keys.NUMPAD6).build().perform();
                break;
            case "NUM_7":
                builder.sendKeys(Keys.NUMPAD7).build().perform();
                break;
            case "NUM_8":
                builder.sendKeys(Keys.NUMPAD8).build().perform();
                break;
            case "NUM_9":
                builder.sendKeys(Keys.NUMPAD9).build().perform();
                break;
            case "DOT":
                builder.sendKeys(Keys.DECIMAL).build().perform();
                break;
            case "SPACE":
                builder.sendKeys(Keys.SPACE).build().perform();
                break;

            default:
                break;
        }
    }


    public WebDriver getCustomDriver(String execMode, boolean enableLogging) throws MalformedURLException {
        String downloadFilepath = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "download"});
        WebDriver newDriver = null;
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", downloadFilepath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.setExperimentalOption("prefs", chromePrefs);

        if (enableLogging) {
            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
            options.setExperimentalOption("w3c", false);
            options.setAcceptInsecureCerts(true);
            options.setCapability("loggingPrefs", logPrefs);
        }

        switch (execMode.toUpperCase()) {
            case "LOCAL":
                io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
                LoggingPreferences logPrefs = new LoggingPreferences();
                logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
                WebDriverManager.chromedriver().setup();

                options.addArguments("--disable-infobars", "--disable-extensions", "--no-sandbox", "--disable-dev-shm-usage");
                options.setExperimentalOption("w3c", false);
                options.setAcceptInsecureCerts(true);
                options.setCapability("loggingPrefs", logPrefs);
                newDriver = new ChromeDriver(options);
                break;
            case "REMOTE":
                String remoteURL = Helper.getConf("webdriver.remote.url");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");
                DesiredCapabilities caps = new DesiredCapabilities();
                caps.setCapability(CapabilityType.BROWSER_NAME, "chrome");
                caps.setCapability(ChromeOptions.CAPABILITY, options);

                assert remoteURL != null;
                newDriver = new RemoteWebDriver(new URL(remoteURL), caps);
                break;
        }
        int timeout = Integer.parseInt(helper.getConfig("env.custom.driver.command.timeout"));
        assert newDriver != null;
        newDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        logger.info("Start new chrome driver: {}", options);
        return newDriver;
    }


    public Response createRestRequest(String method, Map<String, String> headers, String requestURI, String requestParam) {
        RequestSpecification rSpec = SerenityRest.given();

        for (Map.Entry<String, String> header : headers.entrySet()) {
            rSpec.header(header.getKey(), header.getValue());
        }

        switch (method.toUpperCase()) {
            case "GET":
                return rSpec.contentType(ContentType.JSON).get(requestURI);
            case "POST":
                return rSpec.contentType(ContentType.JSON).baseUri(requestURI).post();
        }
        return null;
    }

    public void terminate() {
        driver.get().quit();
    }

    public By xpathOrCss(String xpathOrCss) {
        Pattern p = Pattern.compile("^\\(*\\.?/{1,2}");
        return p.matcher(xpathOrCss).find() ? By.xpath(xpathOrCss) : By.cssSelector(xpathOrCss);
    }

    public WebElement waitForElementVisible(WebElement webElement, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.visibilityOf(webElement));
    }

    public WebElement waitForElementVisible(By by, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public WebElement waitForElementVisible(By by) {
        return waitForElementVisible(by, TIMEOUT);
    }

    public WebElement waitForElementVisible(WebElement webElement) {
        return waitForElementVisible(webElement, TIMEOUT);
    }

    public void waitForElementInvisible(WebElement element, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(timeout));
        ExpectedCondition<Boolean> invisibilityOfElement = driverWait -> {
            try {
                return !element.isDisplayed();
            } catch (StaleElementReferenceException | NoSuchElementException ignored) {
                return true;
            }
        };
        wait.until(invisibilityOfElement);
    }

    public void waitForElementInvisible(By by, int timeout) {
        try {
            WebElement element = waitForElementVisible(by, 0);
            try {
                waitForElementInvisible(element, timeout);
            } catch (TimeoutException e) {
                logger.fail(e.getMessage());
            }
        } catch (TimeoutException ignored) {
        }
    }

    public void waitForElementInvisible(String xpathOrCss, int timeout) {
        waitForElementInvisible(xpathOrCss(xpathOrCss), timeout);
    }

    public WebElement waitForElementClickable(WebElement webElement, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.elementToBeClickable(webElement));
    }

    public WebElement waitForElementClickable(WebElement webElement) {
        return waitForElementClickable(webElement, TIMEOUT);
    }

    public WebElement waitForElementRefreshedAndVisible(WebElement element) {
        return waitForElementRefreshedAndVisible(element, TIMEOUT);
    }

    public WebElement waitForElementRefreshedAndVisible(WebElement element, int timeOut) {
        return new WebDriverWait(driver.get(), Duration.ofSeconds(timeOut))
                .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOf(element)));
    }

    public WebElement waitForElementPresent(By locator, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement waitForElementPresent(By locator) {
        return waitForElementPresent(locator, TIMEOUT);
    }

    public WebElement waitForElementPresent(String xpathOrCss, int timeout) {
        return waitForElementPresent(xpathOrCss(xpathOrCss), timeout);
    }

    public WebElement waitForElementPresent(String xpathOrCss) {
        return waitForElementPresent(xpathOrCss, TIMEOUT);
    }

    public List<WebElement> waitForNestedElementsPresent(WebElement element, By childLocator) {
        return waitForNestedElementsPresent(element, childLocator, TIMEOUT);
    }

    public List<WebElement> waitForNestedElementsPresent(WebElement element, By childLocator, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(timeout));

        ExpectedCondition<List<WebElement>> expectedCondition = driverWait -> {
            List<WebElement> allChildren = element.findElements(childLocator);
            return !allChildren.isEmpty() ? allChildren : null;
        };

        return wait.until(expectedCondition);
    }

    public WebElement waitForNestedElementPresent(WebElement element, String xpathOrCss) {
        return waitForNestedElementsPresent(element, xpathOrCss(xpathOrCss), TIMEOUT).get(0);
    }

    public WebElement waitForNestedElementPresent(WebElement element, By by) {
        return waitForNestedElementsPresent(element, by, TIMEOUT).get(0);
    }

    public List<WebElement> waitForAllElementsPresent(By by, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    public List<WebElement> waitForAllElementsPresent(By by) {
        return waitForAllElementsPresent(by, TIMEOUT);
    }


    private void clickOnElement(WebElement element, int timeout) {
        try {
            waitForElementVisible(element);
            waitForElementClickable(element, timeout).click();
        } catch (StaleElementReferenceException e) {
            boolean isClicked = false;
            int loop = 3;
            while (loop > 0 && !isClicked) {
                try {
                    waitForElementRefreshedAndVisible(element, timeout);
                    waitForElementClickable(element, timeout).click();
                    isClicked = true;
                } catch (StaleElementReferenceException | TimeoutException ignored) {
                    logger.info("Attempting to click on element");
                    loop--;
                }
            }
            if (!isClicked) {
                logger.info("Click on element as failed");
                Assert.fail();
            }
        } catch (ElementClickInterceptedException e2) {
            clickElementByJS(element);
        }
    }

    public void clickOnElement(WebElement element) {
        clickOnElement(element, TIMEOUT);
    }

    public void clickOnElement(String xpathOrCss) {
        clickOnElement(waitForElementPresent(xpathOrCss), TIMEOUT);
    }

    public void clickOnElement(By by) {
        clickOnElement(waitForElementPresent(by), TIMEOUT);
    }

    public void waitForPageLoaded(long timeout) {
        ExpectedCondition<Boolean> expectation = driverWait -> {
            assert driverWait != null;
            return ((JavascriptExecutor) driverWait).executeScript("return document.readyState").toString().equalsIgnoreCase("complete");
        };
        try {
            Thread.sleep(1000);
            WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(timeout));
            wait.until(expectation);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void waitForPageLoaded() {
        waitForPageLoaded(TIMEOUT);
    }

    public boolean isElementPresent(By locator, int timeout) {
        boolean isPresent = false;
        try {
            waitForElementPresent(locator, timeout);
            isPresent = true;
        } catch (TimeoutException ignored) {
            //ignored
        }
        return isPresent;
    }

    public boolean isElementVisible(WebElement element, int timeout) {
        boolean isVisible = false;
        try {
            waitForElementVisible(element, timeout);
            isVisible = true;
        } catch (NoSuchElementException | TimeoutException ignored) {
            //ignored
        }
        return isVisible;
    }

    public boolean isElementVisible(By locator, int timeout) {
        boolean isVisible = false;
        try {
            waitForElementVisible(locator, timeout);
            isVisible = true;
        } catch (TimeoutException ignored) {
            //ignored
        }
        return isVisible;
    }

    public boolean isElementVisible(String xpathOrCss, int timeout) {
        return isElementVisible(xpathOrCss(xpathOrCss), timeout);
    }

    public boolean isElementInvisible(WebElement element, int timeout) {
        boolean isVisible = false;
        try {
            waitForElementInvisible(element, timeout);
            isVisible = true;
        } catch (TimeoutException ignored) {
            //ignored
        }
        return isVisible;
    }

    public boolean isElementInvisible(By by, int timeout) {
        boolean isVisible = false;
        try {
            waitForElementInvisible(by, timeout);
            isVisible = true;
        } catch (TimeoutException ignored) {
            //ignored
        }
        return isVisible;
    }

    private WebElement waitForElementContainsText(WebElement element, String text, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(timeout));
        ExpectedCondition<WebElement> textContains = driverWait -> {
            try {
                return element.getText().contains(text) ? element : null;
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                return null;
            }
        };
        return wait.until(textContains);
    }

    public WebElement waitForTextToBePresentInElement(WebElement element, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(timeout));
        ExpectedCondition<WebElement> textToBePresent = driverWait -> {
            try {
                String elementText = getText(element);
                return elementText != null && !elementText.isBlank() ? element : null;
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                return null;
            }
        };
        return wait.until(textToBePresent);
    }

    public void takeScreenshot() {
        helper.takeScreenshot(driver.get());
    }

    public void assertElementCssHasValue(String attribute, String elementName, String expectedValue) {
        assertElementDisplayed(elementName);
        String css = getElement(elementName).getCssValue(attribute).trim();
        logger.info("Css {} value of {}: {}", attribute, elementName, css);
        Assert.assertTrue(css.contains(expectedValue));
    }

    public String getCurrentURL() {
        return driver.get().getCurrentUrl();
    }

    public void switchToTab(int tabIndex) {
        ArrayList<String> tabs2 = new ArrayList<>(driver.get().getWindowHandles());
        driver.get().switchTo().window(tabs2.get(tabIndex));
    }

    public void closeCurrentTab() {
        ArrayList<String> tabs2 = new ArrayList<>(driver.get().getWindowHandles());
        driver.get().close();
        driver.get().switchTo().window(tabs2.get(0));
    }

    public List<WebElement> waitForAllElementsVisible(List<WebElement> elements, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    public List<WebElement> waitForAllElementsVisible(List<WebElement> elements) {
        return waitForAllElementsVisible(elements, TIMEOUT);
    }

    public List<WebElement> waitForAllElementsVisible(By by, int timeout) {
        return waitForAllElementsVisible(waitForAllElementsPresent(by, timeout), timeout);
    }

    public List<String> getTextFromElements(List<WebElement> elements) {
        return elements.stream().map(e -> e.getText().trim()).collect(Collectors.toList());
    }

    public List<String> getTextFromElements(String xpathOrCss) {
        List<WebElement> elements = waitForAllElementsPresent(xpathOrCss(xpathOrCss));
        return getTextFromElements(elements);
    }

    private void waitForJQueryAvailable() {
        if (!(Boolean) executeScript("return (typeof jQuery != \"undefined\")")) {
            executeScript(
                    "var headID = document.getElementsByTagName('head')[0];" +
                            "var newScript = document.createElement('script');" +
                            "newScript.type = 'text/javascript';" +
                            "newScript.src = 'https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js';" +
                            "headID.appendChild(newScript);");
            WebDriverWait waitJQ = new WebDriverWait(driver.get(), Duration.ofSeconds(30));
            Function<WebDriver, Boolean> jQueryAvailable = WebDriver -> (
                    (Boolean) executeScript("return (typeof jQuery != \"undefined\")")
            );
            waitJQ.until(jQueryAvailable);
        }
    }

    public void dragAndDrop(WebElement src, WebElement dst) {
//        int x = dst.getLocation().getX();
//        int y = dst.getLocation().getY();
        scrollToElement(src);
//        builder.clickAndHold(src)
//                .pause(Duration.ofSeconds(1))
////                .moveToElement(dst)
//                .pause(Duration.ofSeconds(1))
//                .release(dst).build().perform();
//        delaySync(1);

        int sourceWidth = src.getSize().getWidth();
        int SourceHeight = src.getSize().getHeight();
        int destinationWidth = dst.getSize().getWidth();
        int destinationHeight = dst.getSize().getHeight();

        Actions act = new Actions(driver.get());
        act.moveToElement(src, (sourceWidth / 2), SourceHeight / 2)
                .clickAndHold(src)
                .pause(Duration.ofSeconds(1))
                .moveToElement(dst, (destinationWidth / 2), (destinationHeight / 2))
                .pause(Duration.ofSeconds(1))
                .release(dst).build().perform();
    }

    public void dragAndDropJS(WebElement src, WebElement dst) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript(DragAndDropHelper.dragAndDropJS, src, dst);
    }

    public void createNewDriver(String name, String server) {
        TestHelper.setMainDriver((RemoteWebDriver) driver.get());
        WebDriver newDriver = TestHelper.getDriver(name, server);
        init(newDriver);
    }

    public void switchToDiver(String name, String server) {
        driver.set(TestHelper.getDriver(name, server));
        init(driver.get());
    }

    public void closeDriver(String name, WebDriver driver) {
        TestHelper.removeDriver(name, (RemoteWebDriver) driver);
    }

    public String xpathExpression(String value) {
        if (!value.contains("'")) {
            return String.format("'%s'", value);
        } else if (!value.contains("\"")) {
            return String.format("\"%s\"", value);
        } else {
            return String.format("concat('%s')", value.replace("'", "',\"'\",'"));
        }
    }

    public String getCurrentUrl() {
        return driver.get().getCurrentUrl();
    }

    private boolean waitForUrlContains(String url, int timeout) {
        logger.info("Wait for url contains: " + url);
        WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(timeout));

        ExpectedCondition<Boolean> expectedCondition = driverWait -> {
            String currentUrl = "";
            try {
                currentUrl = URLDecoder.decode(getCurrentUrl(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.info(e.getMessage());
            }

            if (currentUrl != null && currentUrl.contains(url)) {
                logger.info("Current url is: {}", currentUrl);
                return true;
            } else {
                return false;
            }
        };
        return wait.until(expectedCondition);
    }

    public boolean waitForUrlContains(String url) {
        return waitForUrlContains(url, TIMEOUT);
    }

    public String getDownloadFolder() {
        return csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "download"});
    }

    public String getDownloadedFile() {
        List<String> paths = FileUtil.listAllFiles(getDownloadFolder());
        if (paths.size() > 0) {
            return paths.get(0);
        } else {
            return null;
        }
    }

    public void zoomBrowser(double percentage) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("document.body.style.zoom = '" + percentage + "'");
    }

    public boolean assertPageLoaded(long timeout) {
        try {
            waitForPageLoaded(timeout);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void findBrokenURL(StringBuilder brokenURLs, StringBuilder verifiedURLs) {
        final int MAX_WINDOW = 6;
        String currentTab = driver.get().getWindowHandle();

        List<String> urls = driver.get().findElements(By.tagName("a")).stream().map(e -> e.getAttribute("href")).collect(Collectors.toList());
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            if (!isURLValid(url)) {
                logger.info("URL is either not configured for anchor tag or it is empty");
            } else if (!verifiedURLs.toString().contains(url) && !brokenURLs.toString().contains(url)) {
                driver.get().switchTo().window(openNewTab()).get(url);
                List<String> tabs = getWindowHandles();
                if (tabs.size() == MAX_WINDOW || i == urls.size() - 1) {
                    tabs.remove(currentTab);
                    for (String tab : tabs) {
                        boolean noBrokenUrl;
                        driver.get().switchTo().window(tab);
                        By by = getElementBy("MASTER_PAGE_ERROR_PAGE_LABEL");
                        noBrokenUrl = !isElementVisible(by, 0) && assertPageLoaded(TIMEOUT);
                        if (!noBrokenUrl) {
                            logger.error("#" + i + " - " + url + " is a broken link");
                            takeScreenshot();
                            brokenURLs.insert(0, url + "\r");
                        } else {
                            verifiedURLs.insert(0, url + "\r");
                        }
                    }
                    closeOtherWindows(currentTab);
                }
            }
        }
    }

    private boolean isURLValid(String urlStr) {
        try {
            URL url = new URL(urlStr);
            url.toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public String openNewTab() {
        List<String> windows = getWindowHandles();
        executeScript("window.open()");
        List<String> newWindows = getWindowHandles();
        newWindows.removeAll(windows);
        return newWindows.get(0);
    }

    public List<String> getWindowHandles() {
        return new ArrayList<>(driver.get().getWindowHandles());
    }

    public void closeOtherWindows(String window) {
        List<String> windows = getWindowHandles();
        windows.remove(window);
        windows.forEach(e -> driver.get().switchTo().window(e).close());
        driver.get().switchTo().window(window);
    }
}
