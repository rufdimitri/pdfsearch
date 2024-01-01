package rd.pdfsearch.model;

import java.util.List;
import java.util.Map;

public record SearchResult(String filename, Map<String, List<WordPosition>> searchResultsPerWord) {
}
