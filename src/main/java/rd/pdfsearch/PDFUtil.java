package rd.pdfsearch;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PDFUtil {
    //TODO method to search recursive in a folder

    /**
     *
     * @param path path where to search for files
     * @param fileExtension filter: only search in files with this extension (e.g  ".pdf"). Leave empty string to ignore this parameter
     * @param keywords list of keywords to search for
     */
    public static void searchInMultipleFiles(String path, String fileExtension, Collection<String> keywords) {
        try (Stream<Path> stream = Files.walk(Paths.get(path))) {
            List<String> files = stream.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(string -> string.endsWith(fileExtension))
                    .toList();
            //TODO foreach files
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
