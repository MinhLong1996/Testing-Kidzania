package pages;

import core.util.FileUtil;
import core.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class QuotationPage extends WebDriverUtil {
    public boolean isFileDownloaded(String folder, String fileName, int fileSize, int timeout) {
        final String fileNameCI = "download";
        do {
            if (FileUtil.isExist(folder, fileName)) {
                long size = FileUtil.size(folder, fileName);
                logger.info("Size of {} is {}", fileName, size);
                return size > fileSize;
            } else {
                if (FileUtil.isExist(folder, fileNameCI)) {
                    long size = FileUtil.size(folder, fileNameCI);
                    logger.info("Size of {} is {}", fileNameCI, size);
                    return size > fileSize;
                }
                logger.info("Waiting for file is downloaded: {}", timeout--);
                delaySync(1);
            }

        } while (timeout > 0);

        return false;
    }

    public List<List<String>> getFullDataTable() {
        List<List<String>> ret = new LinkedList<>();
        WebElement table = element("div.el-table__body-wrapper > table.el-table__body");
        WebElement jumperElement = element("div.pagination__jumper input");
        WebElement totalElement = element("div.pagination__total");
        int total = Integer.parseInt(getText(totalElement).split("ページ")[0].replace("/", "").trim());
        for (int page = 1; page <= total; page++) {
            jumperElement.clear();
            jumperElement.sendKeys(String.valueOf(page));
            pressKey("ENTER");
            waitForPageLoaded();
            delaySync(2);

            List<String> headers = getTextFromElements("table.el-table__header thead th span:first-child");
            ret.add(headers);

            List<WebElement> dataElements = waitForNestedElementsPresent(table, xpathOrCss(".//tr[not(contains(@style,'display: none'))]"), TIMEOUT_SMALL);

            List<Integer> removedCols = new LinkedList<>();
            List<WebElement> elements = dataElements.get(0).findElements(By.cssSelector("td:not(.last-td)"));
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).getAttribute("class").contains("el-table-column--selection")) {
                    removedCols.add(i);
                }
            }

            dataElements.forEach(e -> {
                List<String> row = Arrays.stream(e.getAttribute("innerText").split("\t"))
                        .map(String::trim).collect(Collectors.toList());
                removedCols.forEach(col -> row.remove(Integer.parseInt(String.valueOf(col))));
                ret.add(row);
            });
        }

        return ret;
    }
}
