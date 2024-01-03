package rd.pdfsearch.model;

import java.util.List;

/**
 * This class is used to cache text content of a pdf file
 * @param pagesContent text content, element in this list represents a document page
 * @param fileIdentity is used to check if file was changed and pagesContent should be updated
 * @param lastAccessTime is used to track when this record was used last time
 */
public record CachedPdfFile(List<String> pagesContent, FileIdentity fileIdentity, long lastAccessTime) {
}
