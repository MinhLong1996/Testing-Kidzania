package core.util;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.IOException;
import java.io.PrintWriter;

public class PdfUtil {
    /**
     * Parses a PDF to a plain text file.
     *
     * @param pdf the original PDF
     * @param txt the resulting text
     * @throws IOException
     */
    public static String parsePdf(String pdf, String txt) throws IOException {
        StringBuilder result = new StringBuilder();
        PdfReader reader = new PdfReader(pdf);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        PrintWriter out = new PrintWriter(txt,"UTF-8");
        TextExtractionStrategy strategy;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
            out.println(strategy.getResultantText());
            result.append(strategy.getResultantText());
        }
        
        out.flush();
        out.close();
        reader.close();
        return result.toString();
    }
}
