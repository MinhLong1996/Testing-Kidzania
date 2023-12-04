package pages;

import core.util.CompareUtil;
import core.util.ImageUtil;
import core.util.ScreenshotUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import steps.BaseStep;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommonPage extends BaseStep {

    public int getColumnIndexByHeader(String header) {
        int ret = -1;
        String xpath = "//table//thead//th[not(contains(@style,'display: none'))]";
        List<String> headers = getTextFromElements(xpath);
        for (int index = 0; index < headers.size(); index++) {
            if (header.equalsIgnoreCase(headers.get(index))) {
                ret = index + 1;
                break;
            }
        }
        return ret;
    }

    public boolean lookUpWebTableVertically(String header, String lookedUpVal) {
        String fmt = "//td[count(//th[normalize-space(.)='%s']/preceding-sibling::th) +1][normalize-space(.)='%s']";
        String xpath = String.format(fmt, header, lookedUpVal);
        return isElementVisible(xpath, TIMEOUT_SMALL);
    }

    public WebElement getWebTableCell(String header, String lookedUpVal) {
        String fmt = "//td[count(//th[normalize-space(.)='%s']/preceding-sibling::th) +1][normalize-space(.)='%s']";
        String xpath = String.format(fmt, header, lookedUpVal);
        return getElementByXPath(xpath);
    }

    public WebElement getWebTableRow(String header, String lookedUpVal) {
        String fmt = "//td[count(//th[normalize-space(.)='%s']/preceding-sibling::th) +1][normalize-space(.)='%s']//parent::tr[1]";
        String xpath = String.format(fmt, header, lookedUpVal);
        return getElementByXPath(xpath);
    }

    public List<String> getAllRowsByHeader(String tableName, String header) {
        int colIndex = -1;

        switch (tableName) {
            case "QUOTATION_LIST":
                colIndex = getColumnIndexByHeader(header);
                break;
            default:
                break;
        }
        String xpath = String.format("//table[not(contains(@class,'date'))]//tbody//tr//td[%s]", colIndex);
        return colIndex > 0 ? getTextFromElements(xpath) : null;
    }

    public int getWebTableRowCount() {
        return getElementsByXPath("//tbody/tr").size();
    }

    public void selectFilterByDate(String dateRange, String datePicker, String startBy) {
        clickElement(datePicker);
        delaySync(1);

        String startDate = "";
        String endDate = "";
        if (dateRange.contains("-")) {
            startDate = dateRange.split("-")[0];
            endDate = dateRange.split("-")[1];
        }
        String fromCal = "//div[contains(@class,'is-left')]";
        String toCal = "//div[contains(@class,'is-right')]";
        String singleCal = "//div[@class='el-picker-panel__body']";
        String headerCal = "//child::div[@class='el-date-range-picker__header']";
        String dayCal = "//child::td[@class='available']//child::span[normalize-space(text())='";
        String backwardYear = "//child::button[contains(@class,'el-icon-d-arrow-left')]";
        String backwardMonth = "//child::button[contains(@class,'el-icon-arrow-left')]";
        String forwardYear = "//child::button[contains(@class,'el-icon-d-arrow-right')]";
        String forwardMonth = "//child::button[contains(@class,'el-icon-arrow-right')]";
        String xpathDay;

        switch (startBy) {
            case "START_DATE":
                // select Start Date
                String year = startDate.split("/")[0].trim();
                String month = startDate.split("/")[1].trim();
                String day = startDate.split("/")[2].trim();
                String expected = year + " 年 " + month + "月";
                String actual = fromCal + headerCal + "//child::div[1]";
                String actualText = getElementByXPath(actual).getText().trim();
                String[] arrS = actualText.replace("年", "").replace("月", "").split("  ");

                int maxDateLookUp = 10;
                int actualYear = Integer.parseInt(arrS[0]);
                int actualMonth = Integer.parseInt(arrS[1]);
                int i = 0;
                int clickNum = 0;
                boolean found = false;

                do {
                    if (expected.equals(getElementByXPath(actual).getText().trim()))
                        found = true;
                    else {
                        i += 1;
                        delaySync(1);
                        clickNum = actualYear - Integer.parseInt(year);
                        if (clickNum > 0) {
                            for (int j = clickNum; j > 0; j--) {
                                getElementByXPath(fromCal + headerCal + backwardYear).click();
                                delaySync(0.5);
                            }
                        }

                        clickNum = actualMonth - Integer.parseInt(month);
                        if (clickNum > 0) {
                            for (int j = clickNum; j > 0; j--) {
                                getElementByXPath(fromCal + headerCal + backwardMonth).click();
                                delaySync(0.5);
                            }
                        } else if (clickNum < 0) {
                            for (int j = clickNum; j < 0; j++) {
                                getElementByXPath(toCal + headerCal + forwardMonth).click();
                                delaySync(0.5);
                            }
                        }
                    }
                } while (!found && i < maxDateLookUp);

                if (!found)
                    helper.writeStepFailed("Unable to find the date -> " + startDate);
                else {
                    xpathDay = fromCal + dayCal + day + "']";
                    getElementByXPath(xpathDay).click();
                    delaySync(0.5);
                }

                // select End Date
                day = endDate.split("/")[2].trim();
                if (month.equals(endDate.split("/")[1].trim()))
                    xpathDay = fromCal + dayCal + day + "']";
                else
                    xpathDay = toCal + dayCal + day + "']";

                getElementByXPath(xpathDay).click();
                delaySync(0.5);
                break;
            case "END_DATE":
                // select End Date
                String year1 = endDate.split("/")[0].trim();
                String month1 = endDate.split("/")[1].trim();
                String day1 = endDate.split("/")[2].trim();
                String expected1 = year1 + " 年 " + month1 + "月";
                String actual1 = toCal + headerCal + "//child::div[1]";
                String actualText1 = getElementByXPath(actual1).getText().trim();
                String[] arrS1 = actualText1.replace("年", "").replace("月", "").split("  ");

                int maxDateLookUp1 = 10;
                int actualYear1 = Integer.parseInt(arrS1[0]);
                int actualMonth1 = Integer.parseInt(arrS1[1]);
                int i1 = 0;
                int clickNum1 = 0;
                boolean found1 = false;

                do {
                    if (expected1.equals(getElementByXPath(actual1).getText().trim()))
                        found1 = true;
                    else {
                        i1 += 1;
                        delaySync(1);
                        clickNum1 = Integer.parseInt(year1) - actualYear1;
                        if (clickNum1 > 0) {
                            for (int j = clickNum1; j > 0; j--) {
                                getElementByXPath(toCal + headerCal + forwardYear).click();
                                delaySync(0.5);
                            }
                        }

                        clickNum1 = Integer.parseInt(month1) - actualMonth1;
                        if (clickNum1 > 0) {
                            for (int j = clickNum1; j > 0; j--) {
                                getElementByXPath(toCal + headerCal + forwardMonth).click();
                                delaySync(0.5);
                            }
                        } else if (clickNum1 < 0) {
                            for (int j = clickNum1; j < 0; j++) {
                                getElementByXPath(fromCal + headerCal + backwardMonth).click();
                                delaySync(0.5);
                            }
                        }
                    }
                } while (!found1 && i1 < maxDateLookUp1);

                if (!found1)
                    helper.writeStepFailed("Unable to find the date -> " + endDate);
                else {
                    xpathDay = toCal + dayCal + day1 + "']";
                    getElementByXPath(xpathDay).click();
                    delaySync(0.5);
                }

                // select Start Date
                day1 = startDate.split("/")[2].trim();
                if (month1.equals(startDate.split("/")[1].trim()))
                    xpathDay = toCal + dayCal + day1 + "']";
                else
                    xpathDay = fromCal + dayCal + day1 + "']";

                getElementByXPath(xpathDay).click();
                delaySync(0.5);
                break;
            case "SINGLE_DATE":
                String year2 = dateRange.split("/")[0].trim();
                String month2 = dateRange.split("/")[1].trim();
                String day2 = dateRange.split("/")[2].trim();
                headerCal = headerCal.replace("-range", "");
                String expected2 = year2 + " 年 " + month2 + "月";

                String actual2 = singleCal + headerCal + "//child::span[1]";
                String actualText2 = getElementByXPath(actual2).getText().replace("年", "").trim();
                int actualYear2 = Integer.parseInt(actualText2);

                actual2 = singleCal + headerCal + "//child::span[2]";
                actualText2 = getElementByXPath(actual2).getText().replace("月", "").trim();
                int actualMonth2 = Integer.parseInt(actualText2);

                int maxDateLookUp2 = 10;
                int i2 = 0;
                int clickNum2 = 0;
                boolean found2 = false;

                do {
                    if (expected2.equals(getCalHeader(singleCal, headerCal)))
                        found2 = true;
                    else {
                        i2 += 1;
                        delaySync(1);

                        clickNum2 = actualYear2 - Integer.parseInt(year2);
                        if (clickNum2 > 0) {
                            for (int j = clickNum2; j > 0; j--) {
                                getElementByXPath(singleCal + headerCal + backwardYear).click();
                                delaySync(0.5);
                            }
                        } else if (clickNum2 < 0) {
                            for (int j = clickNum2; j < 0; j++) {
                                getElementByXPath(singleCal + headerCal + forwardYear).click();
                                delaySync(0.5);
                            }
                        }

                        clickNum2 = actualMonth2 - Integer.parseInt(month2);
                        if (clickNum2 > 0) {
                            for (int j = clickNum2; j > 0; j--) {
                                getElementByXPath(singleCal + headerCal + backwardMonth).click();
                                delaySync(0.5);
                            }
                        } else if (clickNum2 < 0) {
                            for (int j = clickNum2; j < 0; j++) {
                                getElementByXPath(singleCal + headerCal + forwardMonth).click();
                                delaySync(0.5);
                            }
                        }
                    }
                } while (!found2 && i2 < maxDateLookUp2);

                if (!found2)
                    helper.writeStepFailed("Unable to find the date -> " + dateRange);
                else {
                    xpathDay = singleCal + dayCal + day2 + "']";
                    getElementByXPath(xpathDay).click();
                    delaySync(0.5);
                }
                break;
        }
    }

    /***
     *
     * @param fullDate format of date: dd/MM/yyyy
     *
     * ***/
    public void datePicker(String fullDate) {
        List<Integer> arr = Arrays.stream(fullDate.split("/")).map(Integer::parseInt).collect(Collectors.toList());
        int date = arr.get(0);
        int month = arr.get(1);
        int year = arr.get(2);

        //locator month
        String fmtCssDataMonth = ".mx-table-month td[data-month='%s']";
        String cssDataMonth = String.format(fmtCssDataMonth, month - 1);
        //locator date
        String fmtCssDataDate = ".mx-table-date td[title$='-%02d']:not(.not-current-month)";
        String cssDataDate = String.format(fmtCssDataDate, date);
        //locator year
        String cssDataYear = String.format(".mx-calendar-content td[data-year='%s']", year);
        String cssHeaderLbl = ".mx-calendar-header-label";
        String cssPreviousYearsBtn = "i.mx-icon-double-left";
        String cssNextYearsBtn = ".mx-icon-double-right";
        String cssHeaderCurrentYearLbl = ".mx-calendar-header-label .mx-btn-current-year";

        //select year
        WebElement lblHeaderCurrentYear = waitForElementPresent(cssHeaderCurrentYearLbl);
        waitForTextToBePresentInElement(lblHeaderCurrentYear, TIMEOUT);
        String currentYear = lblHeaderCurrentYear.getText().trim().replace("年", "");
        if (year != Integer.parseInt(currentYear)) {
            clickOnElement(lblHeaderCurrentYear);
            if (!isElementInvisible(lblHeaderCurrentYear, 2)) {
                clickOnElement(lblHeaderCurrentYear);
            }
            int count = 0;
            while (count < 10) {
                List<Integer> yearRange = Arrays.stream(waitForElementPresent(cssHeaderLbl).getText().trim().split(" "))
                        .map(Integer::parseInt).collect(Collectors.toList());

                int startYear = yearRange.get(0);
                int endYear = yearRange.get(1);

                if (startYear <= year && year <= endYear) {
                    clickOnElement(cssDataYear);
                    break;
                } else if (year < startYear) {
                    clickOnElement(cssPreviousYearsBtn);
                } else {
                    clickOnElement(cssNextYearsBtn);
                }
                count++;
            }
        } else {
            String cssMonthButton = ".mx-calendar-header-label .mx-btn-current-month";
            clickOnElement(cssMonthButton);
            if (!isElementVisible(cssDataMonth, 2)) {
                clickOnElement(cssMonthButton);
            }
        }
        //select month
        clickOnElement(cssDataMonth);
        //select date
        clickOnElement(cssDataDate);
    }

    private String getCalHeader(String root, String header) {
        String actual2 = root + header + "//child::span[1]";
        String actualCal = getElementByXPath(actual2).getText().trim();

        actual2 = root + header + "//child::span[2]";
        return actualCal + " " + getElementByXPath(actual2).getText().trim();
    }

    public void selectItemFromDropdown(String item, String dropdownName, WebElement btnDDL) {
        btnDDL = null != btnDDL ? btnDDL : getElement(dropdownName);
        List<String> items = !item.contains("#") ? List.of(item)
                : Arrays.stream(item.split("#")).map(String::trim).collect(Collectors.toList());
        if (btnDDL.getTagName().equalsIgnoreCase("select")) {
            Select select = new Select(btnDDL);
            items.forEach(e -> select.selectByVisibleText(item));
        } else {
            clickOnElement(btnDDL);
            WebElement ctnDDL = dropdownName.isEmpty() ? btnDDL : getElement(dropdownName);
            items.forEach(e -> {
                String childXpath = String.format(".//*[normalize-space(text())='%s']", e);
                clickOnElement(waitForNestedElementPresent(ctnDDL, childXpath));
                delaySync(0.5);
            });
            if (items.size() > 1) {
                pressKey("ESC");
            }
        }
    }

    public void selectItemFromDropdown(String item, String dropdown) {
        selectItemFromDropdown(item, dropdown, null);
    }

    public void selectItemFromDropdown(String item, WebElement dropdown) {
        selectItemFromDropdown(item, "", dropdown);
    }

    public void validateTemplate(String templateName, String method, String baseline) throws Exception {
        delaySync(2);

        switch (method.toUpperCase()) {
            case "HTML_SOURCE":
                String filePath = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "template", baseline});
                String expected = helper.readTextFile(filePath).replace(System.getProperty("line.separator"), "");
                String actual = getElement(templateName).getAttribute("outerHTML").trim().replace(System.getProperty("line.separator"), "");

                filePath = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "template", "iFrameTemplate.html"});
                String fileWithPath = csUtil.getFullPathFromFragments(new String[]{"target", "site", "serenity", "iFrameTemplate.html"});
                File destFile = new File(fileWithPath);

                try {
                    FileUtils.copyFile(new File(filePath), destFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CompareUtil compareUtil = new CompareUtil();
                if (compareUtil.diff_levenshtein(compareUtil.diff_main(expected, actual)) > 0) { // failed
                    takeScreenshot();
                    String origin = helper.readTextFile(fileWithPath);
                    origin = String.format(origin, compareUtil.diff_prettyHtml(compareUtil.diff_main(expected, actual)));
                    helper.writeToTextFile(fileWithPath, origin);
                    helper.writeLogToReport("<iframe src='iFrameTemplate.html' style='background: #FFFFFF;width: 300%; height: 300%'>", "");
                }
                Assert.assertEquals(0, compareUtil.diff_levenshtein(compareUtil.diff_main(expected, actual)));
                break;
            case "SUB_IMAGE":
                String source = ScreenshotUtil.takeSnapShot(driver.get(), "source");
                String target = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "template", baseline});

                if (!ImageUtil.findSubImageInImage(target, source)) {
                    ImageUtil.exportFailureToReport(source, target);
                } else {
                    Assert.assertTrue(true);
                }
                break;

            default:
                break;
        }
    }

    public void waitForPageLoad(int timeout) {
        waitForPageLoaded(timeout);
    }
}
