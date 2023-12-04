package core.helper;

import core.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DropDownHelper extends WebDriverUtil {
    private String formatXpath = null;
    private final WebElement dropdownBtn;
    private WebElement dropdownCtn = null;


    public DropDownHelper(WebElement dropdownBtn, String formatXpath) {
        this.dropdownBtn = dropdownBtn;
        this.formatXpath = formatXpath;
    }

    public DropDownHelper(WebElement dropdownBtn, WebElement dropdownCtn) {
        this.dropdownBtn = dropdownBtn;
        this.dropdownCtn = dropdownCtn;
    }

    public DropDownHelper(WebElement dropdownBtn) {
        this.dropdownBtn = dropdownBtn;
    }

    private WebElement optionElement(String option) {
        By by = optionBy(option);
        if (null != formatXpath) {
            return element(optionBy(option));
        } else {
            WebElement element = null != dropdownCtn ? dropdownCtn : dropdownBtn;
            return waitForNestedElementPresent(element, by);
        }
    }

    private By optionBy(String option) {
        if (null != formatXpath) {
            return By.xpath(String.format(formatXpath
                    .replace("'%s'", "%s")
                    .replace("\"%s\"", "%s"), xpathExpression(option)));
        } else {
            String childXpath = String.format(".//*[normalize-space(text())=%s]", xpathExpression(option));
            return By.xpath(childXpath);
        }
    }

    public void select(List<?> options) {
        clickOnElement(dropdownBtn);
        options.stream().map(String::valueOf).forEach(option -> {
            WebElement element = optionElement(option);
            clickOnElement(element);
            delaySync(0.5);
        });
        if (options.size() > 1) {
            pressKey("ESC");
            delaySync(0.5);
        }
    }

    public void select(String[] options) {
        select(Arrays.asList(options));
    }

    public void select(String option) {
        select(Collections.singletonList(option));
    }

    public boolean isExist(String option, int timeout) {
        clickOnElement(dropdownBtn);
        By by = optionBy(option);
        boolean result = isElementPresent(by, timeout);
        pressKey("ESC");
        delaySync(0.5);
        return result;
    }

    public boolean isExist(String option) {
        return isExist(option, 3);
    }
}
