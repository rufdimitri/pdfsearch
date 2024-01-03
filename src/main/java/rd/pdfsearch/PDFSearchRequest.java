package rd.pdfsearch;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import rd.pdfsearch.model.CachedPdfFile;
import rd.pdfsearch.model.FileIdentity;
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
    private final Map<Integer,List<CachedPdfFile>> cachedFilePerHashCode;

    public PDFSearchRequest(BlockingQueue<String> outputQueue, BlockingQueue<Throwable> errorQueue, Map<Integer,List<CachedPdfFile>> cachedFilePerHashCode) {
        this.outputQueue = outputQueue;
        this.errorQueue = errorQueue;
        this.cachedFilePerHashCode = cachedFilePerHashCode;
    }

    public PDFSearchRequest(BlockingQueue<String> outputQueue, BlockingQueue<Throwable> errorQueue) {
        this.outputQueue = outputQueue;
        this.errorQueue = errorQueue;
        this.cachedFilePerHashCode = new HashMap<>();
    }

    /**
     * @param path path where to search for files
     * @param fileExtension filter: only search in files with this extension (e.g  ".pdf"). Leave empty string to ignore this parameter
     * @param keywords list of keywords to search for
     */
    public void searchInMultipleFiles(String path, String fileExtension, List<String> keywords) {
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
                            List<String> pagesContent;
                            CachedPdfFile cachedPdfFile = getCachedPdfFile(file);
                            if (cachedPdfFile == null) {
                                pagesContent = getPdfPagesContent(file);
                                cachePdfFile(pagesContent, file);
                            } else {
                                pagesContent = cachedPdfFile.pagesContent();
                            }

                            searchResult = new SearchResult(file.toString(), searchInContents(pagesContent, keywords));
                        } catch (Exception exception) {
                            errorQueue.put(exception);
                            exception.printStackTrace();
                            System.out.println("errorQueue: " + errorQueue.size());
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
                    try {
                        errorQueue.put(exc);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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

    /** Finds cached file in cachedFiles list, returns null if not found
     * @return CachedFile that has the same FileIdentity, or returns null if not found
     */
    private CachedPdfFile getCachedPdfFile(Path file) {
        FileIdentity fileIdentity = getFileIdentity(file);

        List<CachedPdfFile> cachedPdfFiles = cachedFilePerHashCode.getOrDefault(fileIdentity.hashCode(), Collections.emptyList());
        for (CachedPdfFile cachedFile : cachedPdfFiles) {
            if (cachedFile.fileIdentity().equals(fileIdentity))
                return cachedFile;
        }
        return null;
    }

    /**
     * Creates cache entry for this file
     * @param pagesContent
     * @param file
     */
    private void cachePdfFile(List<String> pagesContent, Path file) {
        CachedPdfFile cachedPdfFile = new CachedPdfFile(pagesContent, getFileIdentity(file), System.currentTimeMillis());
        List<CachedPdfFile> cachedFilesList = cachedFilePerHashCode.getOrDefault(cachedPdfFile.hashCode(), new ArrayList<>());
        cachedFilesList.add(cachedPdfFile);
        cachedFilePerHashCode.put(cachedPdfFile.hashCode(), cachedFilesList);
    }

    private FileIdentity getFileIdentity(Path file) {
        return new FileIdentity(
                file.getFileName().toString(),
                file.getParent().getFileName().toString(),
                file.toFile().length(),
                file.toFile().lastModified()
        );
    }

    /**
     * Searches for keywords in contents, case-insensitive
     * @param contents List of pages
     * @param keywords List of keywords
     * @return Map of Positions per Word
     */
    public Map<String,List<WordPosition>> searchInContents(List<String> contents, List<String> keywords) {
        Map<String,List<WordPosition>> positionsPerWord = new HashMap<>();

        int pagePosition = 0;
        int pageNr = 1;
        for (String pageText : contents) {
            String pageTextLow = pageText.toLowerCase();

            for (String keyword : keywords) {
                String keywordLow = keyword.toLowerCase();
                int position = pageTextLow.indexOf(keywordLow);
                if (position >= 0) {
                    List<WordPosition> searchResults = positionsPerWord.getOrDefault(keyword, new ArrayList<>());
                    searchResults.add(new WordPosition(position, pagePosition, pageNr));
                    positionsPerWord.putIfAbsent(keyword, searchResults);
                }
            }

            pagePosition += pageText.length();
            pageNr++;
        }

        return positionsPerWord;
    }

    /**
     * @param file Path object - can be easy created with Paths.get(filename)
     * @return text contents of a file. Each element in a list represents a page in document
     */
    public List<String> getPdfPagesContent(Path file) {
        File src = file.toFile();

        try (PdfReader pdfReader = new PdfReader(src.getAbsolutePath())) {
            int pageCount = pdfReader.getNumberOfPages();
            PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfReader);
            List<String> contents = new ArrayList<>(pageCount);

            for (int pageNr = 1; pageNr <= pageCount; pageNr++) {
                String pageText = pdfTextExtractor.getTextFromPage(pageNr)
                        .toLowerCase();

                contents.add(pageText);
            }
            return contents;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
