package rd.pdfsearch;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFUtil {

    public static Map<String,SearchResult> searchInPdf(String fileName, List<String> keywords) {
        Map<String,SearchResult> map = new HashMap<>();
        File src = new File(fileName);
        try (PdfReader pdfReader = new PdfReader(src.getAbsolutePath())) {
            int pageCount = pdfReader.getNumberOfPages();
            PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfReader);
            int charCount = 0;
            for (int pageId = 0; pageId < pageCount; pageId++) {
                String pageText = pdfTextExtractor.getTextFromPage(pageId);
                for (String keyword : keywords) {
                    int pos = pageText.indexOf(keyword);
                    if (pos >= 0) {
                        map.put(keyword, new SearchResult(charCount + pos, pageId+1));
                    }
                }
                charCount += pageText.length();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }
}
