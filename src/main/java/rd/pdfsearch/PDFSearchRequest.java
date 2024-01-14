package rd.pdfsearch;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import rd.pdfsearch.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class PDFSearchRequest {
    private final BlockingQueue<String> outputQueue;
    private final BlockingQueue<Throwable> errorQueue;
    private Map<Integer,List<CachedPdfFile>> cachedFilesPerFileIdentityHashCode;

    public PDFSearchRequest(BlockingQueue<String> outputQueue, BlockingQueue<Throwable> errorQueue, Map<Integer,List<CachedPdfFile>> cachedFilesPerFileIdentityHashCode) {
        this.outputQueue = Objects.requireNonNull(outputQueue);
        this.errorQueue = Objects.requireNonNull(errorQueue);
        this.cachedFilesPerFileIdentityHashCode = Objects.requireNonNull(cachedFilesPerFileIdentityHashCode);
    }

    public PDFSearchRequest(BlockingQueue<String> outputQueue, BlockingQueue<Throwable> errorQueue) {
        this.outputQueue = Objects.requireNonNull(outputQueue);
        this.errorQueue = Objects.requireNonNull(errorQueue);
        this.cachedFilesPerFileIdentityHashCode = new HashMap<>();
    }

    /**
     * @param path path where to search for files
     * @param fileExtension filter: only search in files with this extension (e.g  ".pdf"). Leave empty string to ignore this parameter
     * @param searchCriteria
     */
    public void searchInMultipleFiles(String path, String fileExtension, SearchCriteria searchCriteria) {
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

                        SearchResult searchResult;
                        try {
                            List<String> pagesContent;
                            CachedPdfFile cachedPdfFile = getCachedPdfFile(file);
                            if (cachedPdfFile == null) {
                                outputQueue.put("file = " + file.toString());
                                pagesContent = getPdfPagesContent(file);
                                cachePdfFile(pagesContent, file);
                            } else {
                                outputQueue.put("file = " + file.toString() + " (cached version)");
                                pagesContent = cachedPdfFile.pagesContent();
                            }

                            searchResult = new SearchResult(file.toString(), searchInContents(pagesContent, searchCriteria));
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

        List<CachedPdfFile> cachedPdfFiles = cachedFilesPerFileIdentityHashCode.getOrDefault(fileIdentity.hashCode(), Collections.emptyList());
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
        List<CachedPdfFile> cachedFilesList = cachedFilesPerFileIdentityHashCode.getOrDefault(cachedPdfFile.hashCode(), new ArrayList<>());
        cachedFilesList.add(cachedPdfFile);
        cachedFilesPerFileIdentityHashCode.put(cachedPdfFile.fileIdentity().hashCode(), cachedFilesList);
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
     * @param searchCriteria
     * @return Map of Positions per Word
     */
    public Map<String,List<WordPosition>> searchInContents(List<String> contents, SearchCriteria searchCriteria) {
        Map<String,List<WordPosition>> positionsPerWord = new HashMap<>();

        int pagePosition = 0;
        int pageNr = 1;
        for (String pageText : contents) {
            String pageTextLow = pageText.toLowerCase();

            for (String keyword : searchCriteria.getKeywords()) {
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

        //did not find all words in this Document
        if (positionsPerWord.size() < searchCriteria.getKeywords().size()) return Collections.emptyMap();
        if (searchCriteria.getWordScopeType() == SearchCriteria.WordScopeType.RANGE) {
            int rangeSize = searchCriteria.getRangeSize();
            for (Map.Entry<String, List<WordPosition>> entry1 : positionsPerWord.entrySet()) {
                String word1 = entry1.getKey();
                for (WordPosition wordPosition1 : entry1.getValue()) {
                    for (Map.Entry<String, List<WordPosition>> entry2 : positionsPerWord.entrySet()) {
                        String word2 = entry2.getKey();
                        for (WordPosition wordPosition2 : entry2.getValue()) {
                            if (!word1.equals(word2) || true) {  //TODO fix debug condition
                                System.out.format("word1: %s pos: %d / word2: %s pos2: %d / diff: %d\n",
                                        word1, wordPosition1.getAbsolutePosition(), word2, wordPosition2.getAbsolutePosition(),
                                        Math.abs(wordPosition1.getAbsolutePosition() - wordPosition2.getAbsolutePosition()));
                            }
                        }
                    }
                }


            }
            //TODO test and compare with for-each
            System.out.println("---------\nstream implementation");
            positionsPerWord.values().stream().flatMap(Collection::stream).forEach(wordPosition1 -> {
                positionsPerWord.values().stream().flatMap(Collection::stream)
                    .filter(wordPosition2 -> wordPosition2 != wordPosition1)
                    .forEach(wordPosition2 -> {
                        System.out.format("pos: %d / pos2: %d / diff: %d\n",
                                wordPosition1.getAbsolutePosition(), wordPosition2.getAbsolutePosition(),
                                Math.abs(wordPosition1.getAbsolutePosition() - wordPosition2.getAbsolutePosition()));
                    });
            });
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
