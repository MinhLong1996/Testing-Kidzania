package core.util;

import core.helper.LogHelper;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class ScreenshotUtil {
    private static final LogHelper logger = LogHelper.getInstance();

    public static String takeSnapShot(WebDriver driver, String fileName) {
        CustomStringUtil csUtil = new CustomStringUtil();
        String filePath = csUtil.getFullPathFromFragments(new String[]{"target", "site", "serenity", fileName + ".png"});
        takeScreenshot(driver,filePath);
        return filePath;
    }

    private static void takeScreenshot(WebDriver driver, String filePath) {
        TakesScreenshot scrShot = (TakesScreenshot) driver;
        File scrFile = scrShot.getScreenshotAs(OutputType.FILE);
        File destFile = new File(filePath);

        try {
            FileUtils.copyFile(scrFile, destFile);
            logger.info("Screenshot saved: {}", filePath);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
