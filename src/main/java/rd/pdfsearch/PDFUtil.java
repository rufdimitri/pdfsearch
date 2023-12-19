package rd.pdfsearch;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.util.*;

public class PDFUtil {

    //TODO method to search recursive in a folder

    //TODO save filename in search results
    public static Map<String,List<SearchResult>> searchInPdf(String fileName, Collection<String> keywords) {
        Map<String,List<SearchResult>> map = new HashMap<>();
        File src = new File(fileName);
        try (PdfReader pdfReader = new PdfReader(src.getAbsolutePath())) {
            int pageCount = pdfReader.getNumberOfPages();
            PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfReader);
            int pagePosition = 0;
            for (int pageNr = 1; pageNr <= pageCount; pageNr++) {
                String pageText = pdfTextExtractor.getTextFromPage(pageNr)
                        .toLowerCase();

                for (String keyword : keywords) {
                    int position = pageText.indexOf(keyword);
                    if (position >= 0) {
                        List<SearchResult> searchResults = map.getOrDefault(keyword, new ArrayList<>());
                        searchResults.add(new SearchResult(position, pagePosition, pageNr));
                        map.putIfAbsent(keyword, searchResults);
                    }
                }
                pagePosition += pageText.length();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }
}
