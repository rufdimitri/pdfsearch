package rd.pdfsearch;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import rd.pdfsearch.model.SearchResult;
import rd.pdfsearch.model.WordPosition;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class PDFSearchRequest {
    private final BlockingQueue<String> outputQueue;
    private final BlockingQueue<Throwable> errorQueue;

    public PDFSearchRequest(BlockingQueue<String> outputQueue, BlockingQueue<Throwable> errorQueue) {
        this.outputQueue = outputQueue;
        this.errorQueue = errorQueue;
    }

    /**
     * @param path path where to search for files
     * @param fileExtension filter: only search in files with this extension (e.g  ".pdf"). Leave empty string to ignore this parameter
     * @param keywords list of keywords to search for
     */
    public void searchInMultipleFiles(String path, String fileExtension, Collection<String> keywords) {
        try {
            Files.walkFileTree(Paths.get(path), new FileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        if (!Files.isRegularFile(file) || !file.toString().endsWith(fileExtension))
                            return FileVisitResult.CONTINUE;

                        outputQueue.put("file = " + file.toString());

                        SearchResult searchResult;
                        try {
                            searchResult = searchInPdf(file.toString(), keywords);
                        } catch (Exception exception) {
                            errorQueue.put(exception);
                            return FileVisitResult.CONTINUE;
                        }

                        outputQueue.put(String.format("found %d entries \n", searchResult.searchResultsPerWord().size()));

                        for (Map.Entry<String, List<WordPosition>> wordSearchResult : searchResult.searchResultsPerWord().entrySet()) {
                            outputQueue.put("found word: " + wordSearchResult.getKey());
                            for (WordPosition wordPosition : wordSearchResult.getValue()) {
                                outputQueue.put("  at " + wordPosition.getAbsolutePosition() + " page #: " + wordPosition.pageNumber());
                            }
                        }

                        return FileVisitResult.CONTINUE;
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    System.err.println(exc.toString() + " / At file: " + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SearchResult searchInPdf(String filename, Collection<String> keywords) {
        Map<String,List<WordPosition>> wordPositionsPerWord = new HashMap<>();
        File src = new File(filename);
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
                        List<WordPosition> searchResults = wordPositionsPerWord.getOrDefault(keyword, new ArrayList<>());
                        searchResults.add(new WordPosition(position, pagePosition, pageNr));
                        wordPositionsPerWord.putIfAbsent(keyword, searchResults);
                    }
                }
                pagePosition += pageText.length();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new SearchResult(filename, wordPositionsPerWord);
    }
}
