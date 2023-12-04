package steps;

import core.util.WebDriverUtil;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.CommonPage;
import pages.QuotationPage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class QuotationDefaultSetting extends BaseStep {
    private LeftMenuSteps leftMenuSteps = new LeftMenuSteps();
    private QuotationPage quotationPage = new QuotationPage();
    private CommonPage commonPage = new CommonPage();
    private CustomDriverSteps customDriverSteps = new CustomDriverSteps();

    @And("User upload a {string} image with {string} size into {string}")
    public void userUploadAImageWithSizeInto(String type, String size, String elementName) {
        String pref = "image-";
        String post = ".jpeg";
        String fileName = pref + size + post;
        String[] pathToFile = new String[]{"src", "test", "resources", "img", "stamp", fileName};

        getElement(elementName).sendKeys(csUtil.getFullPathFromFragments(pathToFile));
        helper.delaySync(1);
        if (elementName.equals("SETTINGS_PAGE_QUOTATION_STAMP_IMAGE_UPLOAD_INPUT")) {
            String src = getElement("SETTINGS_PAGE_QUOTATION_STAMP_IMAGE_HAS_IMAGE").getAttribute("src").trim();
            context.setContext("stampImgSrc", src);
        }
    }

    @Then("{string} should show correct text per inputted")
    public void shouldShowCorrectTextPerInputted(String elementName) {
        String expected;
        String actual;

        assertElementPresent(elementName);
        assertElementDisplayed(elementName);

        if (elementName.contains("ISSUER")) {
            expected = context.getContext("issuerText");
            assertElementShowText(elementName, expected);
        } else if (elementName.contains("REMARKS")) {
            expected = context.getContext("remarksText");
            actual = getElement(elementName).getAttribute("innerText");
            helper.compareEqual(expected, actual);
        } else if (elementName.contains("STAMP")) {
            expected = context.getContext("stampImgSrc");
            assertElementAttributeHasValue("src", elementName, expected);
        }
    }

    @And("{string} should be {string}")
    public void shouldBe(String elementName, String status) {
        if (status.equalsIgnoreCase("disabled")) {
            assertElementAttributeContainValue("class", elementName, status.toUpperCase());
        } else if (status.equalsIgnoreCase("enabled")) {
            assertElementAttributeNotContainValue("class", elementName, "disabled");
        }
    }

    @When("User select first {string} for New Quotation")
    public void userSelectFirstForNewQuotation(String fieldName) {
        assertElementPresentInGivenTimeout("QUOTATION_PAGE_NEW_QUOTATION_ITEM_LIST", 10);
        if (fieldName.equalsIgnoreCase("client")) {
            clickElement("QUOTATION_PAGE_NEW_QUOTATION_CLIENT_NAME_INPUT");
        } else if (fieldName.equalsIgnoreCase("item")) {
            clickElement("QUOTATION_PAGE_NEW_QUOTATION_FIRST_ITEM_NAME_INPUT");
        }
        helper.delaySync(1);
        pressKey("DOWN");
        helper.delaySync(1);
        pressKey("ENTER");
        helper.delaySync(1);
    }

    @And("User saves {string} into context as {string}")
    public void userSavesIntoContextAs(String value, String key) {
        context.setContext(key, value);
    }

    @And("User update Quotation setting using {string} account with Issuer {string}")
    public void userUpdateQuotationSettingUsingAccount(String office, String issuer) {
        customDriverSteps.userStartNewCustomDriver();

        redirectTo(context.getContext("baseURL"));
        assertPageShowUpInGivenTimeout("PRE_LOGIN_PAGE", 5);
        clickElement("PRE_LOGIN_PAGE_CONTINUE_BUTTON");
        assertPageShowUp("LOGIN_PAGE");

        String user = helper.getConfig("env." + context.getContext("env") + "." + office + ".login.user");
        String pass = helper.getConfig("env." + context.getContext("env") + "." + office + ".login.pass");
        pass = helper.decodePassPhrase(pass);

        typeText(user, "LOGIN_PAGE_USERNAME_INPUT");
        clickElement("LOGIN_PAGE_SUBMIT_BUTTON");
        typeText(pass, "LOGIN_PAGE_PASSWORD_INPUT");
        clickElement("LOGIN_PAGE_SUBMIT_BUTTON");
        assertPageShowUpInGivenTimeout("MASTER_PAGE", 10);

        clickElement("MASTER_PAGE_LEFT_MENU_SETTINGS_LABEL");
        assertPageShowUpInGivenTimeout("SETTINGS_PAGE", 10);
        assertElementPresentInGivenTimeout("SETTINGS_PAGE_SUB_MENU_QUOTATION_ITEM_BUTTON", 10);
        clickElement("SETTINGS_PAGE_SUB_MENU_QUOTATION_ITEM_BUTTON");
        assertElementPresent("SETTINGS_PAGE_QUOTATION_PREVIEW_HEADER_LABEL");
        typeText(issuer, "SETTINGS_PAGE_QUOTATION_ISSUER_TEXTAREA");
        clickElement("SETTINGS_PAGE_QUOTATION_SAVE_BUTTON");
        assertElementPresent("SETTINGS_PAGE_QUOTATION_SAVED_TEMPLATE_LABEL");
        customDriverSteps.userSwitchToMainDriver();
        helper.delaySync(2);
    }

    @And("{string} column should be sorted in {string} order by default")
    public void columnShouldBeSortedInOrderByDefault(String header, String order) {
        String xpath = "//span[text()='" + header + "']//parent::div//parent::th";
        Assert.assertTrue(getElementByXPath(xpath).getAttribute("class").contains(order));
    }

    @And("User quick create a quotation")
    public void userQuickCreateAQuotation() {
        this.userSelectFirstForNewQuotation("client");
        this.userSelectFirstForNewQuotation("item");
        clickElement("QUOTATION_PAGE_NEW_QUOTATION_CREATE_BUTTON");
        userIncreaseQuotationNoIfDuplicated();
    }

    @And("User get quotation details {string}")
    public void userGetQuotationDetails(String quotationInfo) {
        String quotationData = "";
        quotationData = getElement("QUOTATION_DETAILS_PAGE_NUMBER_DIV").getText().trim();
        quotationData = quotationData + "," + getElementText("QUOTATION_DETAILS_PAGE_CLIENT_NAME_SPAN");
        quotationData = quotationData + "," + getElementText("QUOTATION_DETAILS_PAGE_CLIENT_ADDRESS_SPAN");
        quotationData = quotationData + "," + getElementText("QUOTATION_DETAILS_PAGE_CLIENT_TITLE_DIV");
        quotationData = quotationData + "," + getElementText("QUOTATION_DETAILS_PAGE_QUOTE_DATE_DIV");
        quotationData = quotationData + "," + getElementText("QUOTATION_DETAILS_PAGE_EXPIRATION_DATE_DIV");
        quotationData = quotationData + "," + getElementText("QUOTATION_DETAILS_PAGE_PUBLISHER_SPAN");
        quotationData = quotationData + "," + getElementText("QUOTATION_DETAILS_PAGE_ITEM_NAME_SPAN");
        quotationData = quotationData + "," + getElementText("QUOTATION_DETAILS_PAGE_ITEM_PRICE_TD");
        quotationData = quotationData + "," + getElementText("QUOTATION_DETAILS_PAGE_ITEM_QUANTITY_TD");
        quotationData = quotationData + "," + getElementText("QUOTATION_DETAILS_PAGE_ITEM_TOTAL_PRICE_TD");
        quotationData = quotationData + "," + getElementText("QUOTATION_DETAILS_PAGE_TOTAL_PRICES_DIV");

        if (isElementPresent("QUOTATION_DETAILS_PAGE_NOTE_SPAN"))
            quotationData = quotationData + "," + getElementText("QUOTATION_DETAILS_PAGE_NOTE_SPAN");
        else
            quotationData = quotationData + ",";

        context.setContext(quotationInfo, quotationData);
    }

    @And("User get quotation edit info {string}")
    public void userGetQuotationEditInfo(String quotationInfo) {
        String quotationData;
        quotationData = getElement("QUOTATION_DUPLICATE_PAGE_NUMBER_INPUT").getAttribute("value");
        quotationData = quotationData + "," + getElement("QUOTATION_DUPLICATE_PAGE_CLIENT_NAME_INPUT").getAttribute("value") + " " + getElement("QUOTATION_DUPLICATE_PAGE_HONOR_TITLE_INPUT").getAttribute("value");
        quotationData = quotationData + "," + getElement("QUOTATION_DUPLICATE_PAGE_CLIENT_DETAILS_TEXTAREA").getAttribute("value");
        quotationData = quotationData + "," + getElement("QUOTATION_DUPLICATE_PAGE_TITLE_INPUT").getAttribute("value");
        quotationData = quotationData + "," + getElement("QUOTATION_DUPLICATE_PAGE_ISSUER_DATE_INPUT").getAttribute("value");
        quotationData = quotationData + "," + getElement("QUOTATION_DUPLICATE_PAGE_EXPIRATION_DATE_INPUT").getAttribute("value");
        quotationData = quotationData + "," + getElementText("QUOTATION_DUPLICATE_PAGE_PUBLISHER_SPAN");
        quotationData = quotationData + "," + getElement("QUOTATION_DUPLICATE_PAGE_ITEM_NAME_INPUT").getAttribute("value");
        quotationData = quotationData + ",¥" + getElement("QUOTATION_DUPLICATE_PAGE_ITEM_PRICE_INPUT").getAttribute("value");
        quotationData = quotationData + "," + getElement("QUOTATION_DUPLICATE_PAGE_ITEM_QUANTITY_INPUT").getAttribute("value");
        quotationData = quotationData + "," + getElementText("QUOTATION_DUPLICATE_PAGE_ITEM_TOTAL_DIV");
        quotationData = quotationData + "," + getElementText("QUOTATION_DUPLICATE_PAGE_TOTAL_PRICES_DIV");
        quotationData = quotationData + "," + getElement("QUOTATION_DUPLICATE_PAGE_NOTE_TEXTAREA").getAttribute("value");
        context.setContext(quotationInfo, quotationData);
    }

    @Then("current edit quotation equal another {string}")
    public void currentEditQuotationEqualAnother(String expected) {
        userGetQuotationEditInfo("currentQuotation");
        String actual = context.getContext("currentQuotation").toString().split(",", 2)[1];
        expected = context.getContext(expected).toString().split(",", 2)[1];

        Assert.assertEquals(actual, expected);
    }

    @Then("current details quotation equal another {string}")
    public void currentDetailsQuotationEqualAnother(String expected) {
        userGetQuotationDetails("currentQuotation");
        String actual = context.getContext("currentQuotation").toString().split(",", 2)[1];
        expected = context.getContext(expected).toString().split(",", 2)[1];

        Assert.assertEquals(actual, expected);
    }


    @Then("last operation is create operation")
    public void lastOperationIsCreateOperation() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime now = LocalDateTime.now();

        String username = getElementText("QUOTATION_PAGE_USERNAME_SPAN").trim();

        Assert.assertEquals(dtf.format(now), getElementByXPath("(//table[@class='el-table__body no-truncate']//tr[1]/td/div)[1]").getText().trim());
        Assert.assertEquals(username, getElementByXPath("(//table[@class='el-table__body no-truncate']//tr[1]/td/div)[2]").getText().trim());
        Assert.assertEquals("作成", getElementByXPath("(//table[@class='el-table__body no-truncate']//tr[1]/td/div)[3]").getText().trim());
        Assert.assertEquals("-", getElementByXPath("(//table[@class='el-table__body no-truncate']//tr[1]/td/div)[4]/span").getText().trim());
        Assert.assertEquals("-", getElementByXPath("(//table[@class='el-table__body no-truncate']//tr[1]/td/div)[5]/span").getText().trim());
    }

    @And("Account from {string} should unable to see {string} in the list")
    public void accountFromShouldUnableToSeeInTheList(String office, String quotationNum) throws Exception {
        String execMode = helper.getConfig("env.custom.driver.exec.mode");
        WebDriver customDriver = getCustomDriver(execMode, false);
        WebDriverUtil customWDU = new WebDriverUtil(customDriver);

        customWDU.redirectTo(context.getContext("baseURL"));
        customWDU.assertPageShowUpInGivenTimeout("PRE_LOGIN_PAGE", 5);
        customWDU.clickElement("PRE_LOGIN_PAGE_CONTINUE_BUTTON");
        customWDU.assertPageShowUp("LOGIN_PAGE");

        String user = helper.getConfig("env." + context.getContext("env") + "." + office + ".login.user");
        String pass = helper.getConfig("env." + context.getContext("env") + "." + office + ".login.pass");
        pass = helper.decodePassPhrase(pass);

        customWDU.typeText(user, "LOGIN_PAGE_USERNAME_INPUT");
        customWDU.clickElement("LOGIN_PAGE_SUBMIT_BUTTON");
        customWDU.typeText(pass, "LOGIN_PAGE_PASSWORD_INPUT");
        customWDU.clickElement("LOGIN_PAGE_SUBMIT_BUTTON");
        customWDU.assertPageShowUpInGivenTimeout("MASTER_PAGE", 10);

        String locator = "//ul[@class='el-menu']//child::span[contains(text(),'見積書')]//parent::li[1]";
        customWDU.getElementByXPath(locator).click();
        customWDU.assertPageShowUpInGivenTimeout("QUOTATION_PAGE", 5);

        quotationNum = context.getContext(quotationNum);
        customWDU.typeText(quotationNum, "QUOTATION_PAGE_FILTER_BY_QUOTATION_NUM_INPUT");
        customWDU.clickElement("QUOTATION_PAGE_FILTER_BUTTON");
        customWDU.assertElementPresent("QUOTATION_PAGE_FILTER_NO_RESULT_LABEL");
        customWDU.terminate();
    }

    @And("User should be switched to {string}")
    public void userShouldBeSwitchedTo(String office) {
        String currentOffice = getElementText("MASTER_PAGE_CURRENT_OFFICE_BTN");
        clickElement("MASTER_PAGE_CURRENT_OFFICE_BTN");
        String officeNumber = csUtil.getPureString(getElementText("MASTER_PAGE_SELECTED_OFFICE_NUMBER_LBL"));
        officeNumber = officeNumber.replace("事業者番号:", "").trim();
        String actual = currentOffice + " (" + officeNumber + ")";
        String expected = helper.getConfig("env.arm.staging." + office);

        if (!expected.equals(actual)) {
            clickElement("MASTER_PAGE_SWITCH_OFFICE_BTN");
            CommonSteps commonSteps = new CommonSteps();
            currentOffice = expected.split("\\(")[0].trim();
            officeNumber = expected.split("\\(")[1].replace(")", "").trim();
            System.out.println("currentOffice = " + currentOffice);
            System.out.println("officeNumber = " + officeNumber);
            commonSteps.userSelectOfficeByAnd(currentOffice, officeNumber);
            helper.delaySync(5);
        }
    }


    @When("User create Quotation {string} with basic data")
    public void userCreateQuotationWithBasicData(String quotationOrder) {
        clickElement("QUOTATION_PAGE_CREATE_BUTTON");
        userSelectFirstForNewQuotation("Client");
        userSelectFirstForNewQuotation("Item");
        clickElement("QUOTATION_PAGE_NEW_QUOTATION_CREATE_BUTTON");
        userIncreaseQuotationNoIfDuplicated();
//        assertElementPresent("QUOTATION_PAGE_NEW_QUOTATION_SAVED_SUCCESS_LABEL");
        assertElementPresentInGivenTimeout("QUOTATION_PAGE_QUOTATION_DETAIL_ISSUER_LABEL", 10);

        String quotationNo = getElementText("QUOTATION_DETAILS_PAGE_NUMBER_DIV");
        context.setContext("newQuotation_" + quotationOrder, quotationNo);
        clickElement("QUOTATION_DETAILS_PAGE_BACK_TO_LIST_LNK");
        assertPageShowUp("QUOTATION_PAGE");
    }

    @And("User change the Quotation No of Quotation {string} to {string}")
    public void userChangeTheQuotationNoOfQuotationTo(String sourceQuotation, String targetQuotation) {
        String selectedQuotationNo = context.getContext("newQuotation_" + sourceQuotation);
        commonPage.getWebTableCell("見積書番号", selectedQuotationNo).click();
        assertPageShowUp("QUOTATION_DETAILS_PAGE");
        clickElement("QUOTATION_DETAILS_PAGE_EDIT_BTN");
        String updatedQuotationNo = String.valueOf(Integer.parseInt(selectedQuotationNo) + 1);
        typeText(updatedQuotationNo, "QUOTATION_PAGE_NEW_QUOTATION_QUOTATION_NUMBER_INPUT");
        clickElement("QUOTATION_PAGE_NEW_QUOTATION_CREATE_BUTTON");
        userIncreaseQuotationNoIfDuplicated();
//        assertElementPresent("QUOTATION_PAGE_NEW_QUOTATION_UPDATED_SUCCESS_LABEL");
        assertElementPresentInGivenTimeout("QUOTATION_PAGE_QUOTATION_DETAIL_ISSUER_LABEL", 10);

        String quotationNo = getElementText("QUOTATION_DETAILS_PAGE_NUMBER_DIV");
        context.setContext("newQuotation_" + targetQuotation, quotationNo);
        clickElement("QUOTATION_DETAILS_PAGE_BACK_TO_LIST_LNK");
        assertPageShowUp("QUOTATION_PAGE");
    }

    @And("User increase Quotation No if duplicated")
    public void userIncreaseQuotationNoIfDuplicated() {
        boolean duplicated = isElementPresent("QUOTATION_PAGE_NEW_QUOTATION_DUPLICATED_QUOTATION_NO_ERROR_MESSAGE_LBL");
        int maxRetry = 10;
        int i = 0;
        if (duplicated) {
            do {
                String currentQuotationNo = getElementText("QUOTATION_PAGE_NEW_QUOTATION_QUOTATION_NUMBER_INPUT");
                String newQuotationNo = String.valueOf(Integer.parseInt(currentQuotationNo) + 1);
                typeText(newQuotationNo, "QUOTATION_PAGE_NEW_QUOTATION_QUOTATION_NUMBER_INPUT");
                clickElement("QUOTATION_PAGE_NEW_QUOTATION_CREATE_BUTTON");
                helper.delaySync(5);
                duplicated = isElementPresent("QUOTATION_PAGE_NEW_QUOTATION_DUPLICATED_QUOTATION_NO_ERROR_MESSAGE_LBL");
                i += 1;
            } while (duplicated && i < maxRetry);
        }
    }

    @And("New quotation number should be auto generated to {string}")
    public void newQuotationNumberShouldBeAutoGeneratedTo(String quotationOrder) {
        String expectedQuotationNo = context.getContext("newQuotation_" + quotationOrder);
        assertElementPresentInGivenTimeout("QUOTATION_PAGE_NEW_QUOTATION_QUOTATION_NUMBER_INPUT", 5);
        String actualQuotationNo = getElementText("QUOTATION_PAGE_NEW_QUOTATION_QUOTATION_NUMBER_INPUT");
        helper.compareEqual(expectedQuotationNo, actualQuotationNo);
    }

    @When("User delete Quotation {string}")
    public void userDeleteQuotation(String quotationOrder) {
        String selectedQuotationNo = context.getContext("newQuotation_" + quotationOrder);
        commonPage.getWebTableCell("見積書番号", selectedQuotationNo).click();
        assertPageShowUp("QUOTATION_DETAILS_PAGE");
        clickElement("QUOTATION_DETAILS_PAGE_DELETE_BTN");
        assertElementPresent("QUOTATION_PAGE_QUOTATION_DETAIL_DELETE_DIALOG");
        clickElement("QUOTATION_PAGE_QUOTATION_DETAIL_DELETE_DIALOG_DELETE_BUTTON");
        assertElementPresent("QUOTATION_PAGE_QUOTATIONS_TABLE");
    }

    @And("User add new Quotation item with data")
    public void userAddNewQuotationItemWithData(DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> columns : rows) {
            assertElementPresentInGivenTimeout("QUOTATION_PAGE_NEW_QUOTATION_ITEM_LIST", 10);
            clickElement("QUOTATION_PAGE_NEW_QUOTATION_ADD_NEW_ITEM_BUTTON");
            delaySync(1);
            String locator = "//form[contains(@class,'drag-item')]";
            String header;
            String value;
            WebElement newItem = getElementsByXPath(locator).get(getElementsByXPath(locator).size() - 1);

            for (Map.Entry<String, String> entry : columns.entrySet()) {
                header = entry.getKey().trim();
                value = entry.getValue().trim();

                if (!value.equalsIgnoreCase("n/a")) {
                    switch (header) {
                        case "品目":
                            newItem.findElements(By.tagName("input")).get(0).sendKeys(value);
                            pressKey("ENTER");
                            break;
                        case "単価 税抜":
                            newItem.findElements(By.tagName("input")).get(1).sendKeys(value);
                            break;
                        case "数量":
                            newItem.findElements(By.tagName("input")).get(2).sendKeys(value);
                            break;
                        case "税率":
                            int size = getElementsByXPath(locator).size();
                            locator = "((//form[contains(@class,'drag-item')])[" + size + "]//child::div[contains(@aria-controls,'dropdown-menu')])[" + size + "]";
                            commonPage.selectItemFromDropdown(value, "QUOTATION_PAGE_NEW_QUOTATION_ITEM_LIST_TAX_RATE_DDL", getElementByXPath(locator));
                            break;
                    }
                }
                delaySync(1);
            }
        }


    }

    @Then("the {string} of Quotation item {string} should be {string}")
    public void theOfQuotationItemShouldBe(String header, String itemNo, String expectedValue) {
        itemNo = itemNo.replace("#", "");
        String locator = "(//form[contains(@class,'drag-item')])[" + itemNo + "]";
        WebElement currentItem = getElementByXPath(locator);
        String actual = null;

        switch (header) {
            case "品目":
                actual = currentItem.findElements(By.tagName("input")).get(0).getAttribute("value").trim();
                break;
            case "単価 税抜":
                actual = currentItem.findElements(By.tagName("input")).get(1).getAttribute("value").trim();
                break;
            case "数量":
                actual = currentItem.findElements(By.tagName("input")).get(2).getAttribute("value").trim();
                break;
            case "税率":
                locator = "((//form[contains(@class,'drag-item')])[" + itemNo + "]//child::div[contains(@aria-controls,'dropdown-menu')])[2]//child::div[1]";
                actual = csUtil.getPureString(getElementByXPath(locator).getText().trim());
                break;
        }
        helper.compareEqual(expectedValue, actual);
    }

    @When("User drag Quotation item {string} to the position of {string}")
    public void userDragQuotationItemToThePositionOf(String sourceItem, String targetItem) {
        String fmt = "form.drag-item:nth-of-type(%s) div.cell i";
        String source = String.format(fmt, sourceItem.replace("#", ""));
        String target = String.format(fmt, targetItem.replace("#", ""));
        WebElement src = element(source);
        WebElement dst = element(target);
        dragAndDrop(src, dst);
    }

    @When("User click {string} button on the item {string}")
    public void userClickButtonOnTheItem(String buttonName, String itemNo) {
        itemNo = itemNo.replace("#", "");
        String locator = "(//form[contains(@class,'drag-item')])[" + itemNo + "]";
        WebElement currentItem = getElementByXPath(locator);

        switch (buttonName) {
            case "詳細":
                currentItem.findElements(By.tagName("button")).get(0).click();
                break;
            case "削除":
                locator = locator + "//child::div[@class='delete-link btn-delete']";
                getElementByXPath(locator).click();
                break;
            case "詳細を削除":
                locator = locator + "//child::div[@class='delete-link btn-delete-detail']";
                getElementByXPath(locator).click();
                break;
        }
    }

    @And("User fill the detail of item {string} as {string}")
    public void userFillTheDetailOfItemAs(String itemNo, String value) {
        itemNo = itemNo.replace("#", "");
        String locator = "(//form[contains(@class,'drag-item')])[" + itemNo + "]";
        WebElement currentItem = getElementByXPath(locator);
        currentItem.findElements(By.tagName("textarea")).get(0).sendKeys(value);
    }

    @Then("Verify current quotation downloaded default download folder in {int} seconds")
    public void verifyCurrentQuotationDownloadedDefaultDownloadFolderInTimeout(int timeout) {
        //This case only apply for quick create quotation or does not change the honorific
        String fileName = getElementText("QUOTATION_DETAILS_PAGE_NUMBER_DIV") + "_"
                + getElementText("QUOTATION_DETAILS_PAGE_CLIENT_NAME_SPAN").replace(" 御中", "御中")
                + getElementText("QUOTATION_DETAILS_PAGE_CLIENT_TITLE_DIV")
                + ".pdf";
        String downloadFolder = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "download"});
        int fileSize = Integer.parseInt(helper.getConfig("env.arm.staging.quotation.pdf.size.min"));
        //there is a space between the honorific and client name, but the pdf name is not, so we need to remove it the space in client name
        logger.info("Folder: {}", downloadFolder);
        logger.info("File name: {}", fileName);
        Assert.assertTrue(quotationPage.isFileDownloaded(downloadFolder, fileName, fileSize, timeout));
    }
}

    


