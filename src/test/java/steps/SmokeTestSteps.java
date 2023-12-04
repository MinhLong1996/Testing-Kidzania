package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SmokeTestSteps extends BaseStep {
    private StringBuilder brokenURLs = new StringBuilder();
    private StringBuilder verifiedURLs = new StringBuilder();
    private CommonSteps commonSteps = new CommonSteps();
    private String locator;

    @And("Target page should be loaded completely")
    public void targetPageShouldBeLoadedCompletely() {
        waitForPageLoaded();
        assertElementNotPresent("MASTER_PAGE_ERROR_PAGE_LABEL");
    }

    @And("Validate all URLs on page")
    public void validateAllUrlOnPage() {
        findBrokenURL(brokenURLs, verifiedURLs);
//        Assert.assertTrue(brokenURLs.toString().equals(""));
    }

    @And("Collect report")
    public void collectReport() {
        if (!brokenURLs.toString().equals("")) {
            helper.writeLogToReport("Broken URLs", String.valueOf(brokenURLs));
        }
        helper.writeLogToReport("Verified URLs", String.valueOf(verifiedURLs));
        Assert.assertEquals("", brokenURLs.toString());
    }

    @When("User verify {string}")
    public void userVerify(String levels) {
        if (!levels.equalsIgnoreCase("n/a")) {
            String[] arrLevels = levels.split(" - ");
            for (String level : arrLevels) {
//                goToURL(level.trim());
                commonSteps.userGoesTo(level.trim());
                targetPageShouldBeLoadedCompletely();
                validateAllUrlOnPage();
            }
        }
        collectReport();
    }

    @When("User verify filter for {string} based on {string}")
    public void userVerifyFilterForBasedOn(String funcName, String data) {
        String[] arrData = data.split(";");
        if (null != context.getContext("subPageName"))
            funcName = context.getContext("subPageName").toString().replace("_PAGE", "");
        for (String item : arrData) {
            String field = item.split(" - ")[0].trim();
            String value = item.split(" - ")[1].trim();
            if (!funcName.equals("Tax Adjustment"))
                clickElement(funcName.toUpperCase().replace(" ", "_") + "_PAGE_RESET_BTN");

            switch (funcName) {
                case "TA_EMPLOYEES":
                    switch (field) {
                        case "事業所":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_OFFICE_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_OFFICE_DDL");
                            break;
                        case "部門":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_DEPARTMENT_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_DEPARTMENT_DDL");
                            break;
                        case "契約種別":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_CONTRACT_TYPE_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_CONTRACT_TYPE_DDL");
                            break;
                        case "在籍状況":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_ENROLLMENT_STATUS_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_ENROLLMENT_STATUS_DDL");
                            break;
                        case "税額区分":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_TAX_CATEGORY_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_TAX_CATEGORY_DDL");
                            break;
                        case "メールアドレス":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_MAIL_ADDRESS_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_MAIL_ADDRESS_DDL");
                            break;
                        case "原本回収":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_ORIGINAL_COLLECTION_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_ORIGINAL_COLLECTION_DDL");
                            break;
                        case "アラート":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_ALERT_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_ALERT_DDL");
                            break;
                        case "コメント":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_COMMENT_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_COMMENT_DDL");
                            break;
                        case "年末調整対象外理由":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_REASON_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_REASON_DDL");
                            break;
                        case "回収・計算ステータス":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_COLLECTION_STATUS_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_COLLECTION_STATUS_DDL");
                            break;
                        case "ログイン状況":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_LOGIN_STATUS_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_LOGIN_STATUS_DDL");
                            break;
                        case "税区分":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_TAX_CLASSIFICATION_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_TAX_CLASSIFICATION_DDL");
                            break;
                        case "申告書/配偶者":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_DECLARATION_SPOUSE_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_DECLARATION_SPOUSE_DDL");
                            break;
                        case "申告書/保険料控除":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_DECLARATION_INSURANCE_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_DECLARATION_INSURANCE_DDL");
                            break;
                        case "申告書/扶養親族":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_DECLARATION_DEPENDENTS_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_DECLARATION_DEPENDENTS_DDL");
                            break;
                        case "申告書/住宅ローン":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_TAX_MORTGAGE_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_TAX_MORTGAGE_DDL");
                            break;
                        case "申告書/前職":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_DECLARATION_PREVIOUS_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_DECLARATION_PREVIOUS_DDL");
                            break;
                        case "アラート/種別":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_ALERT_TYPE_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_ALERT_TYPE_DDL");
                            break;
                        case "アラート/箇所":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_ALERT_LOCATION_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_ALERT_LOCATION_DDL");
                            break;
                        case "エラー/ステータス":
                            commonSteps.userSelectItemOnTheDropdown(value, "TA_EMPLOYEES_PAGE_FILTER_BY_ERROR_STATUS_DROPDOWN_BTN > TA_EMPLOYEES_PAGE_FILTER_BY_ERROR_STATUS_DDL");
                            break;
                        case "氏名（従業員番号）":
                            typeText(value, "TA_EMPLOYEES_PAGE_FILTER_BY_EMPLOYEE_NUMBER_TXT");
                            break;
                        case "入社日":
                            typeText(value, "TA_EMPLOYEES_PAGE_FILTER_BY_HIRE_DATE_TXT");
                            delaySync(0.5);
                            pressKey("ENTER");
                            delaySync(0.5);
                            pressKey("ESC");
                            delaySync(0.5);
                            break;
                        case "退職日":
                            typeText(value, "TA_EMPLOYEES_PAGE_FILTER_BY_RETIREMENT_DATE_TXT");
                            delaySync(0.5);
                            pressKey("ENTER");
                            delaySync(0.5);
                            pressKey("ESC");
                            delaySync(0.5);
                            break;
                        case "アラート/日付":
                            typeText(value, "TA_EMPLOYEES_PAGE_FILTER_BY_ALERT_DATE_TXT");
                            delaySync(0.5);
                            pressKey("ENTER");
                            delaySync(0.5);
                            pressKey("ESC");
                            delaySync(0.5);
                            break;
                    }
                    break;
                case "Tax Adjustment":
                    switch (field) {
                        case "年度を選択":
                            commonSteps.userSelectItemOnTheDropdown(value, "TAX_ADJUSTMENT_PAGE_FILTER_BY_YEAR_DROPDOWN_BTN > TAX_ADJUSTMENT_PAGE_FILTER_BY_YEAR_DDL");
                            break;
                    }
                    break;
                case "Authority":
                    switch (field) {
                        case "氏名（従業員番号）":
                            typeText(value, "AUTHORITY_PAGE_FILTER_BY_NAME_OR_EMPL_NUMB_TXT");
                            break;
                        case "メールアドレス":
                            typeText(value, "AUTHORITY_PAGE_FILTER_BY_EMAIL_TXT");
                            break;
                        case "権限名":
                            commonSteps.userSelectItemOnTheDropdown(value, "AUTHORITY_PAGE_FILTER_BY_AUTHORITY_NAME_DROPDOWN_BTN > AUTHORITY_PAGE_FILTER_BY_AUTHORITY_NAME_DROPDOWN_DDL");
                            break;
                    }
                    break;
                case "Employee":
                    switch (field) {
                        case "氏名（従業員番号）":
                            typeText(value, "EMPLOYEE_PAGE_FILTER_BY_NAME_OR_EMPL_NUMB_TXT");
                            break;
                        case "メールアドレス":
                            typeText(value, "EMPLOYEE_PAGE_FILTER_BY_EMAIL_TXT");
                            break;
                        case "入社日":
                            typeText(value, "EMPLOYEE_PAGE_FILTER_BY_HIRE_DATE_TXT");
                            break;
                        case "退職日":
                            typeText(value, "EMPLOYEE_PAGE_FILTER_BY_RETIREMENT_DATE_TXT");
                            break;
                        case "事業所":
                            commonSteps.userSelectItemOnTheDropdown(value, "EMPLOYEE_PAGE_FILTER_BY_OFFICE_DROPDOWN_BTN > EMPLOYEE_PAGE_FILTER_BY_OFFICE_DROPDOWN_DDL");
                            break;
                        case "権限":
                            commonSteps.userSelectItemOnTheDropdown(value, "EMPLOYEE_PAGE_FILTER_BY_AUTHORITY_DROPDOWN_BTN > EMPLOYEE_PAGE_FILTER_BY_AUTHORITY_DROPDOWN_DDL");
                            break;
                        case "在籍状況":
                            commonSteps.userSelectItemOnTheDropdown(value, "EMPLOYEE_PAGE_FILTER_BY_ENROLLMENT_STATUS_DROPDOWN_BTN > EMPLOYEE_PAGE_FILTER_BY_ENROLLMENT_STATUS_DROPDOWN_DDL");
                            break;
                        case "連携":
                            commonSteps.userSelectItemOnTheDropdown(value, "EMPLOYEE_PAGE_FILTER_BY_ALIGNMENT_DROPDOWN_BTN > EMPLOYEE_PAGE_FILTER_BY_ALIGNMENT_DROPDOWN_DDL");
                            break;
                    }
                    break;
            }
            delaySync(0.5);

            if (!funcName.equals("Tax Adjustment"))
                clickElement(funcName.toUpperCase().replace(" ", "_") + "_PAGE_FILTER_BTN");
            delaySync(0.5);
            assertElementNotPresent("MASTER_PAGE_LOADING_SPINNER_LABEL");
            Assert.assertTrue(assertPageLoaded(5));
        }
    }

    @And("User verify detail as {string} for {string} based on {string}")
    public void userVerifyDetailAsForBasedOn(String checkPoint, String funcName, String data) {
    }

    @And("User open {string} and verify {string} if applicable")
    public void userOpenAndVerifyIfApplicable(String url, String pageName) {
        if (!url.equalsIgnoreCase("n/a")) {
            String baseUrl = context.getContext("baseURL");
            url = baseUrl + url;
            redirectTo(url);
            assertPageShowUp(pageName);
            context.setContext("subPageName", pageName);
        }
    }

    @When("User verify {string} on {string}")
    public void userVerifyOn(String testedFunc, String pageName) throws InterruptedException {
        String buttonName;
        switch (pageName) {
            case "YETA_PAGE":
                if (!testedFunc.contains("Employee Detail")) {
                    buttonName = testedFunc.split("-")[0].trim().replace(" ", "_").toUpperCase();
                    buttonName = pageName + "_VERTICAL_MENU_" + buttonName + "_TAB";
                    clickElement(buttonName);
                } else {
                    String xpath = "(//a[contains(text(),'tau')])[1]";
                    getElementByXPath(xpath).click();
                    commonSteps.userSwitchToTab(2);
                }
                delaySync(0.5);
                if (!testedFunc.equals("My Number")) {
                    buttonName = testedFunc.split("-")[1].trim().replace(" ", "_").toUpperCase();
                    buttonName = pageName + "_HORIZONTAL_MENU_" + buttonName + "_TAB";
                    clickElement(buttonName);
                    waitForPageLoaded();
                }

                switch (testedFunc) {
                    case "General setting - Basic setting":
                    case "General setting - Declaration Guidelines":
                    case "General setting - Notification":
                        if (testedFunc.contains("Declaration Guidelines")) {
                            String xpathOrigin = "//button[text()='編集']";
                            String xpath;
                            List<WebElement> elems = getElementsByXPath(xpathOrigin);
                            for (int i = 0; i < elems.size(); i++) {
                                xpath = "(" + xpathOrigin + ")[" + (i + 1) + "]";
                                getElementByXPath(xpath).click();
                                isElementPresent(pageName + "_SUBMIT_BUTTON");
                                clickElement(pageName + "_SUBMIT_BUTTON");
                                isElementPresent(pageName + "_SUCCESS_1_MESSAGE_LABEL");
                                waitForPageLoaded();
                            }
                        } else {
                            clickElement(pageName + "_SUBMIT_BUTTON");
                            isElementPresent(pageName + "_SUCCESS_MESSAGE_LABEL");
                        }
                        break;
                    case "Import - Employee information":
                    case "Import - Payroll information":
                    case "Import - My number":
                    case "Import - Insurance information":
                    case "Import - Housing loan":
                    case "Import - Previous withholding slip":
                    case "Import - Employee calculation":
                    case "Import - Other calculation":
                        waitForPageLoaded();
                        clickElementIfPresent("YETA_PAGE_IMPORT_FAILURE_CLOSE_BTN");
                        clickElement("YETA_PAGE_EXPORT_BTN");
                        delaySync(5);
                        waitForPageLoaded();
                        isElementPresent("YETA_PAGE_EXPORT_SUCCESS_LBL");
                        isElementPresent("YETA_PAGE_EXPORT_FILE_LNK");
                        String fileName = getElementText("YETA_PAGE_EXPORT_FILE_LNK");
                        String filePath = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "download", fileName});
                        helper.deleteFile(filePath);

                        if (!(testedFunc.contains("Employee calculation")) && !(testedFunc.contains("Other calculation"))) {
                            clickElement("YETA_PAGE_EXPORT_FILE_LNK");
                            if (!helper.isFileExists("/src/test/resources/download/" + fileName, 60)) {
                                helper.writeStepFailed("ERROR: File not found <" + filePath + ">");
                            } else {
                                typeTextWithoutClear(filePath, "YETA_PAGE_UPLOAD_FILE_INPUT");
                            }
                            isElementPresent("YETA_PAGE_UPLOADED_FILE_NAME_LBL");
                            clickElement("YETA_PAGE_IMPORT_BTN");
                            targetPageShouldBeLoadedCompletely();
                            isElementPresent("YETA_PAGE_IMPORT_FAILURE_LBL");
                        }
                        break;
                    case "Export - Basic Deduction":
                    case "Export - Dependent Deduction":
                    case "Export - Dependent Deduction For Next Term":
                    case "Export - Insurance Deduction":
                    case "Export - Withholding Book":
                    case "Export - Withholding Slip":
                    case "Export - Payment Report Total Table":
                        waitForPageLoaded();
                        clickElementIfPresent("YETA_PAGE_IMPORT_FAILURE_CLOSE_BTN");
                        clickElement("YETA_PAGE_EXPORT_BTN");
                        delaySync(5);
                        waitForPageLoaded();
                        isElementPresent("YETA_PAGE_EXPORT_SUCCESS_LBL");
                        isElementPresent("YETA_PAGE_EXPORT_FILE_LNK");
                        break;
                    case "Employee Detail - Declaration Form":
                    case "Employee Detail - Withholding Book":
                    case "Employee Detail - Withholding Slip":
                    case "Employee Detail - Payroll Payment report":
                        int formCount;
                        try {
                            formCount = getElements("YETA_PAGE_FORM_LIST_LNK").size();
                        } catch (Exception e) {
                            formCount = 0;
                        }
                        if (formCount > 0) {
                            for (int i = 0; i < formCount; i++) {
                                String xpath = getElementLocator("YETA_PAGE_FORM_LIST_LNK") + "[" + (i + 1) + "]";
                                getElementByXPath(xpath).click();
                                waitForPageLoaded();

                                clickElement("YETA_PAGE_FORM_PREVIEW_BTN");
                                commonSteps.userSwitchToTab(3);
                                waitForPageLoaded();
                                commonSteps.closeCurrentTab();
                                commonSteps.userSwitchToTab(2);
                                clickElement("YETA_PAGE_FORM_DOWNLOAD_BTN");
//                            commonSteps.userSwitchToTab(3);
                                delaySync(5);
                            }
                        } else {
//                            String xpath = getElementLocator("YETA_PAGE_FORM_LIST_LNK") + "[" + (i + 1) + "]";
//                            getElementByXPath(xpath).click();
//                            waitForPageLoaded();

                            clickElement("YETA_PAGE_FORM_PREVIEW_BTN");
                            commonSteps.userSwitchToTab(3);
                            waitForPageLoaded();
                            commonSteps.closeCurrentTab();
                            commonSteps.userSwitchToTab(2);
                            clickElement("YETA_PAGE_FORM_DOWNLOAD_BTN");
//                            commonSteps.userSwitchToTab(3);
                            delaySync(5);
                        }
//                        waitForPageLoaded();
                        break;
                    case "Employee Detail - Calculation":
                        clickElement("YETA_PAGE_EDIT_CALCULATION_BTN");
                        isElementPresent("YETA_PAGE_CALCULATION_FORM_TBL");
                        String xpath = "(" + getElementLocator("YETA_PAGE_CALCULATION_FORM_TBL") + "//child::td[3]//child::input)[1]";
                        typeText("12345", getElementByXPath(xpath));
                        clickElement("YETA_PAGE_SUBMIT_CALCULATION_BTN");
                        isElementPresent("YETA_PAGE_SUCCESS_2_MESSAGE_LABEL");
                        delaySync(5);
                        break;
                    case "My Number":
                        clickElement("YETA_PAGE_UPDATE_FROM_CLOUD_BTN");
                        isElementPresent("YETA_PAGE_UPDATE_FROM_CLOUD_SUCCESS_MESSAGE_LBL");
                        break;
                }
                break;
        }
    }
}

    


