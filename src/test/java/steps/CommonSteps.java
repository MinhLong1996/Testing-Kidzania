package steps;

import com.google.gson.JsonObject;
import core.helper.DropDownHelper;
import core.util.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import pages.CommonPage;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class CommonSteps extends BaseStep {
    private CustomDriverSteps customDriverSteps = new CustomDriverSteps();
    CommonPage commonPage = new CommonPage();

    @Given("User redirects to {string}")
    public void userRedirectsTo(String env) {
        String url = helper.getConfig("env." + env + ".url");
        boolean isAuth = Boolean.parseBoolean(helper.getConfig("env." + env + ".auth"));
        if (isAuth) {
            String user = helper.getConfig("env." + env + ".auth.user");
            String pass = helper.getConfig("env." + env + ".auth.pass");
            url = user + ":" + pass + "@" + url;
        }
        url = url.startsWith("https://") ? url : "https://" + url;
        goToURL(url);
        context.setContext("env", env);
        context.setContext("baseURL", url);
    }

    @When("{string} shows up")
    public void showsUp(String pageName) {
        assertPageShowUp(pageName);
    }

    @And("{string} shows up in {int} timeout")
    public void showsUpInTimeout(String pageName, int n) {
        assertPageShowUpInGivenTimeout(pageName, n);
    }

    @And("User clicks on {string}")
    public void userClicksOn(String elementName) {
        clickElement(elementName);
    }

    @And("User clicks on {string} by JS")
    public void userClicksOnByJS(String elementName) {
        clickElementByJS(elementName);
    }

    @And("User clear text on {string}")
    public void userClearText(String elementName) {
        clickElement(elementName);
        clearText(elementName);
    }

    @And("User clear special text on {string}")
    public void userClearSpecialText(String elementName) {
        // Cannot use this in current web of ARM
        clickElement(elementName);
        getElement(elementName).sendKeys(" ");
        pressKey("CTRL_A");
        pressKey("DEL");
    }

    @And("User types {string} into {string}")
    public void userTypesInto(String text, String elementName) {
        text = translateText(text);
        typeText(text, elementName);
    }

    @And("User types {string} into {string} by JS")
    public void userTypesIntoByJS(String text, String elementName) {
        typeTextByJS(text, elementName);
    }

    @When("User uploads {string} to {string}")
    public void userUploadFileTo(String fileName, String elementName) {
        String filePath = helper.getUploadFilePath(context.translate(fileName));
        typeText(filePath, elementName);
    }

    @And("User refresh browser")
    public void userRefreshBrowser() {
        refreshBrowser();
    }

    @And("User navigate {string}")
    public void userNavigate(String direction) {
        if (direction.equalsIgnoreCase("BACK"))
            navigateBack();
        else {
            navigateForward();
        }
    }

    @And("User switch to {string}")
    public void userSwitchTo(String iframeName) {
        switchToIframe(iframeName);
    }

    @And("User exit current iframe")
    public void userExitCurrentIframe() {
        exitIframe();
    }

    @And("User scroll down by {int} pixel")
    public void userScrollDownByPixel(int pixel) {
        singleScrollDown(pixel);
    }

    @And("User scroll down by {int} pixel {int} times")
    public void userScrollDownByPixelTimes(int pixel, int n) {
        multiScrollDown(pixel, n);
    }

    @And("User scroll to {string}")
    public void userScrollTo(String elementName) {
        scrollToElement(elementName);
    }

    @And("User clicks on {string} if present")
    public void userClicksOnIfPresent(String elementName) {
        clickElementIfPresent(elementName);
    }

    @And("User clicks on {string} if present in {int} seconds")
    public void userClicksOnIfPresentInSeconds(String elementName, int timeout) {
        clickElementIfPresent(elementName, timeout);
    }

    @And("{string} is present")
    public void isPresent(String elementName) throws IOException {
        if (elementName.endsWith("_IMG")) {
            String source = ScreenshotUtil.takeSnapShot(driver.get(), "source");
            String target = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "baseline", elementName + ".png"});

            if (!ImageUtil.findSubImageInImage(target, source)) {
                String downloadDir = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "download"});
                FileUtils.cleanDirectory(new File(downloadDir));
                String icURL = helper.getConfig("env.image.compare.url");
                customDriverSteps.userSwitchToMainDriver();
                WebElement element;

                goToURL(icURL);
                assertPageShowUp("IC_HOME_PAGE");
                delaySync(1);
                element = getElement("IC_HOME_PAGE_SOURCE_IMAGE_INPUT");
                element.sendKeys(source);
                delaySync(5);
                element = getElement("IC_HOME_PAGE_TARGET_IMAGE_INPUT");
                executeScript("arguments[0].setAttribute('data-click', '1')", element);
                delaySync(1);
                element.sendKeys(target);
                delaySync(5);
                clickElement("IC_HOME_PAGE_COMPARE_BUTTON");
                delaySync(10);
                clickElementByJS("IC_HOME_PAGE_VISUALISATION_BUTTON");
                delaySync(3);
                String locator = getElementLocator("IC_HOME_PAGE_VISUALISATION_DROPDOWN");
                locator = locator + "//child::li[1]";
                getElementByXPath(locator).click();
                delaySync(1);
                clickElement("IC_HOME_PAGE_DOWNLOAD_BUTTON");
                delaySync(5);

                String output = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "download", "image_compare_visualisation.png"});
                helper.embedScreenshot(output, "image_compare_visualisation");
                helper.writeStepFailed();
            } else {
                Assert.assertTrue(true);
            }
        } else {
            assertElementPresent(elementName);
        }
    }

    @And("{string} is present in {int} timeout")
    public void isPresentInTimeout(String elementName, int n) {
        assertElementPresentInGivenTimeout(elementName, n);
    }

    @And("{string} is not present")
    public void isNotPresent(String elementName) {
        assertElementNotPresent(elementName);
    }

    @And("{string} is not present in {int} timeout")
    public void isNotPresentInTimeout(String elementName, int n) {
        assertElementNotPresentInGivenTimeout(elementName, n);
    }

    @And("{string} shows {string}")
    public void shows(String elementName, String expectedText) {
        expectedText = context.translate(expectedText);
        assertElementShowText(elementName, expectedText);
    }

    @And("{string} contains {string}")
    public void contains(String elementName, String expectedText) {
        expectedText = context.translate(expectedText);
        assertElementContainText(elementName, expectedText);
    }

    @And("{string} of {string} has {string}")
    public void ofHas(String attribute, String elementName, String expectedValue) {
        assertElementAttributeHasValue(attribute, elementName, expectedValue);
    }

    @And("{string} of {string} contains {string}")
    public void ofContains(String attribute, String elementName, String expectedValue) {
        assertElementAttributeContainValue(attribute, elementName, expectedValue);
    }

    @And("User double click on {string}")
    public void userDoubleClickOn(String elementName) {
        doubleClick(elementName);
    }

    @And("User mouse hover on {string}")
    public void userMouseHoverOn(String elementName) {
        mouseHover(elementName);
    }

    @And("User press key {string}")
    public void userPressKey(String keyCode) {
        pressKey(keyCode);
    }

    @And("User waits for {int} seconds")
    public void userWaitsForSeconds(int sec) {
        delaySync(sec);
    }

    @And("User switch to tab {int}")
    public void userSwitchToTab(int tabIndex) {
        switchToTab(tabIndex - 1);
    }

    @And("User close current tab")
    public void userCloseCurrentTab() {
        closeCurrentTab();
    }

    @And("User goes to {string}")
    public void userGoesTo(String endpoint) {
        String baseURL = context.getContext("baseURL");
        goToURL(baseURL + endpoint);
    }

    @And("User clicks on {string} by {string}")
    public void userClicksOnBy(String elementType, String visibleText) {
        String xpath = "//span[contains(text(),'" + visibleText + "')]//parent::" + elementType + "[1]";
        try {
            getElementByXPath(xpath).click();
        } catch (Exception e) {
            switch (visibleText) {
                case "Create":
                    visibleText = "新規作成";
                    break;

                default:
                    break;
            }
            xpath = "//span[contains(text(),'" + visibleText + "')]//parent::" + elementType + "[1]";
            getElementByXPath(xpath).click();
        }
    }

    @Given("User switch to language {string}")
    public void userSwitchToLanguage(String language) {
        clickElement("MASTER_PAGE_LANGUAGE_SETTING_DROPDOWN");
        delaySync(1);
        try {
            clickElement("MASTER_PAGE_LANGUAGE_SETTING_" + language.toUpperCase() + "_ITEM");
        } catch (Exception e) {
            clickElementByJS("MASTER_PAGE_LANGUAGE_SETTING_" + language.toUpperCase() + "_ITEM");
        }
    }

    @And("Current web table should contain below data")
    public void currentWebTableShouldContainBelowData(Map<String, String> table) {
        delaySync(5);
        String header;
        String value;
        StringBuilder rowData = new StringBuilder();
        StringBuilder expected = new StringBuilder();
        boolean getRow = false;
        WebElement row = null;

        for (Map.Entry<String, String> entry : table.entrySet()) {
            header = entry.getKey().trim();
            value = (null == entry.getValue()) ? "" : entry.getValue().trim();

            if (value.startsWith("<")) {
                switch (value) {
                    case "<NOW>":
                        value = context.getContext("currentTime");
                        break;
                    case "<CURRENT_USER>":
                        value = getElementText("MASTER_PAGE_LOGGED_IN_USER_LABEL");
                        break;
                }
            }

            if (!commonPage.lookUpWebTableVertically(header, value))
                helper.writeStepFailed("Data not found -> Header = " + header + " , Value = " + value);
//            else {
//                rowData.append(value.trim());
//                if (!getRow) {
//                    row = commonPage.getWebTableRow(header, value);
//                    getRow = true;
//                }
//            }
        }

//        assert row != null;
//        List<WebElement> cols = row.findElements(By.tagName("div"));
//        for (WebElement col : cols) {
//            expected.append(col.getText().trim());
//        }
//        takeScreenshot();
////        System.out.println("Expected = " + rowData);
////        System.out.println("Actual = " + expected);
//        helper.compareEqual(rowData.toString(), expected.toString());
    }

    @And("User snapshot current timestamp")
    public void userSnapshotCurrentTimestamp() {
        CustomStringUtil csUtil = new CustomStringUtil();
        context.setContext("currentTime", csUtil.getCurrentTimeAsString());
    }

    @And("{string} is displayed")
    public void isDisplayed(String elementName) {
        assertElementDisplayed(elementName);
    }

    @When("{string} is not displayed")
    public void isNotDisplayed(String elementName) {
        assertElementNotDisplayed(elementName);
    }

    @When("{string} is not displayed in {int} seconds")
    public void isNotDisplayedInSeconds(String elementName, int timeout) {
        assertElementNotDisplayed(elementName, timeout);
    }

    @When("User click on Web Table cell {string} by column {string}")
    public void userClickOnWebTableCellByColumn(String value, String header) {
        if (value.startsWith("$.")) {
            value = value.replace("$.", "");
            value = context.getContext(value);
        }
        commonPage.getWebTableCell(header, value).click();
        delaySync(5);
    }

    @When("User enter a string with {int} chars into {string}")
    public void userEnterAStringWithCharsInto(int len, String elementName) {
        String input = helper.randomString(len);
        typeText(input, elementName);
        if (elementName.contains("ISSUER"))
            context.setContext("issuerText", input);
        else if (elementName.contains("REMARKS")) {
            context.setContext("remarksText", input);
        }
    }

    @Then("{string} column data of {string} table should be sorted in {string} order properly")
    public void columnDataOfTableShouldBeSortedInOrderProperly(String header, String table, String order) {
        List<Integer> actual = helper.convertToInt(commonPage.getAllRowsByHeader(table, header));
        List<Integer> expected = helper.convertToInt(context.getContext("colData"));

        if (order.equalsIgnoreCase("ascending")) {
            Collections.sort(expected);
        } else {
            expected.sort(Collections.reverseOrder());
        }

        Assert.assertEquals(expected, actual);
    }

    @And("User snapshot current state of {string} column data in {string} table")
    public void userSnapshotCurrentStateOfColumnDataInTable(String header, String table) {
        List<String> colData = commonPage.getAllRowsByHeader(table, header);
        context.setContext("colData", colData);
    }

    @Then("Current web table should have more than {int} row")
    public void currentWebTableShouldHaveMoreThanRow(int expected) {
        delaySync(2);
        int actual = commonPage.getWebTableRowCount();
        Assert.assertTrue(actual > expected);
    }

    @Given("User encode the password {string}")
    public void userEncodeThePassword(String password) {
        logger.info("password = " + helper.encodePassPhrase(password));
    }

    @When("User select item {string} on the dropdown {string}")
    public void userSelectItemOnTheDropdown(String item, String dropdown) {
        item = context.translate(item);
        String dropdownBtn = dropdown.split(">")[0].trim();
        String dropdownLst = dropdown.split(">")[1].trim();
        commonPage.selectItemFromDropdown(item, dropdownLst, getElement(dropdownBtn));
    }

    @And("User select date range {string} on the date picker {string} by starting with {string}")
    public void userSelectDateRangeOnTheDatePicker(String dateRange, String datePicker, String startBy) {
        commonPage.selectFilterByDate(dateRange, datePicker, startBy);
    }

    @Then("{string} column data of {string} table should be sorted in {string} order and {string} type properly")
    public void columnDataOfTableShouldBeSortedInOrderAndTypeProperly(String header, String table, String order, String type) {
        List<String> actual = commonPage.getAllRowsByHeader(table, header);
        List<String> expected = new ArrayList<>(actual);

        if (type.equalsIgnoreCase("NUMBER")) {
            List<Integer> actualInt = helper.convertToInt(actual);
            List<Integer> expectedInt = helper.convertToInt(expected);
            if (order.equalsIgnoreCase("ascending")) {
                Collections.sort(expectedInt);
            } else {
                expectedInt.sort(Collections.reverseOrder());
            }
            logger.info("Expected:\t{}", expectedInt);
            logger.info("Actual:\t{}", actualInt);
            Assert.assertEquals(expectedInt, actualInt);
        } else {
            if (order.equalsIgnoreCase("ascending")) {
                Collections.sort(expected);
            } else {
                expected.sort(Collections.reverseOrder());
            }
            logger.info(ASSERT_MESSAGE, expected, actual);
            logger.info("Expected:\t{}", expected);
            logger.info("Actual:\t{}", actual);
        }
    }

    @And("{string} column data of {string} table should be filtered by {string} and the {string} properly")
    public void columnDataOfTableShouldBeFilteredByAndTheProperly(String header, String tableName, String filterType, String filterVal) {
        List<String> actual = commonPage.getAllRowsByHeader(tableName, header);

        switch (filterType.toUpperCase()) {
            case "TEXT":
                for (String item : actual) {
                    logger.info("filtered data = " + item);
                    Assert.assertTrue(item.contains(filterVal));
                }
                break;
            case "DROPDOWN":
                boolean found = false;
                if (filterVal.contains(",")) {
                    for (String value : actual) {
                        String[] arr = filterVal.split(",");
                        for (String s : arr) {
                            logger.info("actual = " + value);
                            if (value.equals(s.trim())) {
                                logger.info("expected found -> " + s.trim());
                                logger.info("------------------");
                                found = true;
                                break;
                            } else
                                logger.info("expected not found, retrying... -> " + s.trim());
                        }
                        Assert.assertTrue(found);
                        found = false;
                    }
                } else {
                    for (String s : actual) {
                        logger.info("actual = " + s);
                        logger.info("expected = " + filterVal.trim());
                        Assert.assertEquals(s, filterVal.trim());
                    }
                }
                break;
            case "DATEPICKER":
                CustomStringUtil csUtil = new CustomStringUtil();
                String fromDate = csUtil.convertToParsableDateString(filterVal.split("-")[0].trim());
                String toDate = csUtil.convertToParsableDateString(filterVal.split("-")[1].trim());
                logger.info("filtered date range = " + filterVal);

                DateTime fDate = new DateTime(fromDate.replace("/", "-"));
                DateTime tDate = new DateTime(toDate.replace("/", "-"));
                DateTime date;

                for (String value : actual) {
                    logger.info("checking filter data -> " + value);
                    date = new DateTime(value.replace("/", "-"));
                    Assert.assertTrue(date.compareTo(fDate) >= 0 && date.compareTo(tDate) <= 0);
                }
                break;
            case "AMOUNT_OF_MONEY":
                logger.info("filtered amount range = " + filterVal);
                String fromAmount = filterVal.split("-")[0].trim();
                String toAmount = filterVal.split("-")[1].trim();
                int from;
                int to;
                int current;

                if (!fromAmount.equalsIgnoreCase("n/a") && !toAmount.equalsIgnoreCase("n/a")) {
                    for (String value : actual) {
                        from = Integer.parseInt(fromAmount);
                        to = Integer.parseInt(toAmount);

                        logger.info("checking filter data -> " + value);
                        value = value.replace("¥", "").replace(",", "").trim();
                        current = Integer.parseInt(value);
                        Assert.assertTrue(current >= from && current <= to);
                    }
                } else if (fromAmount.equalsIgnoreCase("n/a") && !toAmount.equalsIgnoreCase("n/a")) {
                    for (String value : actual) {
                        to = Integer.parseInt(toAmount);
                        logger.info("checking filter data -> " + value);
                        value = value.replace("¥", "").replace(",", "").trim();
                        current = Integer.parseInt(value);
                        Assert.assertTrue(current <= to);
                    }
                }
                if (!fromAmount.equalsIgnoreCase("n/a") && toAmount.equalsIgnoreCase("n/a")) {
                    for (String value : actual) {
                        from = Integer.parseInt(fromAmount);
                        logger.info("checking filter data -> " + value);
                        value = value.replace("¥", "").replace(",", "").trim();
                        current = Integer.parseInt(value);
                        Assert.assertTrue(current >= from);
                    }
                }
                break;
        }
    }

    @And("User create new {string} request to the endpoint {string} with data")
    public void userCreateNewRequestToTheEndpointWithData(String method, String endPoint) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", context.getContext("loggedInCookie"));
        context.setContext("requestHeader", headers);

        String baseURL = helper.getConfig("env." + context.getContext("env") + ".url");

        userGetId(endPoint, "itemID");
        String requestParam = context.getContext("itemID");

        String requestURI = "https://" + baseURL + "/" + helper.getConfig("api.version") + "/" + helper.getConfig("api." + endPoint + ".endpoint");

        requestURI = requestURI + "/" + requestParam;
        Response response = createRestRequest(method, headers, requestURI, requestParam);

        assert response != null;
        if (response.getStatusCode() == 200) {
            context.setContext(method + "_" + endPoint + "_" + requestParam, response.getBody().asPrettyString());
        }
    }

    @And("The response of {string} request to the endpoint {string} with data should remain unchanged")
    public void theResponseOfRequestToTheEndpointWithDataShouldRemainUnchanged(String method, String endPoint) {
        String baseURL = helper.getConfig("env." + context.getContext("env") + ".url");

        userGetId(endPoint, "itemID");
        String requestParam = context.getContext("itemID");

        String expected = context.getContext(method + "_" + endPoint + "_" + requestParam);
        String actual = null;

        String requestURI = "https://" + baseURL + "/" + helper.getConfig("api.version") + "/" + helper.getConfig("api." + endPoint + ".endpoint");
        requestURI = requestURI + "/" + requestParam;
        Response response = createRestRequest(method, context.getContext("requestHeader"), requestURI, requestParam);

        assert response != null;
        if (response.getStatusCode() == 200) {
            actual = response.getBody().asPrettyString();
        }
        Assert.assertEquals(expected, actual);
    }

    @And("User waits for page load in {int} timeout")
    public void userWaitsForPageLoadInTimeout(int timeout) {
        commonPage.waitForPageLoad(timeout);
    }

    @And("User waits for page load completed")
    public void userWaitsForPageLoad() {
        commonPage.waitForPageLoad(WebDriverUtil.TIMEOUT);
    }

    @And("User read the email which was sent out to the {string}")
    public void userReadTheEmailWhichWasSentOutToThe(String recipient) {
        String title = context.getContext("emailSubject");
        context.setContext("emailContent", EmailUtil.getEmailContent(title, recipient));
    }

    @And("User validate the {string} by {string} comparison based on {string}")
    public void userValidateTheByComparisonAndBasedOn(String templateName, String method, String baseline) throws Exception {
        commonPage.validateTemplate(templateName, method, baseline);
    }

    @When("User loads data from json {string}")
    public void userLoadDataFromJson(String fileName) {
        JsonObject jsonObject = helper.readJsonDataFile(fileName);
        context.setContext("jsonData", jsonObject);
        Set<String> keys = new HashSet<>();
        helper.parseAllKeys(keys, jsonObject);
        for (String key : keys) {
            context.setContext(key, jsonObject.get(key));
        }
    }

    @When("User read data from Json by path {string}")
    public void userReadDataFromJsonByPath(String jsonPath) {
        JsonObject jsonObject = context.getContext("jsonData");
        String print = helper.readJsonByPath(jsonObject, jsonPath);
        logger.info(print);
    }

    @When("User store {string} value into {string}")
    public void userStoreValueInto(String elementName, String key) {
        String text = getElementText(elementName);
        logger.info("Store {}'s value: {} into {}", elementName, text, key);
        context.setContext(key, text);
    }

    @When("User select date {string} on {string}")
    public void userSelectDateOn(String date, String elementName) {
        clickElement(elementName);
        commonPage.datePicker(date);
        delaySync(1);
    }

    @Then("{string} matches regex {string}")
    public void matchesRegex(String elementName, String regex) {
        String text = getElementText(elementName);
        Pattern p = Pattern.compile(context.translate(regex));
        Assert.assertTrue(p.matcher(text).find());
    }

    @And("User sort column {string} by {string}")
    public void userSortBy(String columnName, String sortOption) {
        String columnHeaderXpath = "//table//th[.='" + columnName + "']";
        String sortOptionXpath = "//table//th[.='" + columnName + "']//i[contains(@class,'" + sortOption + "')]";
        String sortAttribute = getElementByXPath(columnHeaderXpath).getAttribute("class");
        if (!sortAttribute.contains(sortOption)) {
            getElementByXPath(sortOptionXpath).click();

        }
    }

    @Then("Css {string} of {string} has {string}")
    public void cssOfHas(String attribute, String elementName, String expectedValue) {
        assertElementCssHasValue(attribute, elementName, expectedValue);
    }

    @And("{string} type {string} exist in column {string} of {string} table")
    public void existInColumnOfTable(String type, String lookUpVal, String header, String table) {
        List<String> actual = commonPage.getAllRowsByHeader(table, header);
        if (type.equalsIgnoreCase("context"))
            Assert.assertTrue(actual.contains(context.getContext(lookUpVal)));
        else
            Assert.assertTrue(actual.contains(lookUpVal));
    }

    @And("{string} type {string} not exist in column {string} of {string} table")
    public void notExistInColumnOfTable(String type, String lookUpVal, String header, String table) {
        List<String> actual = commonPage.getAllRowsByHeader(table, header);
        if (type.equalsIgnoreCase("context"))
            Assert.assertFalse(actual.contains(context.getContext(lookUpVal)));
        else
            Assert.assertFalse(actual.contains(lookUpVal));
    }

    @And("User get text of {string} to {string}")
    public void userGetValueOfTo(String elementName, String saveVar) {
        this.isPresentInTimeout(elementName, 8);
        context.setContext(saveVar, getElementText(elementName));
    }

    @And("User extract login cookie of {string}")
    public void userExtractLoginCookieOf(String env) {
        customDriverSteps.userStartNewCustomDriverAndLogging();

        redirectTo(context.getContext("baseURL"));
        String user = helper.getConfig("env." + env + ".login.user");
        String pass = helper.getConfig("env." + env + ".login.pass");
        pass = helper.decodePassPhrase(pass);

        assertPageShowUpInGivenTimeout("PRE_LOGIN_PAGE", 5);
        clickElement("PRE_LOGIN_PAGE_CONTINUE_BUTTON");
        assertPageShowUp("LOGIN_PAGE");

        typeText(user, "LOGIN_PAGE_USERNAME_INPUT");
        clickElement("LOGIN_PAGE_SUBMIT_BUTTON");
        typeText(pass, "LOGIN_PAGE_PASSWORD_INPUT");
        clickElement("LOGIN_PAGE_SUBMIT_BUTTON");
        assertPageShowUpInGivenTimeout("MASTER_PAGE", 10);

        String xpath = "//ul[@class='el-menu']//child::span[contains(text(),'見積書')]//parent::li[1]";
        getElementByXPath(xpath).click();

        assertPageShowUp("QUOTATION_PAGE");
        typeText("100", "QUOTATION_PAGE_FILTER_BY_QUOTATION_NUM_INPUT");
        clickElement("QUOTATION_PAGE_FILTER_BUTTON");
        helper.delaySync(2);

        LogEntries logs;
        logs = driver.get().manage().logs().get("performance");
        String keyword = "arm_jwt_refresh";
        String origin = "";

        for (Iterator<LogEntry> it = logs.iterator(); it.hasNext(); ) {
            LogEntry entry = it.next();
            if (entry.getMessage().contains(keyword)) {
                origin = entry.getMessage().trim();
                break;
            }
        }
        String lookUp = "arm_jwt_access=";
        String[] arr = origin.split(";");

        origin = arr[10];
        origin = origin.substring(origin.indexOf(lookUp));
        context.setContext("loggedInCookie", origin);
        customDriverSteps.userSwitchToMainDriver();
    }

    @And("{string} exist in column {string} of {string} table")
    public void existInColumnOfTable(String lookUpVal, String header, String table) {
        List<String> actual = commonPage.getAllRowsByHeader(table, header);
        Assert.assertTrue(actual.contains(lookUpVal));

    }

    @And("{string} not exist in column {string} of {string} table")
    public void notExistInColumnOfTable(String lookUpVal, String header, String table) {
        List<String> actual = commonPage.getAllRowsByHeader(table, header);
        Assert.assertFalse(actual.contains(lookUpVal));
    }

    @Then("User click on Web Table cell by context {string} by column {string}")
    public void userClickOnWebTableCellByContextByColumn(String contextKey, String header) {
        commonPage.getWebTableCell(header, context.getContext(contextKey).toString()).click();
        helper.delaySync(5);
    }

    @And("User get {string} id {string}")
    public void userGetId(String endPoint, String itemID) {
        String urlExtent = null;
        endPoint = endPoint.toLowerCase().trim();

        switch (endPoint) {
            case "quotation":
                urlExtent = "/quotations/";
                break;
            case "opportunity":
            case "sales":
                urlExtent = "/transactions/";
                break;
            case "invoice":
                urlExtent = "/invoices/";
                break;
            case "reconciliation":
                urlExtent = "/reconciliations/";
                break;
        }

        String baseURL = helper.getConfig("env." + context.getContext("env") + ".url");
        String requestParam = getCurrentURL().split("https://" + baseURL + urlExtent)[1];
        context.setContext(itemID, requestParam);
    }

    @And("User create new {string} request to the endpoint {string} with ID {string}")
    public void userCreateNewRequestToTheEndpointWithID(String method, String endPoint, String itemID) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", context.getContext("loggedInCookie"));
        context.setContext("requestHeader", headers);

        String baseURL = helper.getConfig("env." + context.getContext("env") + ".url");

        String requestParam = null;
        if (context.getContext(itemID) != null)
            requestParam = context.getContext(itemID);
        else
            requestParam = itemID;

        String requestURI = "https://" + baseURL + "/" + helper.getConfig("api.version") + "/" + helper.getConfig("api." + endPoint + ".endpoint");

        requestURI = requestURI + "/" + requestParam;
        Response response = createRestRequest(method, headers, requestURI, requestParam);

        assert response != null;
        if (response.getStatusCode() == 200) {
            context.setContext(requestParam, response.getBody().asPrettyString());
        }
    }


    @And("Verify the response of {string} request to the endpoint {string} with current data should same as {string}")
    public void verifyTheResponseOfRequestToTheEndpointWithCurrentDataShouldSameAs(String method, String endPoint, String expected) {
        userGetId(endPoint, "currentID");
        String requestParam = context.getContext("currentID");
        userCreateNewRequestToTheEndpointWithID(method, endPoint, requestParam);

        Assert.assertSame(context.getContext(requestParam), context.getContext(expected));
    }

    @And("User update response {string} from {string} to {string}")
    public void userUpdateResponseFromTo(String responseID, String oldValue, String newValue) {
        String newResponse = context.getContext(context.getContext(responseID)).toString().replace(context.getContext(oldValue), context.getContext(newValue));
        context.setContext(context.getContext(responseID), newResponse);
    }

    @And("User get attribute {string} of {string} to {string}")
    public void userGetAttributeOfTo(String attribute, String elementName, String keyValue) {
        context.setContext(keyValue, getElement(elementName).getAttribute(attribute));
    }

    @Then("The length of input field {string} should be {int}")
    public void theLengthOfInputFieldShouldBe(String elementName, int length) {
        String value = getElement(elementName).getAttribute("value").trim();
        Assert.assertEquals(length, value.length());
    }


    @And("User delete file {string}")
    public void userDeleteFile(String filePath) throws IOException {
        //filePath sample "src/test/resources/download/invoice.pdf"
        String[] arrayPath = filePath.split("/");
        if (helper.isFileExists(arrayPath)) {
            //helper.deleteAllFiles(arrayPath);
            FileUtils.delete(new File(csUtil.getFullPathFromFragments(arrayPath)));
        }
    }

    @Then("Verify  file {string} exist in {int} timeout")
    public void verifyFileExistInTimeout(String filePath, int timeout) throws InterruptedException {
        //filePath sample src/test/resources/download/invoice.pdf
        Assert.assertTrue(helper.isFileExists(filePath, timeout));
    }

    @Then("Verify  file {string} exist in default download folder in {int} timeout")
    public void verifyFileExistInDefaultDownloadFolderInTimeout(String fileName, int timeout) throws InterruptedException {
        String filePath = "src/test/resources/download/" + fileName;
        System.out.println(filePath);
        Assert.assertTrue(helper.isFileExists(filePath, timeout));
    }

    @And("User select office by {string} and {string}")
    public void userSelectOfficeByAnd(String officeName, String officeCode) {
        String locator = "//div[@class='office__name' and normalize-space(text())='" + officeName + "']//following::div[@class='office__identification-code' and normalize-space(text())='" + officeCode + "']//following::button[1]";
        getElementByXPath(locator).click();
    }

    @When("User stop for debugging")
    public void userStopForDebugging() {
        logger.debug("DEBUG POINT");
    }

    @Then("{string} is present then storing its text")
    public void isPresentThenStoringItsText(String elementName) {
        assertElementPresent(elementName);
        context.setContext("TEXT_OF_" + elementName, getElementText(elementName));
    }

    @And("User clean up download directory")
    public void userCleanUpDownloadDirectory() throws IOException {
        FileUtils.cleanDirectory(new File(csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "download"})));
    }

    @And("User login with valid credentials")
    public void userLoginWithValidCredentials() {
        if (!isElementPresent("MASTER_PAGE_LEFT_MENU")) {
            userRedirectsTo("ta.staging");
            assertPageShowUp("LOGIN_PAGE");
            LoginSteps loginSteps = new LoginSteps();
            loginSteps.userEnterValidCredentials();
            assertPageShowUpInGivenTimeout("MASTER_PAGE", 6);
        }
    }

    @Then("Url contains {string}")
    public void urlContains(String url) {
        waitForUrlContains(url);
    }

    @When("User enter a string with {int} numbers into {string}")
    public void userEnterAStringWithNumbersInto(int length, String elementName) {
        typeText(CustomStringUtil.randomNumeric(length), elementName);
    }

    @When("User select item {string} on the dropdown {string} by {string}")
    public void userSelectItemOnTheDropdownBy(String items, String dropdownName, String optionXpath) {
        DropDownHelper dropDownHelper = new DropDownHelper(getElement(dropdownName), getElementLocator(optionXpath));
        dropDownHelper.select(translateText(items).split(","));
    }

    @When("Verify that item {string} is showing in dropdown {string} by {string}")
    public void verifyThatItemIsShowingInDropdownBy(String items, String dropdownName, String optionXpath) {
        DropDownHelper dropDownHelper = new DropDownHelper(getElement(dropdownName), getElementLocator(optionXpath));
        Arrays.stream(items.split(",")).map(String::trim).map(this::translateText)
                .forEach(e -> Assert.assertTrue(e + " is NOT showing in dropdown", dropDownHelper.isExist(e)));

    }

    @When("Verify that item {string} is not showing in dropdown {string} by {string}")
    public void verifyThatItemIsNotShowingInDropdownBy(String items, String dropdownName, String optionXpath) {
        DropDownHelper dropDownHelper = new DropDownHelper(getElement(dropdownName), getElementLocator(optionXpath));
        Arrays.stream(items.split(",")).map(String::trim).map(this::translateText)
                .forEach(e -> Assert.assertFalse(e + " is showing in dropdown", dropDownHelper.isExist(e)));

    }

    @When("User concatenates string {string} and {string} then stores into {string}")
    public void userConcatenatesStringAndThenStoresInto(String str1, String str2, String key) {
        str1 = translateText(str1);
        str2 = translateText(str2);
        context.setContext(key, str1.concat(str2));
    }

    @When("User waits for {string} is invisible in {int} seconds")
    public void userWaitsForIsInvisibleInSeconds(String elementName, int timeout) {
        waitForElementInvisible(getElementLocator(elementName), timeout);
    }

    @When("User waits for {string} is invisible")
    public void userWaitsForIsInvisibleInSeconds(String elementName) {
        userWaitsForIsInvisibleInSeconds(elementName, TIMEOUT);
    }

    @When("User generates {string} then stores into {string}")
    public void userGeneratesThenStoresInto(String text, String key) {
        text = translateText(text);
        logger.info("Generated string: {} > store into: {}", text, key);
        context.setContext(key, text);
    }

    @Then("{string} is greater than {string}")
    public void isGreaterThan(String elementName, String expectedValue) {
        int value = Integer.parseInt(getElementText(elementName));
        int expValueInt = Integer.parseInt(translateText(expectedValue));
        String message = String.format("%d is not greater than %d", value, expValueInt);
        Assert.assertTrue(message, Matchers.greaterThan(expValueInt).matches(value));
    }

    @Then("Verify that error message {string} is displayed under field {string} on popup")
    public void verifyThatErrorMessageIsDisplayedUnderFieldOnPopup(String expected, String field) {
        String fmt = "//form//tr[.//*[normalize-space()='%s']]//div[@data-test='errorMessage']";
        String xpath = String.format(fmt, field);
        String errorMessage = getText(element(xpath));
        Assert.assertEquals(expected, errorMessage);
    }

    @And("User zoom browser by {string} percentage")
    public void userZoomBrowserByPercentage(String percentage) {
        delaySync(2);
        zoomBrowser(Double.parseDouble(percentage.trim()));
    }
}
