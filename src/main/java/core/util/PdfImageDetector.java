package core.util;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import core.helper.LogHelper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PdfImageDetector implements RenderListener {
    protected static LogHelper logger = LogHelper.getInstance();
    private static final CustomStringUtil cs = new CustomStringUtil();
    private final List<String> paths = new ArrayList<>();
    private String extractedFolder = cs.getFullPathFromFragments(new String[]{"src", "test", "resources", "images", "extracted"});


    public PdfImageDetector() {
        cleanExtractedFolder();
    }

    public PdfImageDetector(String extractedFolder) {
        this.extractedFolder = extractedFolder;
    }

    public void beginTextBlock() {
    }

    public void endTextBlock() {
    }

    public void renderText(TextRenderInfo renderInfo) {
    }

    public void renderImage(ImageRenderInfo renderInfo) {
        try {
            BufferedImage img = renderInfo.getImage().getBufferedImage();
            String pathname = extractedFolder + File.separator + UUID.randomUUID() + ".png";
            ImageUtil.createPngImage(img, pathname);
            paths.add(pathname);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void cleanExtractedFolder() {
        FileUtil.cleanDirectory(extractedFolder);
    }

    public List<String> getPaths() {
        return paths;
    }
}
