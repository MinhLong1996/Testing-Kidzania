package core.util;

import core.helper.Helper;
import core.helper.LogHelper;
import marvin.image.MarvinImage;
import marvin.image.MarvinSegment;
import marvin.io.MarvinImageIO;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static marvinplugins.MarvinPluginCollection.findSubimage;

public class ImageUtil {
    protected static final LogHelper logger = LogHelper.getInstance();
    private static final Helper helper = new Helper();
    private static final CustomStringUtil csUtil = new CustomStringUtil();
    private static long difference = 0;
//    private static JFrame f;
//    private static JProgressBar b;

    public static boolean findSubImageInImage(String subImage, String image) {
        CustomStringUtil csUtil = new CustomStringUtil();
        Helper helper = new Helper();
        MarvinImage mainImg = MarvinImageIO.loadImage(image);
        MarvinImage subImg = MarvinImageIO.loadImage(subImage);

        MarvinSegment seg1;
        seg1 = findSubimage(subImg, mainImg, 0, 0);

        if (null == seg1)
            return false;
        else {
            String key = "screenshotID";
            int ssId = Integer.parseInt(Objects.requireNonNull(helper.readGlobalParam(key)));
            int nextSSID = ssId + 1;

            String fileName = "CompareResult_" + ssId + ".png";
            drawRect(mainImg, seg1.x1, seg1.y1, seg1.x2 - seg1.x1, seg1.y2 - seg1.y1);
            String output = csUtil.getFullPathFromFragments(new String[]{"target", "site", "serenity", fileName});
            MarvinImageIO.saveImage(mainImg, output);
            helper.writeLogToReport("<img src='" + fileName + "'>", fileName.replace(".png", ""));
            helper.writeGlobalParam(key, String.valueOf(ssId), String.valueOf(nextSSID));
            return true;
        }
    }

    public static void exportFailureToReport(String source, String target) throws IOException {
        WebDriverUtil driverUtil = new WebDriverUtil();
        // create a frame
//        f = new JFrame("Exporting failure...");
//        // create a panel
//        JPanel p = new JPanel();
//        // create a progressbar
//        b = new JProgressBar();
//        // set initial value
//        b.setValue(0);
//        b.setStringPainted(true);
//        // add progressbar
//        p.add(b);
//        // add panel
//        f.add(p);
//        // set the size of the frame
//        f.setSize(300, 100);
//        f.setVisible(true);
//        f.setLocationRelativeTo(null);

        String downloadDir = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "download"});
        FileUtils.cleanDirectory(new File(downloadDir));
        String icURL = helper.getConfig("env.image.compare.url");
        String execMode = helper.getConfig("env.custom.driver.exec.mode");
        WebDriver customDriver = driverUtil.getCustomDriver(execMode, false);
        WebDriverUtil dU = new WebDriverUtil(customDriver);
        WebElement element;
//        b.setValue(10);

        dU.goToURL(icURL);
        dU.assertPageShowUp("IC_HOME_PAGE");
        dU.delaySync(1);
        element = dU.getElement("IC_HOME_PAGE_SOURCE_IMAGE_INPUT");
        element.sendKeys(source);
        dU.delaySync(5);
//        b.setValue(20);
        element = dU.getElement("IC_HOME_PAGE_TARGET_IMAGE_INPUT");
        dU.executeScript("arguments[0].setAttribute('data-click', '1')", element);
        dU.delaySync(1);
        element.sendKeys(target);
        dU.delaySync(5);
//        b.setValue(30);
        dU.clickElement("IC_HOME_PAGE_COMPARE_BUTTON");
        dU.delaySync(10);
//        b.setValue(50);
        dU.clickElementByJS("IC_HOME_PAGE_VISUALISATION_BUTTON");
        dU.delaySync(3);
//        b.setValue(70);
        String locator = dU.getElementLocator("IC_HOME_PAGE_VISUALISATION_DROPDOWN");
        locator = locator + "//child::li[1]";
        dU.getElementByXPath(locator).click();
        dU.delaySync(1);
        dU.clickElement("IC_HOME_PAGE_DOWNLOAD_BUTTON");
//        b.setValue(80);
        dU.delaySync(5);
        dU.terminate();
//        b.setValue(100);
//        f.setVisible(false); //you can't see me!
//        f.dispose();

        String output = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "download", "image_compare_visualisation.png"});
        helper.embedScreenshot(output, "image_compare_visualisation");
        helper.writeStepFailed();
    }

    private static void drawRect(MarvinImage image, int x, int y, int width, int height) {
        x -= 4;
        y -= 4;
        width += 8;
        height += 8;
        image.drawRect(x, y, width, height, Color.red);
        image.drawRect(x + 1, y + 1, width - 2, height - 2, Color.red);
        image.drawRect(x + 2, y + 2, width - 4, height - 4, Color.red);
    }

    public static boolean compareTwoImages(String expectedImgPath, String actualImgPath) {
        return compareTwoImages(expectedImgPath, actualImgPath, "");
    }

    public static boolean compareTwoImages(String expectedImgPath, String actualImgPath, String resultDir) {
        try {
            double percentage = getDiffPercentAndCreateCompareImage(expectedImgPath, actualImgPath, resultDir);
            return (percentage < 0.1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static double getDiffPercentAndCreateCompareImage(String expectedPath, String actualPath, String resultDir) throws IOException {
        BufferedImage expectedImg = ImageIO.read(new File(expectedPath));
        BufferedImage actualImg = ImageIO.read(new File(actualPath));

        difference = 0;

        int widthBase = expectedImg.getWidth();
        int widthChange = actualImg.getWidth();
        int heightBase = expectedImg.getHeight();
        int heightChange = actualImg.getHeight();

        if ((widthBase != widthChange) || (heightBase != heightChange)) {
            logger.debug("Error: baseline: {} x {} - compared {} x {}", widthBase, heightBase, widthChange, heightChange);
            logger.debug("Resize the actual image to the expected image size");
            actualImg = resizeImage(actualImg, widthBase, heightBase);
        }

        BufferedImage imgResult = new BufferedImage(widthBase, heightBase, BufferedImage.TYPE_4BYTE_ABGR);
        compareRGBAllPixel(imgResult, expectedImg, actualImg);
        double totalPixels = (double) widthBase * heightBase * 3;
        double avgDifferentPixels = (double) difference / totalPixels;
        double percentage = (avgDifferentPixels / 255) * 100;

        logger.debug("Difference percentage: {}", percentage);

        if (percentage > 0 && !resultDir.isEmpty()) {
            Path resultDirPath = Paths.get(resultDir);
            if (!Files.exists(resultDirPath)) {
                Files.createDirectories(resultDirPath);
            }

            File file = new File(expectedPath);
            int pos = file.getName().lastIndexOf('.');

            String fileName = String.format("%s_Result_%s.png", file.getName().substring(0, pos)
                    , String.format("%1$,.4f", percentage).replace(".", "_"));
            String path = resultDir + File.separator + fileName;
            createPngImage(imgResult, path);
            logger.info("Result image: {}", path);
        }

        return percentage;
    }

    private static void compareRGBAllPixel(BufferedImage imgResult, BufferedImage expectedImg, BufferedImage actualImg) {
        for (int y = 0; y < expectedImg.getHeight(); y++) {
            for (int x = 0; x < expectedImg.getWidth(); x++) {
                int rgbExp = expectedImg.getRGB(x, y);
                int rgbAct = actualImg.getRGB(x, y);

                if (rgbExp == rgbAct) {
                    imgResult.setRGB(x, y, rgbAct);
                } else {
                    int redA = (rgbExp >> 16) & 0xff;
                    int greenA = (rgbExp >> 8) & 0xff;
                    int blueA = (rgbExp) & 0xff;
                    int redB = (rgbAct >> 16) & 0xff;
                    int greenB = (rgbAct >> 8) & 0xff;
                    int blueB = (rgbAct) & 0xff;

                    difference += Math.abs(redA - redB) + Math.abs(greenA - greenB) + Math.abs(blueA - blueB);
                    int modifiedRGB = new Color(255, 0, 0).getRGB();
                    imgResult.setRGB(x, y, modifiedRGB);
                }
            }
        }
    }

    public static BufferedImage resizeImage(final Image image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }

    public static void createPngImage(BufferedImage image, String filePath) {
        try {
            ImageIO.write(image, "PNG", new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

