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

                        outputQueue.put(String.format("found %d entries \n", searchResult.searchResults().size()));

                        for (SearchScope scope : searchResult.searchResults()) {
                            for (WordPosition position : scope.getWordPositions()) {
                                outputQueue.put(String.format("Found '%s' at page %d", position.word(), position.pageNumber()));
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
     * @return List of SearchScope objects
     */
    public List<SearchScope> searchInContents(List<String> contents, SearchCriteria searchCriteria) {
        int pagePosition = 0;
        int pageNr = 1;
        List<WordPosition> searchResults = new ArrayList<>();
        for (String pageText : contents) {
            String pageTextLowerCase = pageText.toLowerCase();

            for (String word : searchCriteria.getKeywords()) {
                String wordLowerCase = word.toLowerCase();
                int position = pageTextLowerCase.indexOf(wordLowerCase);
                if (position < 0) continue;

                searchResults.add(new WordPosition(word, position, pagePosition, pageNr));
            }

            pagePosition += pageText.length();
            pageNr++;
        }
        searchResults.sort(Comparator.comparingInt(WordPosition::getAbsolutePosition));

        List<SearchScope> scopes = new ArrayList<>();

        switch(searchCriteria.getWordScopeType()) {
            case DOCUMENT: {
                SearchScope scope = new SearchScope(0, Integer.MAX_VALUE);
                scope.getWordPositions().addAll(searchResults);
                scopes.add(scope);
                scopes.removeIf(scopeN -> !searchCriteria.getKeywords().stream().allMatch(scopeN::contains));
                return scopes;
            }
            case RANGE: {
                searchResults.forEach(searchResult -> {
                    SearchScope scope = new SearchScope(searchResult.getAbsolutePosition(), searchCriteria.getRangeSize());
                    scopes.add(scope);
                    scopes.stream()
                            .filter(scopeN ->
                                scopeN.getStartPosition() <= searchResult.getAbsolutePosition()
                                    && scopeN.getEndPosition() >= searchResult.getAbsolutePosition())
                            .forEach(scopeN -> scopeN.getWordPositions().add(searchResult));
                });

                scopes.removeIf(scopeN -> !searchCriteria.getKeywords().stream().allMatch(scopeN::contains));

                return scopes;
            }
            default: {
                throw new RuntimeException("Not implemented SearchScope type: " + searchCriteria.getWordScopeType());
            }
        }
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
